package org.foi.nwtis.lmrkonjic.app2.rest;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.lmrkonjic.app2.komunikacija.SlanjeZahtjeva;
import org.foi.nwtis.lmrkonjic.app2.podaci.AirportDAO;
import org.foi.nwtis.lmrkonjic.app2.entiteti.Korisnik;
import org.foi.nwtis.lmrkonjic.app2.podaci.MyAirportDAO;
import org.foi.nwtis.lmrkonjic.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;

@Path("mojiAerodromi")
public class MyAirportsResource
{

    @Inject
    ServletContext context;

    /**
     * Funckija dohvaća sve aerodrome iz tablice myAirports
     *
     * @param korime - header parametar za povjeru korisnika - korisnicko ime
     * @param lozinka - header parametar za povjeru korisnika - lozinka
     * @return - json odgovor sa svim aerodromima iz tablice myAirports
     */
    @GET
    @Produces(
            {
                MediaType.APPLICATION_JSON
            })
    public Response dajAerodrome(
            @HeaderParam("korime") String korime,
            @HeaderParam("lozinka") String lozinka
    )
    {
        String zahtjev = "AUTHEN " + korime + " " + lozinka;
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        SlanjeZahtjeva sz = new SlanjeZahtjeva(pbp);
        String autentikacija = sz.obradiZahtjev(zahtjev);
        String[] dioAutentikacije = autentikacija.split(" ");
        if (dioAutentikacije.length == 4 && dioAutentikacije[0].equalsIgnoreCase("OK"))
        {
            MyAirportDAO madao = new MyAirportDAO();
            List<Aerodrom> aerodromi = madao.dohvatiSveAerodrome(pbp);
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
     * Za pojedini aerodrom dohvaća sve korisnike koji ga prate
     *
     * @param korime - header parametar za povjeru korisnika - korisnicko ime
     * @param lozinka - header parametar za povjeru korisnika - lozinka
     * @param icao - path parametar koji predstavlja identifikacijsku oznaku traženog aerodroma
     * @return - vraća json odgovor sa traženim aerdormima
     */
    @GET
    @Path("/{icao}/prate")
    @Produces(
            {
                MediaType.APPLICATION_JSON
            })
    public Response dohvatiKorisnikeZaIcao(
            @HeaderParam("korime") String korime,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("icao") String icao
    )
    {
        String zahtjev = "AUTHEN " + korime + " " + lozinka;
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        SlanjeZahtjeva sz = new SlanjeZahtjeva(pbp);
        String autentikacija = sz.obradiZahtjev(zahtjev);
        String[] dioAutentikacije = autentikacija.split(" ");
        if (dioAutentikacije.length == 4 && dioAutentikacije[0].equalsIgnoreCase("OK"))
        {
            MyAirportDAO madao = new MyAirportDAO();
            List<String> korisnickaImena = madao.dohvatiKorisnikeZaICAO(pbp, icao);
            if (korisnickaImena == null)
            {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Nije pronađen nijedan korisnik za ICAO")
                        .build();
            }
            zahtjev = "LISTALL " + korime + " " + dioAutentikacije[1];
            String odgovor = sz.obradiZahtjev(zahtjev);
            if (odgovor.contains("ERROR"))
            {
                return Response
                        .status(Response.Status.NOT_ACCEPTABLE)
                        .entity(odgovor)
                        .build();
            } else
            {
                ArrayList<Korisnik> korisnici = konverzijaStringKorisnici(odgovor, korisnickaImena);
                return Response
                        .status(Response.Status.OK)
                        .entity(korisnici)
                        .build();
            }

        } else
        {
            return Response
                    .status(Response.Status.NOT_ACCEPTABLE)
                    .entity(autentikacija)
                    .build();
        }
    }

    private ArrayList<Korisnik> konverzijaStringKorisnici(String odgovor, List<String> korisnickaImena)
    {
        ArrayList<Korisnik> korisnici = new ArrayList<>();
        String[] dijeloviOdgovora = odgovor.split("\"");
        for (int i = 1; i < dijeloviOdgovora.length; i += 2)
        {
            Korisnik korisnik = konverzijaStringKorisnik(dijeloviOdgovora[i], korisnickaImena);
            if (korisnik != null)
            {
                korisnici.add(korisnik);
            }
        }
        return korisnici;
    }

    private Korisnik konverzijaStringKorisnik(String podaci, List<String> korisnickaImena)
    {
        String[] dioPodataka = podaci.split("\t");
        boolean pronasao = false;
        String korime = dioPodataka[0];
        for (String korisnickoIme : korisnickaImena)
        {
            if (korime.equalsIgnoreCase(korisnickoIme))
            {
                pronasao = true;
                break;
            }
        }
        if (!pronasao)
        {
            return null;
        }
        String prezime = dioPodataka[1];
        String ime = dioPodataka[2];
        return new Korisnik(korime, "******", prezime, ime);
    }

    /**
     * Za pojedinog korisnika dohvaća sve aerodrome koje prati
     *
     * @param korime - header parametar za povjeru korisnika - korisnicko ime
     * @param lozinka - header parametar za povjeru korisnika - lozinka
     * @param trazeniKorisnik - path parametar, identifikator korisnika u pitanju
     * @return - json odgovor sa svim aerodromima kojeg korisnik prati
     */
    @GET
    @Path("/{korisnik}/prati")
    @Produces(
            {
                MediaType.APPLICATION_JSON
            })
    public Response dohvatiAerodromeKorisnika(
            @HeaderParam("korime") String korime,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("korisnik") String trazeniKorisnik
    )
    {
        String zahtjev = "AUTHEN " + korime + " " + lozinka;
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        SlanjeZahtjeva sz = new SlanjeZahtjeva(pbp);
        String autentikacija = sz.obradiZahtjev(zahtjev);
        String[] dioAutentikacije = autentikacija.split(" ");
        if (dioAutentikacije.length == 4 && dioAutentikacije[0].equalsIgnoreCase("OK"))
        {
            MyAirportDAO madao = new MyAirportDAO();
            List<Aerodrom> aerodromi = madao.dohvatiAerodromeKorisnika(pbp, trazeniKorisnik);
            if (aerodromi == null)
            {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Nije pronađen nijedan aerodrom za korisnika")
                        .build();
            }
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
     * Zadanom korisniku dodaje novi aerodrom za praćenje
     *
     * @param korime - header parametar za povjeru korisnika - korisnicko ime
     * @param lozinka - header parametar za povjeru korisnika - lozinka
     * @param trazeniKorisnik - path parametar, identifikator korisnika u pitanju
     * @param icao - query parametar, identifikator aerodroma koji se dodaje
     * @return - vraća json odgovor u kojem stoji je li operacija uspjela ili ne
     */
    @POST
    @Path("/{korisnik}/prati")
    @Produces(
            {
                MediaType.APPLICATION_JSON
            })
    public Response dodajAerodromKorisniku(
            @HeaderParam("korime") String korime,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("korisnik") String trazeniKorisnik,
            Aerodrom aerodromDohvaceni
    )
    {
        String icao = aerodromDohvaceni.getIcao();
        if (icao == null)
        {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Krivo unešen aerodrom")
                    .build();
        }
        String zahtjev = "AUTHEN " + korime + " " + lozinka;
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        SlanjeZahtjeva sz = new SlanjeZahtjeva(pbp);
        String autentikacija = sz.obradiZahtjev(zahtjev);
        String[] dioAutentikacije = autentikacija.split(" ");
        if (dioAutentikacije.length == 4 && dioAutentikacije[0].equalsIgnoreCase("OK"))
        {
            MyAirportDAO madao = new MyAirportDAO();
            AirportDAO adao = new AirportDAO();
            Aerodrom aerodrom = adao.dohvatiAerodromICAO(icao, pbp);
            if (aerodrom == null)
            {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Nije pronađen nijedan takav aerodrom!")
                        .build();
            }
            boolean dodan = madao.dodajAerodromKorisniku(icao, trazeniKorisnik, pbp);
            return Response
                    .status(Response.Status.OK)
                    .entity(dodan)
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
     *
     * @param korime - header parametar za povjeru korisnika - korisnicko ime
     * @param lozinka - header parametar za povjeru korisnika - lozinka
     * @param trazeniKorisnik - path parametar, identifikator korisnika u pitanju
     * @param icao - query parametar, identifikator aerodroma koji se briše
     * @return - vraća json odgovor u kojem stoji je li operacija uspjela ili ne
     */
    @DELETE
    @Path("/{korisnik}/prati/{icao}")
    @Produces(
            {
                MediaType.APPLICATION_JSON
            })
    public Response izbrisiKorisnikuAerodrom(
            @HeaderParam("korime") String korime,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("korisnik") String trazeniKorisnik,
            @PathParam("icao") String icao
    )
    {
        String zahtjev = "AUTHEN " + korime + " " + lozinka;
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        SlanjeZahtjeva sz = new SlanjeZahtjeva(pbp);
        String autentikacija = sz.obradiZahtjev(zahtjev);
        String[] dioAutentikacije = autentikacija.split(" ");
        if (dioAutentikacije.length == 4 && dioAutentikacije[0].equalsIgnoreCase("OK"))
        {
            MyAirportDAO madao = new MyAirportDAO();
            AirportDAO adao = new AirportDAO();
            Aerodrom aerodrom = adao.dohvatiAerodromICAO(icao, pbp);
            if (aerodrom == null)
            {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Nije pronađen nijedan takav aerodrom!")
                        .build();
            }
            boolean dodan = madao.izbrisiAerodromKorisniku(icao, trazeniKorisnik, pbp);
            return Response
                    .status(Response.Status.OK)
                    .entity(dodan)
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
