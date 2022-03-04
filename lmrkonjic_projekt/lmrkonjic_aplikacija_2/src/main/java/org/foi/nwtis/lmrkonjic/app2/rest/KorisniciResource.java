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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import org.foi.nwtis.lmrkonjic.app2.komunikacija.SlanjeZahtjeva;
import org.foi.nwtis.lmrkonjic.app2.entiteti.Korisnik;
import org.foi.nwtis.lmrkonjic.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

@Path("korisnici")
public class KorisniciResource
{

    @Inject
    ServletContext context;

    /**
     * vraća kolekciju korisnika
     *
     * @param korime - header parametar za autentikaciju korisnika - korisnicko ime
     * @param lozinka - header parametar za autentikaciju korisnika - lozinka
     * @return - vraća ArrayList<Korisnik> korisnici
     */
    @GET
    @Produces(
            {
                MediaType.APPLICATION_JSON
            })
    public Response dajKorisnike(
            @HeaderParam("korime") String korime,
            @HeaderParam("lozinka") String lozinka)
    {
        String zahtjev = "AUTHEN " + korime + " " + lozinka;
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        SlanjeZahtjeva sz = new SlanjeZahtjeva(pbp);
        String autentikacija = sz.obradiZahtjev(zahtjev);
        String[] dioAutentikacije = autentikacija.split(" ");
        if (dioAutentikacije.length == 4 && dioAutentikacije[0].equalsIgnoreCase("OK"))
        {
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
                ArrayList<Korisnik> korisnici = konverzijaStringKorisnici(odgovor);
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

    /**
     * vraća podatke izabranog korisnika
     *
     * @param korime - header parametar za autentikaciju korisnika - korisnicko ime
     * @param lozinka - header parametar za autentikaciju korisnika - lozinka
     * @param korisnikTrazi - korisničko ime za korisnika za kojeg se traže podaci
     * @return - objekt tipa Korisnik
     */
    @GET
    @Path("/{korisnik}")
    @Produces(
            {
                MediaType.APPLICATION_JSON
            })
    public Response dajKorisnika(
            @HeaderParam("korime") String korime,
            @HeaderParam("lozinka") String lozinka,
            @PathParam("korisnik") String korisnikTrazi)
    {
        String zahtjev = "AUTHEN " + korime + " " + lozinka;
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        SlanjeZahtjeva sz = new SlanjeZahtjeva(pbp);
        String autentikacija = sz.obradiZahtjev(zahtjev);
        String[] dioAutentikacije = autentikacija.split(" ");
        if (dioAutentikacije.length == 4 && dioAutentikacije[0].equalsIgnoreCase("OK"))
        {
            zahtjev = "LIST " + korime + " " + dioAutentikacije[1] + " " + korisnikTrazi;
            String odgovor = sz.obradiZahtjev(zahtjev);
            if (odgovor.contains("ERROR"))
            {
                return Response
                        .status(Response.Status.NOT_ACCEPTABLE)
                        .entity(odgovor)
                        .build();
            } else
            {
                String[] dioOdgovora = odgovor.split("\"");
                Korisnik korisnik = konverzijaStringKorisnik(dioOdgovora[1]);
                return Response
                        .status(Response.Status.OK)
                        .entity(korisnik)
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

    /**
     * Parsira string zapis u ArrayList<Korisnik>
     *
     * @param odgovor - odgovor iz aplikacije 1 koji se parsira
     * @return - ArrayList<Korisnik>
     */
    private ArrayList<Korisnik> konverzijaStringKorisnici(String odgovor)
    {
        ArrayList<Korisnik> korisnici = new ArrayList<>();
        String[] dijeloviOdgovora = odgovor.split("\"");
        for (int i = 1; i < dijeloviOdgovora.length; i += 2)
        {
            Korisnik korisnik = konverzijaStringKorisnik(dijeloviOdgovora[i]);
            korisnici.add(korisnik);
        }
        return korisnici;
    }

    /**
     * Parsira String u zapis Korisnik
     *
     * @param podaci - potrebni String u koejm se nalaze podaci za korisnika
     * @return
     */
    private Korisnik konverzijaStringKorisnik(String podaci)
    {
        String[] dioPodataka = podaci.split("\t");
        String korime = dioPodataka[0];
        String prezime = dioPodataka[1];
        String ime = dioPodataka[2];
        return new Korisnik(korime, "******", prezime, ime);
    }

    /**
     * dodaje korisnika u bazu podataka.
     *
     * @param korisnik - objekt tipa Korisnik koji se pokušaje dodati u bazu podataka
     * @return - istinu ako je dodano, laž ako nije
     */
    @POST
    @Consumes(
            {
                MediaType.APPLICATION_JSON
            })
    public Response dodajKorisnika(Korisnik korisnik)
    {
        String korime = korisnik.getKorime();
        String lozinka = korisnik.getLozinka();
        String prezime = korisnik.getPrezime();
        String ime = korisnik.getIme();
        if (korime == null || lozinka == null || prezime == null || ime == null)
        {
            return Response
                    .status(Response.Status.OK)
                    .entity("Jedan ili više atributa su prazni!")
                    .build();
        }
        System.out.println("APP2-restServis-unutarDodajKorisnika");
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        String komanda = izgradiKomanduDodajKorisnika(korime, lozinka, prezime, ime);
        SlanjeZahtjeva sz = new SlanjeZahtjeva(pbp);
        String odgovor = sz.obradiZahtjev(komanda);
        if (odgovor.contains("ERROR"))
        {
            return Response
                    .status(Response.Status.NOT_ACCEPTABLE)
                    .entity(odgovor)
                    .build();
        } else
        {
            return Response
                    .status(Response.Status.OK)
                    .entity(odgovor)
                    .build();
        }
    }

    /**
     * Generira komandu koju je potrebno poslati app1 kako bi se korisnik dodao unutar baze podataka
     *
     * @param korime - korisničko ime novo dodanog korisnika
     * @param lozinka - lozinka novo dodanog korisnika
     * @param prezime - prezime novododanog korisnika
     * @param ime - ime novododanog korisnika
     * @return - string za upit koji je potrebno slati aplikaciji 1
     */
    private String izgradiKomanduDodajKorisnika(String korime, String lozinka, String prezime, String ime)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("ADD ");
        sb.append(korime);
        sb.append(" ");
        sb.append(lozinka);
        sb.append(" \"");
        sb.append(prezime);
        sb.append("\" \"");
        sb.append(ime);
        sb.append("\"");
        return sb.toString();
    }
    
    
}
