package entitet;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
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
    private int brojCekaca;
    private int port;

    public SlanjeZahtjeva(PostavkeBazaPodataka pbp)
    {
        this.pbp = pbp;
        ucitajPodatke();
    }

    private void ucitajPodatke()
    {
        this.adresa = this.pbp.dajPostavku("app3.adresa");
        this.port = Integer.parseInt(this.pbp.dajPostavku("app3.port"));
        this.brojCekaca = Integer.parseInt(this.pbp.dajPostavku("app3.maxBrojCekaca"));
        System.out.println("Adresa je: " + this.adresa);
    }

    public String obradiZahtjev(String komanda)
    {
        System.out.println("APP3-SlanjeZahtjeva-Komanda je: " + komanda);
        String obradjeniZahtjev = posaljiZahtjev(komanda);
        return obradjeniZahtjev;
    }

    private String posaljiZahtjev(String komanda)
    {
        System.out.println("App3-Šaljem komandu: " + komanda);
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
        return "ERROR Greška sa serverSocketom";
    }
}
