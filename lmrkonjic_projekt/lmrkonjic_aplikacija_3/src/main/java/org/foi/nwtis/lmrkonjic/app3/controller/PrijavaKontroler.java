package org.foi.nwtis.lmrkonjic.app3.controller;

import entitet.SlanjeZahtjeva;
import jakarta.inject.Inject;
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
import org.foi.nwtis.lmrkonjic.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import poruke.WebSocketKlijent;

@Path("prijava")
@Controller
public class PrijavaKontroler
{

    @Inject
    ServletContext context;

    @GET
    @View("prijavaKorisnika.jsp")
    public String prijavaKorisnika()
    {
        return "prijavaKorisnika.jsp";
    }

    @POST
    public String prijavaKorisnika(
            @Context HttpServletRequest httpZahtjev,
            @FormParam("korime") String korime,
            @FormParam("lozinka") String lozinka)
    {
        String authZahtjev = stvoriAuthenZahtjev(korime, lozinka);
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        SlanjeZahtjeva slanjeZahtjeva = new SlanjeZahtjeva(pbp);
        String odgovor = slanjeZahtjeva.obradiZahtjev(authZahtjev);
        if (odgovor.contains("OK"))
        {
            String[] dijeloviOdgovora = odgovor.split(" ");
            int idSjednice = Integer.parseInt(dijeloviOdgovora[1]);
            HttpSession htpSesija = httpZahtjev.getSession();
            htpSesija.setAttribute("korime", korime);
            htpSesija.setAttribute("lozinka", lozinka);
            htpSesija.setAttribute("idSjednice", idSjednice);
            try
            {
                posaljiPoruku(korime, lozinka, idSjednice);
            } catch (Exception e)
            {
            }
            return "redirect:/pocetna";
        } else
        {
            httpZahtjev.setAttribute("greska", odgovor);
        }
        return "prijavaKorisnika.jsp";
    }

    private String stvoriAuthenZahtjev(String korime, String lozinka)
    {
        StringBuilder sb = new StringBuilder("AUTHEN ");
        sb.append(korime);
        sb.append(" ");
        sb.append(lozinka);
        return sb.toString();
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
