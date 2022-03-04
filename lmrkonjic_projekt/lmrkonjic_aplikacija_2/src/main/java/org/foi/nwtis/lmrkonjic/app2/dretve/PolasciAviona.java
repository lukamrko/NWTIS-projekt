package org.foi.nwtis.lmrkonjic.app2.dretve;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.lmrkonjic.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.lmrkonjic.app2.podaci.AirplanesDAO;
import org.foi.nwtis.lmrkonjic.app2.podaci.MyAirportDAO;
import org.foi.nwtis.lmrkonjic.app2.podaci.MyAirportsLogDAO;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;

public class PolasciAviona extends Thread
{

    private PostavkeBazaPodataka pbp;
    private OSKlijent osk;
    private String pocetakPreuzimanja;
    private String krajPreuzimanja;
    private int trajanjeCiklusa;
    private int trajanjePauze;
    private boolean kraj = false;
    private String osKorisnik;
    private String osZaporka;
    private List<Aerodrom> aerodromi;
    private MyAirportDAO myAirportDAO;
    private MyAirportsLogDAO myAirportsLogDAO;
    private AirplanesDAO airplanesDao;

    /**
     * Konstruktor koji omoguđuje pristup PostavkeBazaPodataka
     *
     * @param pbp - PostavkeBazaPodataka
     */
    public PolasciAviona(PostavkeBazaPodataka pbp)
    {
        this.pbp = pbp;
    }

    /**
     * Funckija koja omogućuje pravilno gašenje dretve
     */
    @Override
    public void interrupt()
    {
        kraj = true;
        super.interrupt();
    }

    /**
     * Funckija koja započinje rad dretve. Uz to puni varijable podacim
     */
    @Override
    public synchronized void start()
    {
        boolean status = Boolean.parseBoolean(pbp.dajPostavku("aerodrom.preuzimanje.status"));
        if (!status)
        {
            System.out.println("Ne preuzimam podatke!");
            interrupt();
        }
        this.pocetakPreuzimanja = pbp.dajPostavku("aerodrom.preuzimanje.pocetak");
        this.krajPreuzimanja = pbp.dajPostavku("aerodrom.preuzimanje.kraj");
        this.trajanjeCiklusa = Integer.parseInt(pbp.dajPostavku("aerodrom.preuzimanje.ciklus"));
        this.trajanjePauze = Integer.parseInt(this.pbp.dajPostavku("aerodrom.preuzimanje.pauza"));
        this.osKorisnik = this.pbp.dajPostavku("OpenSkyNetwork.korisnik");
        this.osZaporka = this.pbp.dajPostavku("OpenSkyNetwork.lozinka");
        this.myAirportDAO = new MyAirportDAO();
        this.aerodromi = myAirportDAO.dohvatiSveAerodrome(pbp);
        this.myAirportsLogDAO = new MyAirportsLogDAO();
        this.airplanesDao = new AirplanesDAO();
        System.out.println(this.osKorisnik + " " + this.osZaporka);
        this.osk = new OSKlijent(this.osKorisnik, this.osZaporka);
        super.start();
    }

    /**
     * Funkcija koja pokreće preuzimanje podataka.
     */
    @Override
    public void run()
    {
        System.out.println("Krenuli preuzimati podatke!");
        while (!kraj)
        {
            System.out.println("Preuzimanje podataka: ");
            try
            {
                for (Aerodrom aerodrom : aerodromi)
                {
                    if (preuzmiPodatke(aerodrom))
                    {
                        Thread.sleep(trajanjeCiklusa * 1000);
                    }
                }
                Thread.sleep(86400000);

            } catch (InterruptedException | ParseException ex)
            {
                Logger.getLogger(PolasciAviona.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Preuzimanje podataka završilo!");

    }

    /**
     * Funkcija koja preuzima podatke. Radi na principu da ucita sve podatke za pojedinacni aerodrom
     *
     * @param aerodrom - Aerodrom za kojeg treba preuzesti podatke
     * @return - vraća informaciju o tome je li funkcija preuzela ikakve podatke
     * @throws ParseException - u slučaje da ne može parsirati string u date baca iznimku
     */
    private boolean preuzmiPodatke(Aerodrom aerodrom) throws ParseException
    {
        boolean dodan = false;
        Date pocetniDatumD = new SimpleDateFormat("dd.MM.yyyy").parse(this.pocetakPreuzimanja);
        Date krajnjiDatumD = new SimpleDateFormat("dd.MM.yyyy").parse(this.krajPreuzimanja);

        long vrijemePocetno = pocetniDatumD.getTime() / 1000;
        long vrijemeZavrsno = krajnjiDatumD.getTime() / 1000;
        while (vrijemePocetno < vrijemeZavrsno)
        {
            if (provjeriZapisMyAirportsLog(aerodrom.getIcao(), vrijemePocetno))
            {
                vrijemePocetno += 86400;
                continue;
            }
            System.out.println(vrijemePocetno);
            List<AvionLeti> avionLeti = this.osk.getDepartures(aerodrom.getIcao(),
                    vrijemePocetno, vrijemePocetno + 86400);
            if (!avionLeti.isEmpty())
            {
                spremiAvione(avionLeti);
                spremiAerodrom(aerodrom, vrijemePocetno);
                dodan = true;
            }
            vrijemePocetno += 86400;
            try
            {
                sleep(this.trajanjePauze*1000);
            } catch (InterruptedException ex)
            {
                Logger.getLogger(PolasciAviona.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return dodan;
    }

    /**
     * Funckija provjerava postoji li zapis u bazi podataka za određeni aerodrom na određeni datum
     *
     * @param icao - oznaka aerodroma
     * @param vrijeme - epcoh vrijeme u sekndama koje se pretvara datum
     * @return - vraća informaciju o uspješnosti fukcije
     */
    private boolean provjeriZapisMyAirportsLog(String icao, long vrijeme)
    {
        String datum = dobijDatumIzLonga(vrijeme);
        return (myAirportsLogDAO.provjeriAerodromZaDatum(icao, datum, this.pbp));
    }

    /**
     * Funckija sprema avione u tablicu Airplanes
     *
     * @param avioni - iista aviona koje treba spremiti
     */
    private void spremiAvione(List<AvionLeti> avioni)
    {
        for (AvionLeti avion : avioni)
        {
            airplanesDao.dodajAvion(avion, pbp);
        }
    }

    /**
     * Funckija sprema aeordom iz kojeg su preuzeti podaci aviona u bazu podataka, tablicu
     * MyAirportsLog
     *
     * @param aerodrom - aerodrom kojeg sprema
     * @param vrijeme - epcoh vrijeme u sekndama koje se pretvara datum
     * @return - vraća je li operacija uspjela ili ne
     */
    private boolean spremiAerodrom(Aerodrom aerodrom, long vrijeme)
    {
        String datum = dobijDatumIzLonga(vrijeme);
        return (myAirportsLogDAO.dodajAerodromLog(aerodrom.getIcao(), datum, pbp));
    }

    /**
     * Funckija služi za dobivanje datuma koji se može spremiti u bazu podataka pritom koristeći
     * epoch
     *
     * @param vrijeme - epoch vrijeme koje se pretvara
     * @return - String datum za upis u bazu podataka
     */
    private String dobijDatumIzLonga(long vrijeme)
    {
        SimpleDateFormat formatZaBazu = new SimpleDateFormat("yyyy-MM-dd");
        Date datum = new Date(vrijeme * 1000);
        String datumString = formatZaBazu.format(datum);
        return datumString;
    }

}
