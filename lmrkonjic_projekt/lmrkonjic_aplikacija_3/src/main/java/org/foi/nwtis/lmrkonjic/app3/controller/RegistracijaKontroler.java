package org.foi.nwtis.lmrkonjic.app3.controller;

import entitet.Korisnik;
import jakarta.jms.Session;
import jakarta.mvc.Controller;
import jakarta.mvc.View;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import podaci.KorisniciKlijent_1;

@Path("registracija")
@Controller
public class RegistracijaKontroler
{

    @GET
    @View("registracijaKorisnika.jsp")
    public String registracijaKorisnika()
    {
        return "registracijaKorisnika.jsp";
    }

    @POST
    public String registracijaKorisnika(
            @Context HttpServletRequest httpZahtjev,
            @FormParam("korime") String korime,
            @FormParam("lozinka") String lozinka,
            @FormParam("prezime") String prezime,
            @FormParam("ime") String ime)
    {
        Korisnik korisnik = new Korisnik(korime, lozinka, prezime, ime);
        KorisniciKlijent_1 klijentRegistracije = new KorisniciKlijent_1();
        Response odgovor = klijentRegistracije.dodajKorisnika(korisnik);
        String odgovorStr = odgovor.readEntity(String.class);
        if (odgovorStr.contains("OK"))
        {
            return "redirect:/prijava";
        } else
        {
            httpZahtjev.setAttribute("greska", odgovorStr);
        }
        return "registracijaKorisnika.jsp";
    }

}
