package org.foi.nwtis.lmrkonjic.app2.rest;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.util.ArrayList;
import org.foi.nwtis.lmrkonjic.app2.komunikacija.SlanjeZahtjeva;
import org.foi.nwtis.lmrkonjic.app2.entiteti.Dnevnik;
import org.foi.nwtis.lmrkonjic.app2.podaci.DnevnikDAO;
import org.foi.nwtis.lmrkonjic.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

@Path("dnevnik")
public class DnevnikResource
{

    @Inject
    ServletContext context;

    /**
     * vraća broj zapisa u dnevniku rada za izabranog korisnika.
     *
     * @param korime - header parametar za autentikaciju korisnika - korisnicko ime
     * @param lozinka - header parametar za autentikaciju korisnika - lozinka
     * @param korisnikTrazi - korisničko ime za korisnika za kojeg se traže podaci
     * @param uriInfo - mogući dodatni parametri koji uključuju od i do
     * @return - broj zapisa u obliku Response
     */
    @GET
    @Path("/{korisnik}/broj")
    @Produces(
            {
                MediaType.APPLICATION_JSON
            })
    public Response dohvatiBrojZapisa(
            @HeaderParam("korime") String korime,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("korisnik") String korisnikTrazi,
            @Context UriInfo uriInfo)
    {
        String zahtjev = "AUTHEN " + korime + " " + lozinka;
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        SlanjeZahtjeva sz = new SlanjeZahtjeva(pbp);
        String autentikacija = sz.obradiZahtjev(zahtjev);
        String[] dioAutentikacije = autentikacija.split(" ");
        if (dioAutentikacije.length == 4 && dioAutentikacije[0].equalsIgnoreCase("OK"))
        {
            DnevnikDAO dnevnikDAO = new DnevnikDAO(pbp);
            MultivaluedMap<String, String> parametri = uriInfo.getQueryParameters();
            int brojDnevnika = dohvatiBrojDnevnika(dnevnikDAO, korisnikTrazi, parametri);
            return Response
                    .status(Response.Status.OK)
                    .entity(brojDnevnika)
                    .build();

        } else
        {
            return Response
                    .status(Response.Status.NOT_ACCEPTABLE)
                    .entity(autentikacija)
                    .build();
        }
    }

    /**
     * pomoćna funkcija za dohvatiBrojZapisa, tj. funkcija koja zapravo vraća broj zapisa
     *
     * @param dnevnikDAO - objekt za komuniciranje s bazom podataka
     * @param korisnikTrazi - korisničko ime korisnika za kojeg se traže zapisti
     * @param parametri - paramteri koji su dobiveni iz rest servisa
     * @return - vraća broj zapisa za gore navedne uvjete
     */
    private int dohvatiBrojDnevnika(
            DnevnikDAO dnevnikDAO,
            String korisnikTrazi,
            MultivaluedMap<String, String> parametri)
    {
        boolean odBool = false;
        boolean doBool = false;

        long odLong = 0;
        long doLong = 0;
        if (parametri.containsKey("od"))
        {
            odBool = true;
            odLong = Long.parseLong(parametri.getFirst("od"));
        }
        if (parametri.containsKey("do"))
        {
            doBool = true;
            doLong = Long.parseLong(parametri.getFirst("do"));
        }
        return dnevnikDAO.dohvatiBrojDnevnika(korisnikTrazi, odBool, doBool, odLong, doLong);
    }

    /**
     * vraća kolekciju zapisa u dnevnik rada za izabranog korisnika.
     *
     * @param korime - header parametar za autentikaciju korisnika - korisnicko ime
     * @param lozinka - header parametar za autentikaciju korisnika - lozinka
     * @param korisnikTrazi - korisničko ime za korisnika za kojeg se traže podaci
     * @param uriInfo - mogući dodatni parametri koji mogu biti od, do, pomak i stranica
     * @return - response u kojem se nalazi ArrayList<Dnevnik> za gore navedene uvjete
     */
    @GET
    @Path("/{korisnik}")
    @Produces(
            {
                MediaType.APPLICATION_JSON
            })
    public Response dohvatiZapiseKorisnika(
            @HeaderParam("korime") String korime,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("korisnik") String korisnikTrazi,
            @Context UriInfo uriInfo)

    {
        String zahtjev = "AUTHEN " + korime + " " + lozinka;
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        SlanjeZahtjeva sz = new SlanjeZahtjeva(pbp);
        String autentikacija = sz.obradiZahtjev(zahtjev);
        String[] dioAutentikacije = autentikacija.split(" ");
        if (dioAutentikacije.length == 4 && dioAutentikacije[0].equalsIgnoreCase("OK"))
        {
            DnevnikDAO dnevnikDAO = new DnevnikDAO(pbp);
            MultivaluedMap<String, String> parametri = uriInfo.getQueryParameters();

            ArrayList<Dnevnik> dnevnici = dohvatiDnevnike(dnevnikDAO, korisnikTrazi, parametri);

            return Response
                    .status(Response.Status.OK)
                    .entity(dnevnici)
                    .build();

        } else
        {
            return Response
                    .status(Response.Status.NOT_ACCEPTABLE)
                    .entity(autentikacija)
                    .build();
        }
    }

    /**
     * Pomoćna metoda za servis dohvatiZapiseKorisnika, odnosno za vraćanje kolekcije zapisa u
     * dnevnik rada za izabranog korisnika. parsira dodatne uvjete te ih kao takve šalje u bazu
     * podataka
     *
     * @param dnevnikDAO - objekt za komuniciranje s bazom podataka
     * @param korisnikTrazi - korisničko ime korisnika za kojeg se traže zapisti
     * @param parametri - paramteri koji su dobievni iz rest servisa
     * @return - vraća ArrayList<Dnevnik> za navedene uvjete
     */
    private ArrayList<Dnevnik> dohvatiDnevnike(
            DnevnikDAO dnevnikDAO,
            String korisnikTrazi,
            MultivaluedMap<String, String> parametri)
    {
        boolean odBool = false;
        boolean doBool = false;
        boolean pomakBool = false;
        boolean stranicaBool = false;

        long odLong = 0;
        long doLong = 0;
        int pomakInt = 0;
        int stranicaInt = 0;
        if (parametri.containsKey("od"))
        {
            odBool = true;
            odLong = Long.parseLong(parametri.getFirst("od"));
        }
        if (parametri.containsKey("do"))
        {
            doBool = true;
            doLong = Long.parseLong(parametri.getFirst("do"));
        }
        if (parametri.containsKey("pomak"))
        {
            pomakBool = true;
            pomakInt = Integer.parseInt(parametri.getFirst("pomak"));
        }
        if (parametri.containsKey("stranica"))
        {
            stranicaBool = true;
            stranicaInt = Integer.parseInt(parametri.getFirst("stranica"));
        }
        return dnevnikDAO.dohvatiSveDnevnike(korisnikTrazi, odBool, doBool, pomakBool,
                stranicaBool, odLong, doLong, pomakInt, stranicaInt);
    }

    @POST
    @Consumes(
            {
                MediaType.APPLICATION_JSON
            })
    public Response dodajZapis(Dnevnik dnevnik)
    {

        String korime = dnevnik.getKorime();
        long vrijemePrimitka = dnevnik.getVrijemePrimitka();
        String komanda = dnevnik.getKomanda();
        String odgovor = dnevnik.getOdgovor();
        if (korime == null || komanda == null || odgovor == null || vrijemePrimitka == 0)
        {
            return Response
                    .status(Response.Status.NOT_ACCEPTABLE)
                    .entity("Došlo je do greške prilikom unosa podataka")
                    .build();
        }
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        DnevnikDAO dnevnikDAO = new DnevnikDAO(pbp);
        boolean odgovorDnevnika = false;
        try
        {
            odgovorDnevnika = dnevnikDAO.dodajZapis(korime, vrijemePrimitka, komanda, odgovor);
        } catch (NumberFormatException numberFormatException)
        {
        }
        if (odgovorDnevnika == false)
        {
            return Response
                    .status(Response.Status.NOT_ACCEPTABLE)
                    .entity("Došlo je do greške prilikom dodavanja")
                    .build();
        } else
        {
            return Response
                    .status(Response.Status.OK)
                    .entity(odgovorDnevnika)
                    .build();
        }
    }

}
