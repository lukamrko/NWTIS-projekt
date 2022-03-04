package org.foi.nwtis.lmrkonjic.projekt.app1.Komanda;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.lmrkonjic.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.lmrkonjic.vjezba_03.konfiguracije.NeispravnaKonfiguracija;

/**
 * Funkcija koja tijekom deploya aplikacija odradi neke stvari, kao i tijekom undeploya
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener
{

    ServerSocketKlasa serverSocket;

    /**
     * Funkcija koja tijekom undeploya aplikacije 1 oslobađa memoriju brišeći dretvu i atribut postavke
     * @param sce 
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        if (serverSocket != null && serverSocket.isAlive())
        {
            serverSocket.interrupt();
        }

        ServletContext servletContext = sce.getServletContext();
        servletContext.removeAttribute("Postavke");
        System.out.println("Konfiguracija obrisana!");
    }

    /**
     * Funckija koja tijekom deploya aplikacije 1 obavlja radnje potrebne za rad aplikacije, u što
     * spada slanje postavke datoteke, te startanje serverSocket klase
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
            serverSocket = new ServerSocketKlasa(pbp);
            serverSocket.start();
            System.out.println("Konfiguracija učitana!");

        } catch (NeispravnaKonfiguracija ex)
        {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
