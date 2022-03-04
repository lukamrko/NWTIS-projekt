package org.foi.nwtis.lmrkonjic.app2.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.lmrkonjic.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.rest.podaci.AvionLeti;

public class AirplanesDAO
{

    /**
     * Funkcija dodaje avion u bazu podataka
     *
     * @param a - klasa AvionLeti
     * @param pbp - varijabla PostavkeBazaPodataka
     * @return - vraća je li podatak uspješno dodan u bazu podataka
     */
    public boolean dodajAvion(AvionLeti a, PostavkeBazaPodataka pbp)
    {
        if (!postojiAerodromSlijetanja(a))
        {
            return false;
        }
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "INSERT INTO airplanes (ICAO24, FIRSTSEEN, ESTDEPARTUREAIRPORT, "
                + "LASTSEEN, ESTARRIVALAIRPORT, CALLSIGN, ESTDEPARTUREAIRPORTHORIZDISTANCE, "
                + "ESTDEPARTUREAIRPORTVERTDISTANCE, ESTARRIVALAIRPORTHORIZDISTANCE, "
                + "ESTARRIVALAIRPORTVERTDISTANCE, DEPARTUREAIRPORTCANDIDATESCOUNT,"
                + " ARRIVALAIRPORTCANDIDATESCOUNT, STORED)"
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try
        {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit))
            {

                s.setString(1, a.getIcao24());
                s.setInt(2, a.getFirstSeen());
                s.setString(3, a.getEstDepartureAirport());
                s.setInt(4, a.getLastSeen());
                s.setString(5, a.getEstArrivalAirport());
                s.setString(6, a.getCallsign());
                s.setInt(7, a.getEstDepartureAirportHorizDistance());
                s.setInt(8, a.getEstDepartureAirportVertDistance());
                s.setInt(9, a.getEstArrivalAirportHorizDistance());
                s.setInt(10, a.getEstArrivalAirportVertDistance());
                s.setInt(11, a.getDepartureAirportCandidatesCount());
                s.setInt(12, a.getArrivalAirportCandidatesCount());
                int brojAzuriranja = s.executeUpdate();

                return brojAzuriranja == 1;

            } catch (SQLException ex)
            {
                Logger.getLogger(AirplanesDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(AirplanesDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Funkcija provjerava je li AvionLeti ima aerordomSlijetanja
     *
     * @param a - AvionLeti objekt kojeg se provjerava
     * @return - vraća poruku ima li ili ne
     */
    private boolean postojiAerodromSlijetanja(AvionLeti a)
    {
        if (a.getArrivalAirportCandidatesCount() < 1)
        {
            return false;
        }
        return true;
    }

}
