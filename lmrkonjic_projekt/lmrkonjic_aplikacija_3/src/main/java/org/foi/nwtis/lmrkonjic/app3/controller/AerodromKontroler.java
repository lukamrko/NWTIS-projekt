package org.foi.nwtis.lmrkonjic.app3.controller;

import entitet.Korisnik;
import entitet.SlanjeZahtjeva;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import org.foi.nwtis.lmrkonjic.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;
import podaci.AerodromKlijent_1;
import podaci.AerodromKlijent_2;
import podaci.MojiAerodromiKlijent_1;
import podaci.MojiAerodromiKlijent_2;
import podaci.MojiAerodromiKlijent_3;
import podaci.MojiAerodromiKlijent_4;

import poruke.PosiljateljPoruka;

@Path("aerodrom")
@Controller
public class AerodromKontroler
{
    
    @EJB
    PosiljateljPoruka posiljateljPoruka;

    @Inject
    ServletContext context;

    @Inject
    private Models model;

    @GET
    @View("aerodrom.jsp")
    public String prikazAerodroma(@Context HttpServletRequest httpZahtjev)
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
        umetniIcaoe(korime, lozinka);
        umetniAerodrome(korime, lozinka);
        return "../privatno/aerodrom.jsp";
    }

    private boolean imaPravoPristupa(PostavkeBazaPodataka pbp, String korime, String idSjednice)
    {
        SlanjeZahtjeva slanjeZahtjeva = new SlanjeZahtjeva(pbp);
        String zahtjev = "AUTHOR " + korime + " " + idSjednice + " administracijaAerodroma";
        String odgovor = slanjeZahtjeva.obradiZahtjev(zahtjev);
        return odgovor.equals("OK");
    }

    private void umetniIcaoe(String korime, String lozinka)
    {
        AerodromKlijent_1 aerodromKlijent_1 = new AerodromKlijent_1();
        Response odgovor = aerodromKlijent_1.dajAerodrome(korime, lozinka);
        ArrayList<Aerodrom> aerodromi = odgovor.readEntity(new GenericType<ArrayList<Aerodrom>>()
        {
        });
        this.model.put("icaoDodati", aerodromi);
    }

    private void umetniAerodrome(String korime, String lozinka)
    {
        MojiAerodromiKlijent_1 mojiAerodromiKlijent_1 = new MojiAerodromiKlijent_1();
        Response odgovor = mojiAerodromiKlijent_1.dajAerodrome(korime, lozinka);
        ArrayList<Aerodrom> aerodromi = odgovor.readEntity(new GenericType<ArrayList<Aerodrom>>()
        {
        });
        this.model.put("aerodromi", aerodromi);
    }

    @POST
    public String aerodromiRad(
            @Context HttpServletRequest httpZahtjev,
            @FormParam("aerodrom") String icaoTrazeni,
            @FormParam("icao") String icaoDodati,
            @FormParam("vrsta") String vrsta)
    {
        HttpSession httpSesija = httpZahtjev.getSession();
        String korime = httpSesija.getAttribute("korime").toString();
        String lozinka = httpSesija.getAttribute("lozinka").toString();
        String idSjednice = httpSesija.getAttribute("idSjednice").toString();
        String odgovor = null;
        switch (vrsta)
        {
            case "prikazi":
                prikaziKorisnike(icaoTrazeni, korime, lozinka);
                break;
            case "prestani":
                odgovor = prestaniPratiti(icaoTrazeni, korime, lozinka, idSjednice);
                break;
            case "dodaj":
                odgovor = dodajAerodrom(icaoDodati, korime, lozinka, idSjednice);
                break;
        }
        if (odgovor != null)
        {
            httpZahtjev.setAttribute("info", odgovor);
        }
        umetniAerodrome(korime, lozinka);
        umetniIcaoe(korime, lozinka);

        return "../privatno/aerodrom.jsp";
    }

    private void prikaziKorisnike(String icaoTrazeni, String korime, String lozinka) throws ClientErrorException
    {
        MojiAerodromiKlijent_2 mojiAerodromiKlijent_2 = new MojiAerodromiKlijent_2(icaoTrazeni);
        Response odgovor = mojiAerodromiKlijent_2.dohvatiKorisnikeZaIcao(korime, lozinka);
        ArrayList<Korisnik> korisnici = odgovor.readEntity(new GenericType<ArrayList<Korisnik>>()
        {
        });
        this.model.put("korisnici", korisnici);
    }

    private String prestaniPratiti(String icaoTrazeni, String korime, String lozinka, String idSjednice)
    {
        MojiAerodromiKlijent_3 mojiAerodromiKlijent_3 = new MojiAerodromiKlijent_3(korime, icaoTrazeni);
        Response odgovor = mojiAerodromiKlijent_3.izbrisiKorisnikuAerodrom(korime, lozinka);
        String odgovorStr = odgovor.readEntity(String.class);
        try
        {
            posaljiPoruku(korime, lozinka, idSjednice, icaoTrazeni, "deaktivaraj");
        } catch (Exception e)
        {
        }
        return odgovorStr;
    }

    private String dodajAerodrom(String icaoDodati, String korime, String lozinka, String idSjednice)
    {
        AerodromKlijent_2 aerodromKlijent_2 = new AerodromKlijent_2(icaoDodati);
        Response aerodromRes = aerodromKlijent_2.dajAerodromICAO(korime, lozinka);
        Aerodrom aerodrom = aerodromRes.readEntity(Aerodrom.class);
        System.out.println("app3-dodajAerodrom:" + aerodrom.getIcao());
        MojiAerodromiKlijent_4 mojiAerodromiKlijent_4 = new MojiAerodromiKlijent_4(korime);
        Response odgovor = mojiAerodromiKlijent_4.dodajAerodromKorisniku(aerodrom, korime, lozinka);
        String odgovorStr = odgovor.readEntity(String.class);
        try
        {
            posaljiPoruku(korime, lozinka, idSjednice, icaoDodati, "aktiviraj");
        } catch (Exception e)
        {
        }
        return odgovorStr;
    }

    private void posaljiPoruku(String korime, String lozinka, String idSjednice, String icao, String akcija)
    {
        StringBuilder poruka = new StringBuilder("korime:");
        poruka.append(korime);
        poruka.append(";lozinka:");
        poruka.append(lozinka);
        poruka.append(";idSjednice:");
        poruka.append(idSjednice);
        poruka.append(";aerodrom:");
        poruka.append(icao);
        poruka.append(";akcija:");
        poruka.append(akcija);
        System.out.println("app3-kontroler-poruka:" + poruka);
        this.posiljateljPoruka.saljiPoruku(poruka.toString());
    }

}
