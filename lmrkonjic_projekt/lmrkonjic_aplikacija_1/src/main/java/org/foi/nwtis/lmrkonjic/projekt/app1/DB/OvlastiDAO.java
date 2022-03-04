package org.foi.nwtis.lmrkonjic.projekt.app1.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.lmrkonjic.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

/**
 * Klasa zadužena za radom tablice ovlasti unutar nwtis_lmrkonjic_bp_1 baze podataka
 */
public class OvlastiDAO
{

    private String url;
    private String bpkorisnik;
    private String bplozinka;
    private PostavkeBazaPodataka pbp;

    /**
     * Konstuktor klase OvlastiDAO. Prosljeđuje mu se PostavkeBazePodatak iz koje učitaje potrebne
     * podatke
     *
     * @param pbp - Objekt PostavkeBazaPodataka
     */
    public OvlastiDAO(PostavkeBazaPodataka pbp)
    {
        this.pbp = pbp;
        this.url = pbp.getServerDatabase() + pbp.getUserDatabase();
        this.bpkorisnik = pbp.getUserUsername();
        this.bplozinka = pbp.getUserPassword();
    }

    /**
     * Provjera da li traženi korisnik ima određeno podrucje s tim da se pregledaje i polje aktivno
     * @param korime - korisnicko ime trazenog korisnika
     * @param podrucje - podrucje koje se zeli pregledati
     * @param aktivno - stanje podrucje koje se zeli pregledati. moze se pregledavati po aktivnom,
     * ali i neaktivnom podrucju
     * @return - vraća istinu ako ima zadano područje, a vraća laž ako nema
     */
    public boolean imaPodrucje(String korime, String podrucje, boolean aktivno)
    {
        String upit = "SELECT * FROM ovlasti WHERE korime=? AND podrucje=? AND status=?";
        try
        {
            System.out.println(this.url);
            System.out.println(this.pbp.getDriverDatabase(this.url));
            Class.forName(this.pbp.getDriverDatabase(this.url));
            try (
                    Connection con = DriverManager.getConnection(this.url, this.bpkorisnik, this.bplozinka);
                    PreparedStatement s = con.prepareStatement(upit))
            {
                s.setString(1, korime);
                s.setString(2, podrucje);
                s.setBoolean(3, aktivno);
                ResultSet rs = s.executeQuery();
                while (rs.next())
                {
                    return true;
                }
                return false;
            } catch (SQLException ex)
            {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Provjera da li traženi korisnik ima određeno podrucje
     * @param korime - korisnicko ime trazenog korisnika
     * @param podrucje - podrucje koje se zeli pregledati
     * @return - vraća istinu ako ima, a laž ako nema
     */
    public boolean imaPodrucje(String korime, String podrucje)
    {
        String upit = "SELECT * FROM ovlasti WHERE korime=? AND podrucje=?";
        try
        {
            System.out.println(this.url);
            System.out.println(this.pbp.getDriverDatabase(this.url));
            Class.forName(this.pbp.getDriverDatabase(this.url));
            try (
                    Connection con = DriverManager.getConnection(this.url, this.bpkorisnik, this.bplozinka);
                    PreparedStatement s = con.prepareStatement(upit))
            {
                s.setString(1, korime);
                s.setString(2, podrucje);
                ResultSet rs = s.executeQuery();
                while (rs.next())
                {
                    return true;
                }
                return false;
            } catch (SQLException ex)
            {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Azurira trežno podrucje za traženog korisnika u željeno stanje (aktivno ili neaktivno)
     * @param aktivno - stanje u koje se želi ažurirati. Može biti aktivno i neaktivno
     * @param korime - korisničko ime korisnika kojem se želi ažurirati područje
     * @param podrucje - područkje koje će se ažurirati
     * @return - vraća istinu ako je područje uspješno ažurirano, a laž ako nije
     */
    public boolean azurirajPodrucje(boolean aktivno, String korime, String podrucje)
    {
        String upit = "UPDATE ovlasti SET status=? WHERE korime=? AND podrucje=?";
        try
        {
            System.out.println(this.url);
            System.out.println(this.pbp.getDriverDatabase(this.url));
            Class.forName(this.pbp.getDriverDatabase(this.url));
            try (
                    Connection con = DriverManager.getConnection(this.url, this.bpkorisnik, this.bplozinka);
                    PreparedStatement s = con.prepareStatement(upit))
            {
                s.setBoolean(1, aktivno);
                s.setString(2, korime);
                s.setString(3, podrucje);
                int brojAzuriranja = s.executeUpdate();
                return brojAzuriranja == 1;
            } catch (SQLException ex)
            {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Unesi pojedino područje traženom korisniku s određenim stanjem (aktivno/neaktivno)
     * @param korime - korisničko ime traženog korisnika
     * @param podrucje - područje koje se dodaju korisniku
     * @param aktivno - stanje u kojem će biti područje. može biti aktivno ili neaktivno
     * @return - vraća istinu ako je područje dodano, a laž ako nije dodano
     */
    public boolean unesiPodrucje(String korime, String podrucje, boolean aktivno)
    {
        String upit = "INSERT INTO ovlasti (korime, podrucje, status) VALUES (?,?,?)";
        try
        {
            System.out.println(this.url);
            System.out.println(this.pbp.getDriverDatabase(this.url));
            Class.forName(this.pbp.getDriverDatabase(this.url));
            try (
                    Connection con = DriverManager.getConnection(this.url, this.bpkorisnik, this.bplozinka);
                    PreparedStatement s = con.prepareStatement(upit))
            {
                s.setString(1, korime);
                s.setString(2, podrucje);
                s.setBoolean(3, aktivno);
                int brojAzuriranja = s.executeUpdate();
                return brojAzuriranja == 1;
            } catch (SQLException ex)
            {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Dohvaća sva područja koja su pridružena traženom korisniku uz određeni status
     * @param korime - korisničko ime traženog korisnika
     * @param status - status po kojim se želi pretražiti područje
     * @return - ArrayList<String> sa svim područjima koji odgovaraju gore navedenim uvjetima
     */
    public ArrayList<String> dohvatiPodrucja(String korime, boolean status)
    {
        ArrayList<String> podrucja = new ArrayList<String>();
        String upit = "SELECT podrucje FROM ovlasti WHERE korime=? AND status=?";
        try
        {
            System.out.println(this.url);
            System.out.println(this.pbp.getDriverDatabase(this.url));
            Class.forName(this.pbp.getDriverDatabase(this.url));
            try (
                    Connection con = DriverManager.getConnection(this.url, this.bpkorisnik, this.bplozinka);
                    PreparedStatement s = con.prepareStatement(upit))
            {
                s.setString(1, korime);
                s.setBoolean(2, status);
                ResultSet rs = s.executeQuery();
                while (rs.next())
                {
                    String podrucje = rs.getString("podrucje");
                    podrucja.add(podrucje);
                }
                return podrucja;
            } catch (SQLException ex)
            {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return podrucja;
    }

}
