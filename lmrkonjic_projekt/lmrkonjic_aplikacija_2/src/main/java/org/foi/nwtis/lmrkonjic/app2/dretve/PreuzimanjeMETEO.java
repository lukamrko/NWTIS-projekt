package org.foi.nwtis.lmrkonjic.app2.dretve;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.lmrkonjic.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.lmrkonjic.app2.podaci.MeteoDAO;
import org.foi.nwtis.lmrkonjic.app2.podaci.MyAirportDAO;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.klijenti.OWMKlijent;
import org.foi.nwtis.rest.podaci.MeteoOriginal;

public class PreuzimanjeMETEO extends Thread
{

    private PostavkeBazaPodataka pbp;
    private OWMKlijent owm;
    private String owmKljuc;
    private MeteoOriginal meteoOriginal;
    private int trajanjeCiklusa;
    private int trajanjePauze;
    private boolean kraj = false;
    private List<Aerodrom> mojiAerodromi;
    private MyAirportDAO myAirportDAO;
    private MeteoDAO meteoDAO;

    /**
     * Konstruktor koji omoguđuje pristup PostavkeBazaPodataka
     *
     * @param pbp - PostavkeBazaPodataka
     */
    public PreuzimanjeMETEO(PostavkeBazaPodataka pbp)
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
        ucitajPodatke();
        this.myAirportDAO = new MyAirportDAO();
        this.mojiAerodromi = myAirportDAO.dohvatiSveAerodrome(pbp);
        this.owm = new OWMKlijent(owmKljuc);
        this.meteoOriginal = new MeteoOriginal();
        super.start();
    }

    /**
     * Ucitaje podatke iz konfiguracijske datoteke
     */
    private void ucitajPodatke()
    {
        boolean status = Boolean.parseBoolean(pbp.dajPostavku("meteo.preuzimanje.status"));
        if (!status)
        {
            System.out.println("Ne preuzimam podatke!");
            interrupt();
        }
        this.trajanjeCiklusa = Integer.parseInt(pbp.dajPostavku("meteo.preuzimanje.ciklus"));
        this.trajanjePauze = Integer.parseInt(this.pbp.dajPostavku("meteo.preuzimanje.pauza"));
        this.owmKljuc = this.pbp.dajPostavku("OpenWeatherMap.apikey");
        this.meteoDAO = new MeteoDAO(this.pbp);
    }

    /**
     * Funkcija koja pokreće preuzimanje podataka za sve aerodrome
     */
    @Override
    public void run()
    {
        System.out.println("Krenuli preuzimati podatke!");
        while (!kraj)
        {
            Timestamp vrijemePreuzimanja = new Timestamp(System.currentTimeMillis());
            System.out.println("Pocinjem preuzimati METEO podatake: " + vrijemePreuzimanja.toString());
            try
            {
                for (Aerodrom aerodrom : mojiAerodromi)
                {
                    if (preuzmiPodatke(aerodrom))
                    {
                        Thread.sleep(this.trajanjePauze*1000);
                    }
                }
                Thread.sleep(this.trajanjeCiklusa * 1000);
            } catch (InterruptedException | ParseException ex)
            {
                Logger.getLogger(PreuzimanjeMETEO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Preuzima meteo podatke za zadani aerodrom
     * @param aerodrom - Objekt tipa aerodrom kojemu se gleda lokacija
     * @return - vraća istinu ako se podatak preuzeo, a laž ako nije
     * @throws ParseException 
     */
    private boolean preuzmiPodatke(Aerodrom aerodrom) throws ParseException
    {
        String latituda = aerodrom.getLokacija().getLatitude();
        String longituda = aerodrom.getLokacija().getLongitude();
        this.meteoOriginal = this.owm.getRealTimeWeatherOriginal(latituda, longituda);
        if (this.meteoOriginal == null)
        {
            return false;
        }
        return this.meteoDAO.dodajMeteoPodatak(this.meteoOriginal, aerodrom.getIcao());
    }

}
