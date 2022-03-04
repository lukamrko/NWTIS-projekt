package org.foi.nwtis.lmrkonjic.app2.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.lmrkonjic.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

public class MyAirportsLogDAO
{

    /** Provjerava postoji li za dati datum aerodrom u bazi podataka
     * 
     * @param ident - identifikacijska oznaka aerodroma. 
     * @param datum - datum za koji se pregledaje u obliku Stringa
     * @param pbp - objekt PostavkeBazaPodataka
     * @return - vraća je li operacija uspješna ili ne
     */
    public boolean provjeriAerodromZaDatum(String ident, String datum, PostavkeBazaPodataka pbp)
    {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT ident FROM MYAIRPORTSLOG WHERE ident=? AND FLIGHTDATE=?";
        try
        {
            Class.forName(pbp.getDriverDatabase(url));
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit))
            {
                s.setString(1, ident);
                s.setString(2, datum);
                ResultSet rs = s.executeQuery();

                while (rs.next())
                {
                    return true;
                }

            } catch (SQLException ex)
            {
                Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    /** Dodaje aerodrom u bazu podataka (myAirportsLog)
     * 
     * @param ident - identifikcaijska oznaka aerodroma
     * @param datum - datum kada je provjeren aerodrom
     * @param pbp - objekt PostavkeBazaPodataka
     * @return - vraća je li operacija uspješna ili ne
     */
    public boolean dodajAerodromLog(String ident, String datum, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "INSERT INTO MYAIRPORTSLOG (IDENT, FLIGHTDATE, STORED) "
                + "VALUES (?, ?, CURRENT_TIMESTAMP)";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                     Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                     PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, ident);
                s.setString(2, datum);
                int brojAzuriranja = s.executeUpdate();
                return brojAzuriranja == 1;

            } catch (SQLException ex) {
                Logger.getLogger(MyAirportsLogDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportsLogDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    

}
