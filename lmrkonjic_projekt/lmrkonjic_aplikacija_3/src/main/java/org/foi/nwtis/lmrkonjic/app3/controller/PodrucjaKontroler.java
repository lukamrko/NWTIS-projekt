package org.foi.nwtis.lmrkonjic.app3.controller;

import entitet.Korisnik;
import entitet.SlanjeZahtjeva;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import org.foi.nwtis.lmrkonjic.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import podaci.KorisniciKlijent_1;

@Path("podrucja")
@Controller
public class PodrucjaKontroler
{

    @Inject
    ServletContext context;

    @Inject
    private Models model;

    @GET
    @View("podrucja.jsp")
    public String podrucjaKorisnika(
            @Context HttpServletRequest httpZahtjev
    )
    {
        HttpSession httpSesija = httpZahtjev.getSession();
        String korime = null;
        String lozinka = null;
        String idSjednice = null;
        try
        {
            korime = httpSesija.getAttribute("korime").toString();
            lozinka = httpSesija.getAttribute("lozinka").toString();
            idSjednice = httpSesija.getAttribute("idSjednice").toString();
        } catch (Exception e)
        {
        }
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        if (!imaPravoPristupa(pbp, korime, idSjednice))
        {
            return "redirect:/pocetna";
        }
        umetniKorisnike(korime, lozinka);
        umetniPodrucja();
        return "../privatno/podrucja.jsp";
    }

    private boolean imaPravoPristupa(PostavkeBazaPodataka pbp, String korime, String idSjednice)
    {
        SlanjeZahtjeva slanjeZahtjeva = new SlanjeZahtjeva(pbp);
        String zahtjev = "AUTHOR " + korime + " " + idSjednice + " administracija";
        String odgovor = slanjeZahtjeva.obradiZahtjev(zahtjev);
        return odgovor.equals("OK");
    }

    private void umetniKorisnike(String korime, String lozinka)
    {
        KorisniciKlijent_1 klijentRegistracije = new KorisniciKlijent_1();
        Response odgovor = klijentRegistracije.dajKorisnike(korime, lozinka);
        ArrayList<Korisnik> korisnici = odgovor.readEntity(new GenericType<ArrayList<Korisnik>>()
        {
        });
        this.model.put("korisnici", korisnici);
    }

    private void umetniPodrucja()
    {
        ArrayList<String> podrucja = new ArrayList<String>();
        //administracija, administracijaAerodroma, pregledKorisnik, pregledJMS, pregledDnevnik, pregledAktivnihKorisnika, pregledAerodroma, 
        podrucja.add("administracija");
        podrucja.add("administracijaAerodroma");
        podrucja.add("pregledKorisnik");
        podrucja.add("pregledJMS");
        podrucja.add("pregledDnevnik");
        podrucja.add("pregledAktivnihKorisnika");
        podrucja.add("pregledAerodroma");
        this.model.put("podrucja", podrucja);
    }

    @POST
    public String registracijaKorisnika(
            @Context HttpServletRequest httpZahtjev,
            @FormParam("korisnik") String korisnikTrazi,
            @FormParam("podrucje") String podrucje,
            @FormParam("aktivacija") String aktivacija
    )
    {
        HttpSession httpSesija = httpZahtjev.getSession();
        String korime = httpSesija.getAttribute("korime").toString();
        String lozinka = httpSesija.getAttribute("lozinka").toString();
        String idSjednice = httpSesija.getAttribute("idSjednice").toString();
        boolean aktiviraj = true;
        if (!aktivacija.equals("aktiviraj"))
        {
            aktiviraj = false;
        }
        String zahtjev = generirajZahtjevZaPodrucje(aktiviraj, korime, idSjednice, podrucje, korisnikTrazi);
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        SlanjeZahtjeva slanjeZahtjeva = new SlanjeZahtjeva(pbp);
        String odgovor = slanjeZahtjeva.obradiZahtjev(zahtjev);
        httpZahtjev.setAttribute("info", odgovor);
        umetniKorisnike(korime, lozinka);
        umetniPodrucja();
        return "../privatno/podrucja.jsp";

    }

    private String generirajZahtjevZaPodrucje(boolean aktiviraj, String korime, String idSjednice,
            String podrucje, String korisnikTrazi)
    {
        StringBuilder sb;
        if (aktiviraj)
        {
            sb = new StringBuilder("GRANT ");
        } else
        {
            sb = new StringBuilder("REVOKE ");
        }
        sb.append(korime);
        sb.append(" ");
        sb.append(idSjednice);
        sb.append(" ");
        sb.append(podrucje);
        sb.append(" ");
        sb.append(korisnikTrazi);
        return sb.toString();
    }

}
