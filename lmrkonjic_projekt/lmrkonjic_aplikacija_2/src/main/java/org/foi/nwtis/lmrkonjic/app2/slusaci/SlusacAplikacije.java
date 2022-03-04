package org.foi.nwtis.lmrkonjic.app2.slusaci;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.lmrkonjic.app2.dretve.PolasciAviona;
import org.foi.nwtis.lmrkonjic.app2.dretve.PreuzimanjeMETEO;
import org.foi.nwtis.lmrkonjic.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.lmrkonjic.vjezba_03.konfiguracije.NeispravnaKonfiguracija;

/**
 * Funkcija koja tijekom deploya aplikacija odradi neke stvari, kao i tijekom undeploya
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener
{

    PolasciAviona polasciAviona;
    PreuzimanjeMETEO preuzimanjeMETEO;

    /**
     * Zatvara dretve odgovorne za preuzimanje letova avoina kao i za preuzimanje meteo podataka
     * Također briše atribut postavke iz servletContexta
     *
     * @param sce
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        if (polasciAviona != null && polasciAviona.isAlive())
        {
            polasciAviona.interrupt();
        }
        if (preuzimanjeMETEO != null && preuzimanjeMETEO.isAlive())
        {
            preuzimanjeMETEO.interrupt();
        }

        ServletContext servletContext = sce.getServletContext();
        servletContext.removeAttribute("Postavke");
        System.out.println("Konfiguracija obrisana!");
    }

    /**
     * Postavlja atribut Postavke unutar servletContexta, ucitaje Postavke te pokreće dretvu za
     * preuzimanje letova aviona i preuzimanje meteo podataka
     *
     * @param sce
     */
    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        ServletContext servletContext = sce.getServletContext();
        String putanjaKonfDatoteka = servletContext.getRealPath("WEB-INF")
                + File.separator + servletContext.getInitParameter("konfiguracija");

        System.out.println(putanjaKonfDatoteka);

        PostavkeBazaPodataka pbp = new PostavkeBazaPodataka(putanjaKonfDatoteka);
        try
        {
            pbp.ucitajKonfiguraciju();
            servletContext.setAttribute("Postavke", pbp);
            polasciAviona = new PolasciAviona(pbp);
            preuzimanjeMETEO = new PreuzimanjeMETEO(pbp);
            polasciAviona.start();
            preuzimanjeMETEO.start();
        } catch (NeispravnaKonfiguracija ex)
        {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
