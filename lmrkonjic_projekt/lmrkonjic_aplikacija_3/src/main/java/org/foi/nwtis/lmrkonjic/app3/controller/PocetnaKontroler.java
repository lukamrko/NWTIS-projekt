package org.foi.nwtis.lmrkonjic.app3.controller;

import entitet.SlanjeZahtjeva;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import org.foi.nwtis.lmrkonjic.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import poruke.WebSocketKlijent;

@Path("pocetna")
@Controller
public class PocetnaKontroler
{

    @Inject
    ServletContext context;

    @GET
    @View("pocetna.jsp")
    public String pocetna(@Context HttpServletRequest httpZahtjev)
    {
//        HttpSession httpSesija = httpZahtjev.getSession();
//        String korime = null;
//        String lozinka = null;
//        String idSjednice = null;
//        try
//        {
//            korime = httpSesija.getAttribute("korime").toString();
//            lozinka = httpSesija.getAttribute("lozinka").toString();
//            idSjednice = httpSesija.getAttribute("idSjednice").toString();
//        } catch (Exception e)
//        {
//        }
//        if (korime == null || lozinka == null || idSjednice == null)
//        {
//            return "redirect:/index";
//        }
        return "../privatno/pocetna.jsp";
    }

    @POST
    public String odjavaKorisnika(
            @Context HttpServletRequest httpZahtjev)
    {
        HttpSession httpSesija = httpZahtjev.getSession(Boolean.FALSE);
        String korime = (String) httpSesija.getAttribute("korime");
        int idSjednice = (int) httpSesija.getAttribute("idSjednice");
        System.out.println(korime);
        String logoutZahtjev = "LOGOUT " + korime + " " + idSjednice;
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        SlanjeZahtjeva slanjeZahtjeva = new SlanjeZahtjeva(pbp);
        String odgovor = slanjeZahtjeva.obradiZahtjev(logoutZahtjev);
        if (odgovor.contains("OK"))
        {
            String lozinka = (String) httpSesija.getAttribute("lozinka");
            try
            {
                posaljiPoruku(korime, lozinka, idSjednice);
            } catch (Exception e)
            {
            }
            httpSesija.removeAttribute("korime");
            httpSesija.removeAttribute("lozinka");
            httpSesija.removeAttribute("idSjednice");
            return "redirect:/index";
        } else
        {
            httpZahtjev.setAttribute("greska", odgovor);
            return "../privatno/pocetna.jsp";
        }

    }

    private void posaljiPoruku(String korime, String lozinka, int idSjednice)
    {
        WebSocketKlijent webKlijent = new WebSocketKlijent();
        String aplikacijaPrijave = "lmrkonjic_aplikacija_3";
        StringBuilder poruka = new StringBuilder("korime:");
        poruka.append(korime);
        poruka.append(";lozinka:");
        poruka.append(lozinka);
        poruka.append(";idSjednice:");
        poruka.append(idSjednice);
        poruka.append(";aplikacija:");
        poruka.append(aplikacijaPrijave);
        webKlijent.posaljiPoruku(poruka.toString());
    }

}
