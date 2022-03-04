package org.foi.nwtis.lmrkonjic.projekt.app1.Komanda;

import jakarta.ws.rs.core.Response;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import org.foi.nwtis.lmrkonjic.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.lmrkonjic.projekt.app1.DB.KorisnikDAO;
import org.foi.nwtis.lmrkonjic.projekt.app1.DB.OvlastiDAO;
import org.foi.nwtis.lmrkonjic.projekt.app1.Entitet.Dnevnik;
import org.foi.nwtis.lmrkonjic.projekt.app1.Entitet.DnevnikKlijent;
import org.foi.nwtis.lmrkonjic.projekt.app1.Entitet.Korisnik;
import org.foi.nwtis.lmrkonjic.projekt.app1.Entitet.Sjednica;

/**
 * Klasa koja je zadužena za izvršavanje svih komandi koje aplikacija 1 mora obavljati
 * @author Mrky
 */
public class IzvrsiKomande
{

    private HashMap<String, Sjednica> sjednice;
    private KorisnikDAO korisnikDAO;
    private OvlastiDAO ovlastiDAO;
    private PostavkeBazaPodataka pbp;
    /**
     * Vrijeme u milisekundama (iz konfiguracije se preuzimaju sekunde)
     */
    private long sjednicaTrajanje;
    private int maxZahtjeva;
    private int idSjednice;

    /**
     * Konstruktor kojim se incijalizira stanje cijele klase
     *
     * @param pbp - objekt PostavkeBazaPodataka
     */
    public IzvrsiKomande(PostavkeBazaPodataka pbp)
    {
        this.pbp = pbp;
        ucitajPostavke();
        this.sjednice = new HashMap<String, Sjednica>();
        this.korisnikDAO = new KorisnikDAO(this.pbp);
        this.ovlastiDAO = new OvlastiDAO(this.pbp);
        this.idSjednice = 1;
    }

    /**
     * Koristeći objekt PostavkeBazaPodataka incijalizira vrijednosti iz konfiguracijske datoteke
     */
    private void ucitajPostavke()
    {
        this.sjednicaTrajanje = Long.parseLong(this.pbp.dajPostavku("sjednica.trajanje")) * 1000;
        this.maxZahtjeva = Integer.parseInt(this.pbp.dajPostavku("app1.maxZahtjeva"));
    }

    /**
     * Glavna metoda koja prima zahtjev. Zahtjev se zatim pravilno proslijedi kroz switch te se
     * vrati odgovor
     *
     * @param zahtjev - string u kojem se nalazi primljena komanda
     * @return - vraća odgovor na pojedini zahtjev u obliku string
     */
    public String izvrsi(String zahtjev)
    {
        System.out.println("APP1-izvrsi-switchIzbornik");
        Long vrijemePrimanja = Instant.now().toEpochMilli() / 1000;
        String odgovor = "ERROR 10 format komande nije ispravan! Komanda ne postoji!";
        if (!zahtjev.contains(" "))
        {
            return odgovor;
        }
        String[] funkcija = zahtjev.split(" ");
        switch (funkcija[0])
        {
            case "ADD":
                odgovor = add(zahtjev);
                break;
            case "AUTHEN":
                odgovor = authen(zahtjev);
                break;
            case "LOGOUT":
                odgovor = logOut(zahtjev);
                break;
            case "GRANT":
                odgovor = grant(zahtjev);
                break;
            case "REVOKE":
                odgovor = revoke(zahtjev);
                break;
            case "RIGHTS":
                odgovor = rights(zahtjev);
                break;
            case "AUTHOR":
                odgovor = author(zahtjev);
                break;
            case "LIST":
                odgovor = list(zahtjev);
                break;
            case "LISTALL":
                odgovor = listAll(zahtjev);
                break;
        }
        zapisiUDnevnik(funkcija[1], vrijemePrimanja, zahtjev, odgovor);
        return odgovor;
    }

    /**
     * Metoda koji služi za dodavnje korisnika u bazu podataka
     *
     * @param zahtjev - puna komanda za dodavanje korisnika u bazu podataka
     * @return - odgovor o ne/uspješnosti zahtjeva
     */
    private String add(String zahtjev)
    {
        System.out.println("APP1-izvrsi-add");
        Korisnik korisnik = obradiAddZahtjev(zahtjev);
        if (korisnik == null)
        {
            return "ERROR 10 format komande nije ispravan!";
        }
        System.out.println("APP1-izvrsi-prijekorisnikDAO");
        if (this.korisnikDAO.postojiKorisnik(korisnik.korime))
        {
            return "ERROR 13 traženi korisnik već postoji";
        }
        if (this.korisnikDAO.dodajKorisnika(korisnik))
        {
            return "OK";
        } else
        {
            return "ERROR 18 Došlo je do greške u bazi podataka";
        }
    }

    /**
     * Rastavlja primljeni zahtjev na logičke dijelove kako bi se korisnik lakše dodao u bazu
     * podataka
     *
     * @param zahtjev - originalni zahtjev u string oblikku
     * @return - objekt tipa korisnik, koji je dobiven iz zahtjeva
     */
    private Korisnik obradiAddZahtjev(String zahtjev)
    {
        System.out.println("APP1-izvrsi-obradiAdd");
        try
        {
            String[] rastavaNavodnici = zahtjev.split("\"");
            if (rastavaNavodnici.length != 4)
            {
                return null;
            }
            String[] rastavaNaredba = rastavaNavodnici[0].split(" ");
            if (rastavaNaredba.length != 3)
            {
                return null;
            }
            return new Korisnik(rastavaNaredba[1], rastavaNaredba[2],
                    rastavaNavodnici[1], rastavaNavodnici[3]);
        } catch (Exception e)
        {
            return null;
        }
    }

    /**
     * metoda za autenikaciju korisnika. dodaje ili ažurira sjednicu korisnika u HashMap sjednica
     *
     * @param zahtjev - orignalni zahtjev u obliku string
     * @return - vraća odgovor o ne/uspješnosti operaciju u obliku string
     */
    private String authen(String zahtjev)
    {
        String[] dijeloviZahtjeva = zahtjev.split(" ");
        if (dijeloviZahtjeva.length != 3)
        {
            return "ERROR 10 format komande nije ispravan! Unesen je kriv broj parametara";
        }
        String korisnik = dijeloviZahtjeva[1];
        String lozinka = dijeloviZahtjeva[2];
        if (!this.korisnikDAO.postojiKorisnik(korisnik))
        {
            return "ERROR 11 unesni korisnik ne postoji";
        }
        if (!this.korisnikDAO.postojiKorisnikLozinka(korisnik, lozinka))
        {
            return "ERROR 11 unesena lozinka ne odgovara ovom korisnku";
        }
        Sjednica sjednica;
        if (this.sjednice.containsKey(korisnik))
        {
            sjednica = this.sjednice.get(korisnik);
            sjednica.azurirajVrijeme(this.sjednicaTrajanje);
            this.sjednice.put(korisnik, sjednica);
        } else
        {
            sjednica = new Sjednica(this.idSjednice++, korisnik, this.sjednicaTrajanje, this.maxZahtjeva);
            this.sjednice.put(korisnik, sjednica);
        }
        String odgovor = "OK " + sjednica.idSjednice + " " + sjednica.vrijemeTrajanja / 1000 + " " + sjednica.brojZahtjeva;
        //this.sjednice.forEach((k, v) -> System.out.println("Korisnik:" + k + " Sjednica:" + v.idSjednice));
        return odgovor;
    }

    /**
     * Metoda koja briše sjednicu pojedinog korisnika
     *
     * @param zahtjev - orignalni zahtjev u obliku string
     * @return - vraća odgovor o ne/uspješnosti operaciju u obliku string
     */
    private String logOut(String zahtjev)
    {
        String[] dijeloviZahtjeva = zahtjev.split(" ");
        if (dijeloviZahtjeva.length != 3)
        {
            return "ERROR 10 format komande nije ispravan! Unesno je kriv broj parametara";
        }
        String korime = dijeloviZahtjeva[1];
        int idSjednica;
        try
        {
            idSjednica = Integer.parseInt(dijeloviZahtjeva[2]);
        } catch (NumberFormatException numberFormatException)
        {
            return "ERROR 10 format komande nije ispravan! Na mjesto ID-a je unešen ne brojčani zapis";
        }
        if (!this.korisnikDAO.postojiKorisnik(korime))
        {
            return "ERROR 11 korisnik koji se odjavljuje ne postoji!";
        }
        if (!this.sjednice.containsKey(korime))
        {
            return "ERROR 15 korisnik nema sjednicu!";
        }
        Sjednica sjednica = this.sjednice.get(korime);
        if (sjednica.idSjednice != idSjednica)
        {
            return "ERROR 15 pogrešno unesen id sjednice!";
        }
        if (sjednica.vrijemeTrajanja > Instant.now().toEpochMilli())
        {
            return "ERROR 15 isteklo vrijeme trajanja sjednice";
        }
        sjednica.vrijemeTrajanja = Instant.now().toEpochMilli();
        sjednica.brojZahtjeva = 0;
        this.sjednice.remove(korime);
        //this.sjednice.forEach((k, v) -> System.out.println("Korisnik:" + k + " Sjednica:" + v.idSjednice));
        return "OK";
    }

    /**
     * Dodaje aktivno područje pojedinom korisniku u bazi podataka, tablici ovlasti
     *
     * @param zahtjev - orignalni zahtjev u obliku string
     * @return - vraća odgovor o ne/uspješnosti operaciju u obliku string
     */
    private String grant(String zahtjev)
    {
        String[] dijeloviZahtjeva = zahtjev.split(" ");
        if (dijeloviZahtjeva.length != 5)
        {
            return "ERROR 10 format komande nije ispravan! Unesno je kriv broj parametara";
        }
        String korime = dijeloviZahtjeva[1];
        int idSjednica;
        try
        {
            idSjednica = Integer.parseInt(dijeloviZahtjeva[2]);
        } catch (NumberFormatException numberFormatException)
        {
            return "ERROR 10 format komande nije ispravan! Na mjesto ID-a je unešen ne brojčani zapis";
        }
        String podrucje = dijeloviZahtjeva[3];
        String trazeniKorisnik = dijeloviZahtjeva[4];
        String odgovor = generalnaProvjera(korime, idSjednica);
        if (odgovor.contains("ERROR"))
        {
            return odgovor;
        }
        Sjednica sjednica = this.sjednice.get(korime);
        sjednica.brojZahtjeva--;
        if (!this.korisnikDAO.postojiKorisnik(trazeniKorisnik))
        {
            return "ERROR 17 traženi korisnik ne postoji!";
        }
        if (this.ovlastiDAO.imaPodrucje(trazeniKorisnik, podrucje, Boolean.TRUE))
        {
            return "ERROR 13 traženi korisnik već ima aktivno područje!";
        }
        if (this.ovlastiDAO.imaPodrucje(trazeniKorisnik, podrucje, Boolean.FALSE))
        {
            if (!this.ovlastiDAO.azurirajPodrucje(Boolean.TRUE, trazeniKorisnik, podrucje))
            {
                return "ERROR 18 Pogreška u radu s bazom podataka!";
            }
        } else
        {
            if (!this.ovlastiDAO.unesiPodrucje(trazeniKorisnik, podrucje, Boolean.TRUE))
            {
                return "ERROR 18 Pogreška u radu s bazom podataka!";
            }
        }
        return "OK";
    }

    /**
     * Traženom korisniku čini pojedino područje unutar baze podataka, tablice ovlasti, nedostupnim
     *
     * @param zahtjev - orignalni zahtjev u obliku string
     * @return - vraća odgovor o ne/uspješnosti operaciju u obliku string
     */
    private String revoke(String zahtjev)
    {
        String[] dijeloviZahtjeva = zahtjev.split(" ");
        if (dijeloviZahtjeva.length != 5)
        {
            return "ERROR 10 format komande nije ispravan! Unesno je kriv broj parametara";
        }
        String korime = dijeloviZahtjeva[1];
        int idSjednica;
        try
        {
            idSjednica = Integer.parseInt(dijeloviZahtjeva[2]);
        } catch (NumberFormatException numberFormatException)
        {
            return "ERROR 10 format komande nije ispravan! Na mjesto ID-a je unešen ne brojčani zapis";
        }
        String podrucje = dijeloviZahtjeva[3];
        String trazeniKorisnik = dijeloviZahtjeva[4];
        String odgovor = generalnaProvjera(korime, idSjednica);
        if (odgovor.contains("ERROR"))
        {
            return odgovor;
        }
        Sjednica sjednica = this.sjednice.get(korime);
        sjednica.brojZahtjeva--;
        if (!this.korisnikDAO.postojiKorisnik(trazeniKorisnik))
        {
            return "ERROR 17 traženi korisnik ne postoji!";
        }
        if (!this.ovlastiDAO.imaPodrucje(trazeniKorisnik, podrucje))
        {
            return "ERROR 14 traženi korisnik nema ovo područje!";
        }
        if (this.ovlastiDAO.imaPodrucje(trazeniKorisnik, podrucje, Boolean.FALSE))
        {
            return "ERROR 14 od traženog korisnika područje je već neaktivno!";
        } else
        {
            if (!this.ovlastiDAO.azurirajPodrucje(Boolean.FALSE, trazeniKorisnik, podrucje))
            {
                return "ERROR 18 Pogreška u radu s bazom podataka!";
            }
        }
        return "OK";
    }

    /**
     * Vraća sva aktivna područja pojedinog korisnika iz baze podataka, tablice ovlasti
     *
     * @param zahtjev - orignalni zahtjev u obliku string
     * @return - vraća područje koja pripadaju pojedinom korisniku iz tablice ovlasti
     */
    private String rights(String zahtjev)
    {
        String[] dijeloviZahtjeva = zahtjev.split(" ");
        if (dijeloviZahtjeva.length != 4)
        {
            return "ERROR 10 format komande nije ispravan! Unesno je kriv broj parametara";
        }
        String korime = dijeloviZahtjeva[1];
        int idSjednica;
        try
        {
            idSjednica = Integer.parseInt(dijeloviZahtjeva[2]);
        } catch (NumberFormatException numberFormatException)
        {
            return "ERROR 10 format komande nije ispravan! Na mjesto ID-a je unešen ne brojčani zapis";
        }
        String trazeniKorisnik = dijeloviZahtjeva[3];
        String odgovor = generalnaProvjera(korime, idSjednica);
        if (odgovor.contains("ERROR"))
        {
            return odgovor;
        }
        Sjednica sjednica = this.sjednice.get(korime);
        sjednica.brojZahtjeva--;
        if (!this.korisnikDAO.postojiKorisnik(trazeniKorisnik))
        {
            return "ERROR 17 traženi korisnik ne postoji!";
        }
        ArrayList<String> aktivnaPodrucja = this.ovlastiDAO.dohvatiPodrucja(trazeniKorisnik, Boolean.TRUE);
        if (aktivnaPodrucja.isEmpty())
        {
            return "ERROR 14 traženi korisnik nema aktivna područja!";
        }
        StringBuilder sb = new StringBuilder("OK");
        for (String aktivnoPodrucje : aktivnaPodrucja)
        {
            sb.append(" ");
            sb.append(aktivnoPodrucje);
        }
        return sb.toString();
    }

    /**
     * Provjera je li traženi korisnik ima aktivno područje unutar tablice ovlasti
     *
     * @param zahtjev - orignalni zahtjev u obliku string
     * @return - vraća odgovor o ne/uspješnosti operaciju u obliku string
     */
    private String author(String zahtjev)
    {
        String[] dijeloviZahtjeva = zahtjev.split(" ");
        if (dijeloviZahtjeva.length != 4)
        {
            return "ERROR 10 format komande nije ispravan! Unesno je kriv broj parametara";
        }
        String korime = dijeloviZahtjeva[1];
        int idSjednica;
        try
        {
            idSjednica = Integer.parseInt(dijeloviZahtjeva[2]);
        } catch (NumberFormatException numberFormatException)
        {
            return "ERROR 10 format komande nije ispravan! Na mjesto ID-a je unešen ne brojčani zapis";
        }
        String podrucje = dijeloviZahtjeva[3];
        String odgovor = generalnaProvjera(korime, idSjednica);
        if (odgovor.contains("ERROR"))
        {
            return odgovor;
        }
        Sjednica sjednica = this.sjednice.get(korime);
        sjednica.brojZahtjeva--;
        if (this.ovlastiDAO.imaPodrucje(korime, podrucje, Boolean.TRUE))
        {
            return "OK";
        } else
        {
            return "ERROR 14 traženi korisnik nema aktivno područje!";
        }
    }

    /**
     * Vraća podatke za pojedinog korisnika
     *
     * @param zahtjev - orignalni zahtjev u obliku string
     * @return - vraća podatke o traženom korisniku. Ukoliko nema traženog korisnika ispisiju grešku
     */
    private String list(String zahtjev)
    {
        String[] dijeloviZahtjeva = zahtjev.split(" ");
        if (dijeloviZahtjeva.length != 4)
        {
            return "ERROR 10 format komande nije ispravan! Unesno je kriv broj parametara";
        }
        String korime = dijeloviZahtjeva[1];
        int idSjednica;
        try
        {
            idSjednica = Integer.parseInt(dijeloviZahtjeva[2]);
        } catch (NumberFormatException numberFormatException)
        {
            return "ERROR 10 format komande nije ispravan! Na mjesto ID-a je unešen ne brojčani zapis";
        }
        String trazeniKorisnik = dijeloviZahtjeva[3];
        String odgovor = generalnaProvjera(korime, idSjednica);
        if (odgovor.contains("ERROR"))
        {
            return odgovor;
        }
        Sjednica sjednica = this.sjednice.get(korime);
        sjednica.brojZahtjeva--;
        if (!this.korisnikDAO.postojiKorisnik(trazeniKorisnik))
        {
            return "ERROR 17 traženi korisnik ne postoji!";
        }
        Korisnik korisnik = this.korisnikDAO.dohvatiTrazenogKorisnika(trazeniKorisnik);
        StringBuilder sb = new StringBuilder("OK");
        sb.append(" \"");
        sb.append(korisnik.korime);
        sb.append("\t");
        sb.append(korisnik.prezime);
        sb.append("\t");
        sb.append(korisnik.ime);
        sb.append("\"");
        return sb.toString();
    }

    /**
     * Vraća podataka svih korisnika (osim lozinki)
     *
     * @param zahtjev - orignalni zahtjev u obliku string
     * @return - vraća string sa svim korisnicima, ili greška ukoliko je ima
     */
    private String listAll(String zahtjev)
    {
        String[] dijeloviZahtjeva = zahtjev.split(" ");
        if (dijeloviZahtjeva.length != 3)
        {
            return "ERROR 10 format komande nije ispravan! Unesno je kriv broj parametara";
        }
        String korime = dijeloviZahtjeva[1];
        int idSjednica;
        try
        {
            idSjednica = Integer.parseInt(dijeloviZahtjeva[2]);
        } catch (NumberFormatException numberFormatException)
        {
            return "ERROR 10 format komande nije ispravan! Na mjesto ID-a je unešen ne brojčani zapis";
        }
        String odgovor = generalnaProvjera(korime, idSjednica);
        if (odgovor.contains("ERROR"))
        {
            return odgovor;
        }
        Sjednica sjednica = this.sjednice.get(korime);
        sjednica.brojZahtjeva--;
        ArrayList<Korisnik> korisnici = this.korisnikDAO.dohvatiSveKorisnike();
        StringBuilder sb = new StringBuilder("OK");
        for (Korisnik korisnik : korisnici)
        {
            sb.append(" \"");
            sb.append(korisnik.korime);
            sb.append("\t");
            sb.append(korisnik.prezime);
            sb.append("\t");
            sb.append(korisnik.ime);
            sb.append("\"");
        }
        return sb.toString();
    }

    /**
     * Metoda koja provjerava greške koje se javljaju u većini komandi
     *
     * @param korime - korisničko ime, vezano uz sjednicu
     * @param idSjednica - idSjednice od gore korisnika
     * @return - Ukoliko postoji greška vraća određeni ERROR, inače vraća OK
     */
    private String generalnaProvjera(String korime, int idSjednica)
    {
        if (!this.korisnikDAO.postojiKorisnik(korime))
        {
            return "ERROR 11 korisnik koji traži ne postoji!";
        }
        if (!this.sjednice.containsKey(korime))
        {
            return "ERROR 15 korisnik nema sjednicu!";
        }
        Sjednica sjednica = this.sjednice.get(korime);
        if (sjednica.idSjednice != idSjednica)
        {
            return "ERROR 15 pogrešno unesen id sjednice!";
        }
        if (sjednica.vrijemeTrajanja < Instant.now().toEpochMilli())
        {
            return "ERROR 15 isteklo vrijeme trajanja sjednice";
        }
        if (sjednica.brojZahtjeva <= 0)
        {
            return "ERROR 16 broj zahtjeva u ovoj sjednici je 0 pa nije moguće izvesi operaciju!";
        }
        return "OK";
    }

    /**
     * Metoda koja po izvršetku komande zapisuje osnovne informacije u dnevniku
     * @param korime - korisničko ime osobe koje je tražila pojedinu komandu
     * @param vrijemePrimanja - vrijeme kad se komanda primila. zapisana je pomoću epoch vremena
     * @param komanda - komanda koju je korisnik unio
     * @param odgovor - odgovor koju je korisnik dobio. Može biti OK ili ERROR s nekim brojem
     */
    private void zapisiUDnevnik(String korime, Long vrijemePrimanja, String komanda, String odgovor)
    {
        if (odgovor.contains("ERROR"))
        {
            String[] dioOdgovora = odgovor.split(" ");
            odgovor = dioOdgovora[0] + " " + dioOdgovora[1];
        } else
        {
            odgovor = "OK";
        }
        Dnevnik dnevnik = new Dnevnik(korime, vrijemePrimanja, komanda, odgovor);
        DnevnikKlijent dnevnikKlijent = new DnevnikKlijent();
        Response odgovorZahtjeva = dnevnikKlijent.dodajZapis(dnevnik);
        try
        {
            String odgovorZahtjevaStr = odgovorZahtjeva.readEntity(String.class);
            System.out.println("APP1-DnevnikDodavanje:" + odgovorZahtjevaStr);
        } catch (Exception e)
        {
        }

    }

}
