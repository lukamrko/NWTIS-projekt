package org.foi.nwtis.lmrkonjic.app2.podaci;

import jakarta.ws.rs.core.MultivaluedMap;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.lmrkonjic.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.Lokacija;

public class AirportDAO
{

    /**
     * Funkcija dohvaća aerodrom iz baze podataka po ICAU
     *
     * @param ident - identifikacijska oznaka aerodoma (icao)
     * @param pbp - objekt PostavkeBazaPodataka
     * @return - vraća objekt Aerodrom
     */
    public Aerodrom dohvatiAerodromICAO(String ident, PostavkeBazaPodataka pbp)
    {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT IDENT, NAME, ISO_COUNTRY, COORDINATES  "
                + "FROM airports WHERE ident = ?";
        try
        {
            Class.forName(pbp.getDriverDatabase(url));
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit))
            {
                s.setString(1, ident);
                ResultSet rs = s.executeQuery();

                while (rs.next())
                {
                    String icao = rs.getString("IDENT");
                    String naziv = rs.getString("NAME");
                    String zemlja = rs.getString("ISO_COUNTRY");
                    String[] koordinate = rs.getString("COORDINATES").split(",");
                    String longituda = koordinate[0];
                    String latituda = koordinate[1];
                    Lokacija lokacija = new Lokacija(latituda, longituda);

                    Aerodrom a = new Aerodrom(icao, naziv, zemlja, lokacija);

                    return a;
                }

            } catch (SQLException ex)
            {
                Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Dohvaća sve aerodrome iz baze podataka. Aerodrome je također moguće filtrarati
     *
     * @param pbp - objekt PostavkeBazaPodataka
     * @param parametri - MultivaluedMap u kojem se mogu nalaziti paramteri drzava i naziv
     * @return - vraća listu Aerodroma
     */
    public List<Aerodrom> dohvatiSveAerodrome(PostavkeBazaPodataka pbp, MultivaluedMap<String, String> parametri)
    {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = generirajUpitZadohvacanjeAerodroma(parametri);
        try
        {
            Class.forName(pbp.getDriverDatabase(url));

            List<Aerodrom> aerodromi = new ArrayList<>();

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit))
            {

                while (rs.next())
                {
                    String icao = rs.getString("IDENT");
                    String naziv = rs.getString("NAME");
                    String zemlja = rs.getString("ISO_COUNTRY");
                    String[] koordinate = rs.getString("COORDINATES").split(",");
                    String longituda = koordinate[0];
                    String latituda = koordinate[1];
                    Lokacija lokacija = new Lokacija(latituda, longituda);

                    Aerodrom a = new Aerodrom(icao, naziv, zemlja, lokacija);
                    aerodromi.add(a);
                }
                return aerodromi;

            } catch (SQLException ex)
            {
                Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Generira upit za dohvaćanje Aerodroma koji može poprimiti dodatne uvjete
     *
     * @param parametri - proslijeđena mapa u kojoj se mogu nalazati određeni parametri
     * @return - vraća upit za dohvaćanje aerodroma
     */
    private String generirajUpitZadohvacanjeAerodroma(MultivaluedMap<String, String> parametri)
    {
        StringBuilder upit = new StringBuilder("SELECT IDENT, NAME, ISO_COUNTRY, COORDINATES "
                + "FROM airports WHERE 1");
        if (parametri.containsKey("naziv"))
        {
            String naziv = parametri.getFirst("naziv");
            upit.append(" AND name LIKE '%");
            upit.append(naziv);
            upit.append("%'");
        }
        if (parametri.containsKey("drzava"))
        {
            String drzava = parametri.getFirst("drzava");
            upit.append(" AND ISO_COUNTRY='");
            upit.append(drzava);
            upit.append("'");
        }
        return upit.toString();
    }

    /**
     * Dohvaća broj letova s pojedinog aerodroma
     *
     * @param pbp - objekt PostavkeBazaPodataka
     * @param ICAO - identifikacijska oznaka aerodroma
     * @return - vraća broj aerodroma
     */
    public int dohvatiBrojLetova(PostavkeBazaPodataka pbp, String ICAO)
    {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT COUNT(estDepartureAirport) AS broj FROM airplanes "
                + "WHERE estDepartureAirport=?";
        try
        {
            Class.forName(pbp.getDriverDatabase(url));
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit))
            {
                s.setString(1, ICAO);
                ResultSet rs = s.executeQuery();

                while (rs.next())
                {
                    String broj = rs.getString("broj");
                    return Integer.parseInt(broj);
                }

            } catch (SQLException ex)
            {
                Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    /**
     * Dohvaća prikupljene letova aviona s izabranog aerodroma za određeni dan
     *
     * @param pbp - objekt PostavkeBazaPodataka
     * @param icao - identifikacijska oznaka aerodroma
     * @param dan - dan u formatu gggg-mm-dd.
     * @return - vraća listu avionLeti koji odgovaraju uvjetim
     */
    public ArrayList<AvionLeti> dohvatiLetoveNaDan(PostavkeBazaPodataka pbp, String icao, String dan)
    {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT * FROM airplanes WHERE estDepartureAirport=? AND "
                + "(lastSeen>=? AND lastSeen<=?+86400)";
        try
        {
            Class.forName(pbp.getDriverDatabase(url));
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit))
            {
                s.setString(1, icao);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try
                {
                    Date datum = sdf.parse(dan);
                    long vrijemePocetno = datum.getTime() / 1000;
                    int vrijeme = (int) vrijemePocetno;
                    s.setInt(2, vrijeme);
                    s.setInt(3, vrijeme);
                } catch (ParseException ex)
                {
                    Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
                }
                ResultSet rs = s.executeQuery();
                ArrayList<AvionLeti> avioni = new ArrayList<AvionLeti>();
                while (rs.next())
                {
                    AvionLeti avionLeti = preuzmiPodatkeAvionLeti(rs);
                    avioni.add(avionLeti);
                }
                return avioni;

            } catch (SQLException ex)
            {
                Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Dohvaća podatke s kojim je potrebno popuniti objekt AvionLeti
     *
     * @param rs - jedan red iz baze podataka koji se parisira u elemente objekta AvionLeti
     * @return - vraća objekt AvionLeti
     * @throws SQLException
     */
    private AvionLeti preuzmiPodatkeAvionLeti(ResultSet rs) throws SQLException
    {
        String icao24 = rs.getString("icao24");
        int firstSeen = rs.getInt("firstSeen");
        String estDepartureAirport = rs.getString("estDepartureAirport");
        int lastSeen = rs.getInt("lastSeen");
        String estArrivalAirport = rs.getString("estArrivalAirport");
        String callsign = rs.getString("callsign");
        int estDepartureAirportHorizDistance = rs.getInt("estDepartureAirportHorizDistance");
        int estDepartureAirportVertDistance = rs.getInt("estDepartureAirportVertDistance");
        int estArrivalAirportHorizDistance = rs.getInt("estArrivalAirportHorizDistance");
        int estArrivalAirportVertDistance = rs.getInt("estArrivalAirportVertDistance");
        int departureAirportCandidatesCount = rs.getInt("departureAirportCandidatesCount");
        int arrivalAirportCandidatesCount = rs.getInt("arrivalAirportCandidatesCount");
        AvionLeti avionLeti = new AvionLeti(icao24, firstSeen, estDepartureAirport,
                lastSeen, estArrivalAirport, callsign, estDepartureAirportHorizDistance,
                estDepartureAirportVertDistance, estArrivalAirportHorizDistance,
                estArrivalAirportVertDistance, departureAirportCandidatesCount,
                arrivalAirportCandidatesCount);
        return avionLeti;
    }

}
