package org.foi.nwtis.lmrkonjic.app2.komunikacija;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.lmrkonjic.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

public class SlanjeZahtjeva
{

    private PostavkeBazaPodataka pbp;
    private String adresa;
    private int port;

    public SlanjeZahtjeva(PostavkeBazaPodataka pbp)
    {
        this.pbp = pbp;
        ucitajPodatke();
    }

    /**
     * Ucitaje podatke iz konfiguracijske datoteke
     */
    private void ucitajPodatke()
    {
        this.adresa = this.pbp.dajPostavku("app2.adresa");
        this.port = Integer.parseInt(this.pbp.dajPostavku("app2.port"));
    }

    /**
     * Javna metoda koja služi za obradu zahtjeva. primljeni paramtera šalje u socket, a nazad
     * dobiva odgovor
     *
     * @param komanda - komanda odnosno zahtjev koji treba obraditi
     * @return - vraća String odgovor na gore navedenu komandu
     */
    public String obradiZahtjev(String komanda)
    {
        System.out.println("APP2-SlanjeZahtjeva-Komanda je: " + komanda);
        String obradjeniZahtjev = posaljiZahtjev(komanda);
        return obradjeniZahtjev;
    }

    /**
     * Šalje zahtjev na ServerSocket iz aplikacije 1
     * @param komanda - komanda/zahtjev koji šalje u serverSocket
     * @return - odgovor na komandu
     */
    private String posaljiZahtjev(String komanda)
    {
        try (Socket uticnica = new Socket(this.adresa, this.port);
                InputStream is = uticnica.getInputStream();
                OutputStream os = uticnica.getOutputStream();)
        {

            System.out.println("App2 - Spojen na: " + this.adresa + ":" + this.port);
            os.write(komanda.getBytes());
            os.flush();
            uticnica.shutdownOutput();
            StringBuilder text = new StringBuilder();
            while (true)
            {
                int i = is.read();
                if (i == -1)
                {
                    break;
                }
                text.append((char) i);
            }
            uticnica.shutdownInput();
            uticnica.close();
            return text.toString();
        } catch (IOException ex)
        {
            Logger.getLogger(SlanjeZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "ERROR 18 Greška sa serverSocketom";
    }
}
