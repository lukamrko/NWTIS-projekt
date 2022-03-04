package org.foi.nwtis.lmrkonjic.app4.slusaci;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.lmrkonjic.app4.BankaPoruka;
import org.foi.nwtis.lmrkonjic.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.lmrkonjic.vjezba_03.konfiguracije.NeispravnaKonfiguracija;

@WebListener
public class SlusacAplikacije implements ServletContextListener
{

    //@Inject
    @EJB
    private BankaPoruka bankaPoruka;

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        ServletContext servletContext = sce.getServletContext();
        servletContext.removeAttribute("Postavke");
        this.bankaPoruka.obrisiPoruke();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        ServletContext servletContext = sce.getServletContext();

        String putanjaKonfDatoteke = servletContext.getRealPath("WEB-INF")
                + File.separator + servletContext.getInitParameter("konfiguracija");

        PostavkeBazaPodataka pbp = new PostavkeBazaPodataka(putanjaKonfDatoteke);
        try
        {
            // pbp.dajPostavku("putanjaPoruka");
            pbp.ucitajKonfiguraciju(putanjaKonfDatoteke);//vamo sam stavio samo da ne baca error
            servletContext.setAttribute("Postavke", pbp);
            String putanjaPoruka = servletContext.getRealPath("WEB-INF")
                    + File.separator + pbp.dajPostavku("putanjaPoruka");
            this.bankaPoruka.ucitajPodatke(putanjaPoruka);
        } catch (NeispravnaKonfiguracija ex)
        {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
