package org.foi.nwtis.lmrkonjic.app2.rest;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.lmrkonjic.app2.komunikacija.SlanjeZahtjeva;
import org.foi.nwtis.lmrkonjic.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.lmrkonjic.app2.podaci.AirportDAO;
import org.foi.nwtis.lmrkonjic.app2.podaci.MeteoDAO;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.Meteo;

@Path("aerodromi")
public class AirportsResource
{

    @Inject
    ServletContext context;

    /**
     * Rest Response koji vraća sve aerodrome. Moguće je i filtrirati podatke
     *
     * @param korime - header parametar za autentikaciju korisnika - korisnicko ime
     * @param lozinka - header parametar za autentikaciju korisnika - lozinka
     * @param uriInfo - Context parametar u kojeg se može (ali ne mora) staviti dodatne opcije za
     * filter. Trenutno je moguće staviti naziv i drzava
     * @return - vraća json response traženih Aerodroma
     */
    @GET
    @Produces(
            {
                MediaType.APPLICATION_JSON
            })
    public Response dajAerodrome(
            @HeaderParam("korime") String korime,
            @HeaderParam("lozinka") String lozinka,
            @Context UriInfo uriInfo
    )
    {
        String zahtjev = "AUTHEN " + korime + " " + lozinka;
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        SlanjeZahtjeva sz = new SlanjeZahtjeva(pbp);
        String autentikacija = sz.obradiZahtjev(zahtjev);
        String[] dioAutentikacije = autentikacija.split(" ");
        if (dioAutentikacije.length == 4 && dioAutentikacije[0].equalsIgnoreCase("OK"))
        {
            AirportDAO adao = new AirportDAO();
            MultivaluedMap<String, String> parametri = uriInfo.getQueryParameters();
            List<Aerodrom> aerodromi = adao.dohvatiSveAerodrome(pbp, parametri);
            return Response
                    .status(Response.Status.OK)
                    .entity(aerodromi)
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
     * Vraća informacije za zadani aerodrom
     *
     * @param korime - header parametar za autentikaciju korisnika - korisnicko ime
     * @param lozinka - header parametar za autentikaciju korisnika - lozinka
     * @param icao - identikacijska oznaka aerodroma u pitanju
     * @return - vraća json odgovor sa podacima zadanag aerodroma
     */
    @GET
    @Path("/{icao}")
    @Produces(
            {
                MediaType.APPLICATION_JSON
            })
    public Response dajAerodromICAO(
            @HeaderParam("korime") String korime,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao)
    {
        String zahtjev = "AUTHEN " + korime + " " + lozinka;
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        SlanjeZahtjeva sz = new SlanjeZahtjeva(pbp);
        String autentikacija = sz.obradiZahtjev(zahtjev);
        String[] dioAutentikacije = autentikacija.split(" ");
        if (dioAutentikacije.length == 4 && dioAutentikacije[0].equalsIgnoreCase("OK"))
        {
            AirportDAO adao = new AirportDAO();
            Aerodrom aerodrom = adao.dohvatiAerodromICAO(icao, pbp);
            return Response
                    .status(Response.Status.OK)
                    .entity(aerodrom)
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
     * Vraća broj prikupljenih letova sa zadanog aerodroma
     *
     * @param korime - header parametar za autentikaciju korisnika - korisnicko ime
     * @param lozinka - header parametar za autentikaciju korisnika - lozinka
     * @param icao - identikacijska oznaka aerodroma u pitanju
     * @return - vraća json odgovor u kojemu se samo nalazi broj koji predstavlja letove
     */
    @GET
    @Path("/{icao}/letovi")
    @Produces(
            {
                MediaType.APPLICATION_JSON
            })
    public Response dohvatiLetove(
            @HeaderParam("korime") String korime,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao)

    {
        String zahtjev = "AUTHEN " + korime + " " + lozinka;
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        SlanjeZahtjeva sz = new SlanjeZahtjeva(pbp);
        String autentikacija = sz.obradiZahtjev(zahtjev);
        String[] dioAutentikacije = autentikacija.split(" ");
        if (dioAutentikacije.length == 4 && dioAutentikacije[0].equalsIgnoreCase("OK"))
        {
            AirportDAO adao = new AirportDAO();
            int brojLetova = adao.dohvatiBrojLetova(pbp, icao);
            return Response
                    .status(Response.Status.OK)
                    .entity(brojLetova)
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
     * Vraća prikupljene letova aviona s izabranog aerodroma za određeni dan
     *
     * @param korime - header parametar za autentikaciju korisnika - korisnicko ime
     * @param lozinka - header parametar za autentikaciju korisnika - lozinka
     * @param icao - identikacijska oznaka aerodroma u pitanju
     * @param dan - dan u obliku gggg-mm-dd
     * @return - vraća Response u kojem se nalazi ArrayList<AvionLeti>
     */
    @GET
    @Path("/{icao}/letovi/{dan}")
    @Produces(
            {
                MediaType.APPLICATION_JSON
            })
    public Response dohvatiLetoveNaDan(
            @HeaderParam("korime") String korime,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao,
            @PathParam("dan") String dan)

    {
        String zahtjev = "AUTHEN " + korime + " " + lozinka;
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        SlanjeZahtjeva sz = new SlanjeZahtjeva(pbp);
        String autentikacija = sz.obradiZahtjev(zahtjev);
        String[] dioAutentikacije = autentikacija.split(" ");
        if (dioAutentikacije.length == 4 && dioAutentikacije[0].equalsIgnoreCase("OK"))
        {
            AirportDAO adao = new AirportDAO();
            System.out.println("RESTapp2, icao:" + icao + " |dan:" + dan);
            ArrayList<AvionLeti> avioni = adao.dohvatiLetoveNaDan(pbp, icao, dan);
            return Response
                    .status(Response.Status.OK)
                    .entity(avioni)
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
     * vraća prikupljene meteo podatke za izabrani aerodrom za određeni dan.
     *
     * @param korime - header parametar za autentikaciju korisnika - korisnicko ime
     * @param lozinka - header parametar za autentikaciju korisnika - lozinka
     * @param icao - identikacijska oznaka aerodroma u pitanju
     * @param dan - dan u obliku gggg-mm-dd
     * @return - vraća Response u kojem se nalazi ArrayList<Meteo>
     */
    @GET
    @Path("/{icao}/meteoDan/{dan}")
    @Produces(
            {
                MediaType.APPLICATION_JSON
            })
    public Response dohvatiMeteoDan(
            @HeaderParam("korime") String korime,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao,
            @PathParam("dan") String dan)

    {
        String zahtjev = "AUTHEN " + korime + " " + lozinka;
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        SlanjeZahtjeva sz = new SlanjeZahtjeva(pbp);
        String autentikacija = sz.obradiZahtjev(zahtjev);
        String[] dioAutentikacije = autentikacija.split(" ");
        if (dioAutentikacije.length == 4 && dioAutentikacije[0].equalsIgnoreCase("OK"))
        {
            MeteoDAO meteoDAO = new MeteoDAO(pbp);
            ArrayList<Meteo> meteoPodaci = meteoDAO.dohvatiMeteoPodatkeICAOnaDan(icao, dan);
            System.out.println("RESTapp2, icao:" + icao + " |dan:" + dan);
            return Response
                    .status(Response.Status.OK)
                    .entity(meteoPodaci)
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
     * Vraća jedan meteo objekt za aerodroma u određenom vremenu
     * @param korime - header parametar za autentikaciju korisnika - korisnicko ime
     * @param lozinka - header parametar za autentikaciju korisnika - lozinka
     * @param icao - identikacijska oznaka aerodroma u pitanju
     * @param dan - dan u obliku gggg-mm-dd
     * @return - vraća Response u kojem se nalazi objekt Meteo
     */
    @GET
    @Path("/{icao}/meteoVrijeme/{dan}")
    @Produces(
            {
                MediaType.APPLICATION_JSON
            })
    public Response dohvatiMeteoVrijeme(
            @HeaderParam("korime") String korime,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao,
            @PathParam("dan") String dan)

    {
        String zahtjev = "AUTHEN " + korime + " " + lozinka;
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        SlanjeZahtjeva sz = new SlanjeZahtjeva(pbp);
        String autentikacija = sz.obradiZahtjev(zahtjev);
        String[] dioAutentikacije = autentikacija.split(" ");
        if (dioAutentikacije.length == 4 && dioAutentikacije[0].equalsIgnoreCase("OK"))
        {
            MeteoDAO meteoDAO = new MeteoDAO(pbp);
            Meteo meteoPodatak = meteoDAO.dohvatiMeteoPodatak(icao, dan);
            System.out.println("RESTapp2, icao:" + icao + " |dan:" + dan);
            return Response
                    .status(Response.Status.OK)
                    .entity(meteoPodatak)
                    .build();
        } else
        {
            return Response
                    .status(Response.Status.NOT_ACCEPTABLE)
                    .entity(autentikacija)
                    .build();
        }
    }
}
