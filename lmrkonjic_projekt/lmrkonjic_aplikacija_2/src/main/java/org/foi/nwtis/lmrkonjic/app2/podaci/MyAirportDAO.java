package org.foi.nwtis.lmrkonjic.app2.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.lmrkonjic.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;

public class MyAirportDAO
{

    /** Za pojedinačni aerodrom dohvati sve korisnike koji ga prate
     * 
     * @param pbp - objekt PostavkeBazaPodataka
     * @param ident - identifikacijska oznaka aerodroma (icao)
     * @return - vraća Listu Korisnika ako neko prati taj aerodrom, inače NULL
     */
    public List<String> dohvatiKorisnikeZaICAO(PostavkeBazaPodataka pbp, String ident)
    {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT username FROM myAirports WHERE ident=?";
        try
        {
            Class.forName(pbp.getDriverDatabase(url));
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit))
            {
                s.setString(1, ident);
                ResultSet rs = s.executeQuery();
                List<String> korisnici = new ArrayList<>();
                while (rs.next())
                {
                    String korisnickoIme = rs.getString("username");
                    korisnici.add(korisnickoIme);
                }
                return korisnici;

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

    /** Dohvaće sve jedinstvene aerdorome iz baze podataka (tablice myAirports)
     * 
     * @param pbp - objekt PostavkeBazaPodataka
     * @return - vraća listu Aerodroma iz tablice myAirports
     */
    public List<Aerodrom> dohvatiSveAerodrome(PostavkeBazaPodataka pbp)
    {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT DISTINCT ident FROM myAirports";
        AirportDAO adao = new AirportDAO();

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
                    Aerodrom a = adao.dohvatiAerodromICAO(icao, pbp);
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

    /** Dohvaće sve aerodrome koje pojedini korisnik prati
     * 
     * @param pbp - objekt PostavkeBazaPodataka
     * @param korisnik - korisik kod kojeg se traže aerodromi
     * @return - vraća listu Aerodroma 
     */
    public List<Aerodrom> dohvatiAerodromeKorisnika(PostavkeBazaPodataka pbp, String korisnik)
    {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT ident FROM myAirports WHERE username=?";
        try
        {
            Class.forName(pbp.getDriverDatabase(url));
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit))
            {
                s.setString(1, korisnik);
                ResultSet rs = s.executeQuery();
                List<Aerodrom> aerodromi = new ArrayList<>();
                AirportDAO adao = new AirportDAO();
                while (rs.next())
                {
                    String ident = rs.getString("ident");
                    Aerodrom aerodrom = adao.dohvatiAerodromICAO(ident, pbp);
                    aerodromi.add(aerodrom);
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
    
    /** Pojedinom korisniku dodaje aerodrom za praćenje
     * 
     * @param ident - identifikacijska oznaka aerodroma. aerodrom koji se dodaje
     * korisniku
     * @param korisnik - korisnik kojem se dodaje aerodrom
     * @param pbp - objekt PostavkeBazaPodataka
     * @return - vraća je li operacija uspjela ili ne
     */
    public boolean dodajAerodromKorisniku(String ident, String korisnik, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "INSERT INTO MYAIRPORTS (ident, username, stored) "
                + "VALUES (?, ?, CURRENT_TIMESTAMP)";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                     Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                     PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, ident);
                s.setString(2, korisnik);

                int brojAzuriranja = s.executeUpdate();

                return brojAzuriranja == 1;

            } catch (SQLException ex) {
                Logger.getLogger(MyAirportDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    /** Pojedinom korisniku brise aerdrom praćenja
     * 
     * @param ident - identifikacijska oznaka aerodroma. aerodrom koji se briše
     * korisniku
     * @param korisnik - korisnik kojemu se briše aerodrom
     * @param pbp - objekt PostavkeBazaPodataka
     * @return - vraća poruku o uspješnosti operacije
     */
    public boolean izbrisiAerodromKorisniku(String ident, String korisnik, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "DELETE FROM MYAIRPORTS WHERE ident=? AND USERNAME=?";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                     Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                     PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, ident);
                s.setString(2, korisnik);

                int brojAzuriranja = s.executeUpdate();

                return brojAzuriranja == 1;

            } catch (SQLException ex) {
                Logger.getLogger(MyAirportDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
