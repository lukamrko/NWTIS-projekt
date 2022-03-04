package org.foi.nwtis.lmrkonjic.projekt.app1.Komanda;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.lmrkonjic.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

/**
 * Klasa koja zapravo predstavlja dretvu. U njoj se tijekom primanja zahtjeva, otvara nova dretva za
 * obradu tog zahtjeva koristeći ServerSocket
 *
 */
public class ServerSocketKlasa extends Thread
{

    private PostavkeBazaPodataka pbp;
    private String adresa;
    private int maxBrojDretvi;
    private int port;
    private int maksimalnoCekaca;
    private ServerSocket ss;
    private boolean kraj;
    private IzvrsiKomande izvrsiKomande;

    /**
     * Konstruktor klase ServerSocketKlasa. Uloga mu je proslijediti PostavkeBazePodataka iz
     * SlusacaAplikacije
     *
     * @param pbp - Objekt klase PostavkeBazePodataka
     */
    public ServerSocketKlasa(PostavkeBazaPodataka pbp)
    {
        this.pbp = pbp;
    }

    /**
     * Funkcija koja učitaje vrijednosti iz postavkeBazePodataka u globalne varijable
     *
     * @param pbp - Objekt klase PostavkeBazePodataka
     */
    private void ucitajVrijednosti()
    {
        this.maxBrojDretvi = Integer.parseInt(this.pbp.dajPostavku("app1.maxBrojDretvi"));
        this.adresa = this.pbp.dajPostavku("app1.adresa");
        this.port = Integer.parseInt(this.pbp.dajPostavku("app1.port"));
        this.maksimalnoCekaca = Integer.parseInt(this.pbp.dajPostavku("app1.maxBrojCekaca"));
    }

    /**
     * Obavlja operacije koje su potrebne za započeti rad dretve kao što su incijaliziranje
     * vrijednosti, klasa i uvjeta za izlaska iz "neograničene" petlje
     */
    @Override
    public synchronized void start()
    {
        ucitajVrijednosti();
        this.izvrsiKomande = new IzvrsiKomande(pbp);
        this.kraj = false;
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * U slučaju prekida zatvara ServerSocket te onemogućuje daljnje otvaranje ServerSocketa
     */
    @Override
    public void interrupt()
    {
        try
        {
            if (!this.ss.isClosed())
            {
                this.ss.close();
            }
        } catch (IOException ex)
        {
            Logger.getLogger(ServerSocketKlasa.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.kraj = true;
        super.interrupt();
    }

    /**
     * Pokeće petlju koja čeka ServerSocket zahtjev. U slučaju dobivanja zahtjeva, otvara se nova
     * dretva koja obrađuje taj zahtjev
     */
    @Override
    public void run()
    {
        try
        {
            this.ss = new ServerSocket(this.port, this.maksimalnoCekaca);
            while (!this.kraj)
            {
                System.out.println("Cekam zahtjeve...");
                Socket uticnica = ss.accept();
                if (maxBrojDretvi > 0)
                {
                    maxBrojDretvi--;
                    Thread dretva = new DretvaSocketa(uticnica, this.izvrsiKomande);
                    dretva.start();
                } else
                {
                    System.out.println("ERROR 01 nema slobodne dretve");
                }
            }
        } catch (IOException ex)
        {
            Logger.getLogger(ServerSocketKlasa.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Klasa unutar klase koja služi za obradu ServerSocket zahtjeva
     */
    private class DretvaSocketa extends Thread
    {

        private Socket uticnica;
        private IzvrsiKomande izvrsiKomande;

        /**
         * Konstuktor kojim se učitavaju potrebne vrijednosti za obradu zahtjeva
         *
         * @param uticnica - objekt tipa Socket
         * @param izvrsiKomande - Klasa u kojoj se obrađuju zaprimljene naredbe
         */
        public DretvaSocketa(Socket uticnica, IzvrsiKomande izvrsiKomande)
        {
            this.uticnica = uticnica;
            this.izvrsiKomande = izvrsiKomande;
        }

        /**
         * U slučaju prekida pravilno zatvara dretvu
         */
        @Override
        public void interrupt()
        {
            if (!this.uticnica.isClosed())
            {
                try
                {
                    this.uticnica.close();
                } catch (IOException ex)
                {
                    Logger.getLogger(ServerSocketKlasa.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            super.interrupt();
        }

        /**
         * Funkcija prima zahtjev, prosljeđuje ga na daljnju obradu te šalje nazad odgovor
         * koristeći Socket
         */
        @Override
        public void run()
        {
            System.out.println("App1-Veza uspostavljena!");
            try (InputStream is = this.uticnica.getInputStream();
                    OutputStream os = this.uticnica.getOutputStream();
                    OutputStreamWriter osw = new OutputStreamWriter(os))
            {
                StringBuilder zahtjev = new StringBuilder();
                while (true)
                {
                    int i = is.read();
                    if (i == -1)
                    {
                        break;
                    }
                    zahtjev.append((char) i);
                }
                this.uticnica.shutdownInput();
                System.out.println(zahtjev.toString());
                String odgovor = obradiKomandu(zahtjev.toString());
                osw.write(odgovor);
                osw.flush();
                this.uticnica.shutdownOutput();
            } catch (IOException ex)
            {
                Logger.getLogger(ServerSocketKlasa.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /**
         * Prima zahtjev iz socketa te ga šalje na obradu unutar klase IzvrsiKomande.
         * @param zahtjev - zahtjev u obliku String, koji prati upute iz PDF-a
         * @return - vraća odgovor u Obliku stringa
         */
        private String obradiKomandu(String zahtjev)
        {
            System.out.println("APP1-ssk-obradiKomandu");
            String odgovor = this.izvrsiKomande.izvrsi(zahtjev);
            return odgovor;
        }
    }

}
