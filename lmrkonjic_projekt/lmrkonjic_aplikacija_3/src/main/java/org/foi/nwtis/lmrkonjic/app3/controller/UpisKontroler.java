package org.foi.nwtis.lmrkonjic.app3.controller;

import entitet.Korisnik;
import entitet.SlanjeZahtjeva;
import jakarta.inject.Inject;
import jakarta.jms.Session;
import jakarta.mvc.Controller;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import org.foi.nwtis.lmrkonjic.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import podaci.KorisniciKlijent_1;

@Path("upis")
@Controller
public class UpisKontroler
{

    @Inject
    ServletContext context;

    @GET
    @View("upis.jsp")
    public String upisKomande()
    {
        return "../privatno/upis.jsp";
    }

    @POST
    public String upisKomande(
            @Context HttpServletRequest httpZahtjev,
            @FormParam("komanda") String komanda)
    {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        SlanjeZahtjeva slanjeZahtjeva = new SlanjeZahtjeva(pbp);
        String odgovor = slanjeZahtjeva.obradiZahtjev(komanda);
        System.out.println("App3-upis:" + odgovor);
        httpZahtjev.setAttribute("greska", odgovor);
        return "../privatno/upis.jsp";
    }

}
