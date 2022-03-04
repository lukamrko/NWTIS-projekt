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
import org.foi.nwtis.lmrkonjic.projekt.app1.Entitet.Korisnik;

/**
 * Klasa zadužena za radom tablice korisnici unutar nwtis_lmrkonjic_bp_1 baze podataka
 */
public class KorisnikDAO
{

    private String url;
    private String bpkorisnik;
    private String bplozinka;
    private PostavkeBazaPodataka pbp;

    /**
     * Konstuktor klase KorisnikDAO. Prosljeđuje mu se PostavkeBazePodatak iz koje 
     * učitaje potrebne podatke
     * @param pbp - Objekt PostavkeBazaPodataka
     */
    public KorisnikDAO(PostavkeBazaPodataka pbp)
    {
        this.pbp = pbp;
        this.url = pbp.getServerDatabase() + pbp.getUserDatabase();
        this.bpkorisnik = pbp.getUserUsername();
        this.bplozinka = pbp.getUserPassword();
    }

    /**
     * Pretražuje bazu da vidi postoji li korisnik sa datim korisničkim imenom
     * @param korime - korisničko ime traženog korisnika
     * @return - vraća istinu ako postoji, i laž ako ne postoji traženi korisnik u bazi podataka
     */
    public boolean postojiKorisnik(String korime)
    {
        String upit = "SELECT * FROM korisnici WHERE korime = ?";
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
                ResultSet rs = s.executeQuery();

                while (rs.next())
                {
                    return true;
                }

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
     * Dodaje korisnika u bazu podataka
     * @param korisnik - objekt tipa Korisnik. Služi za dodavanje korisnika u bazu podataka
     * @return - vraća istinu ako je korisnik dodan, a laž ako nije
     */
    public boolean dodajKorisnika(Korisnik korisnik)
    {
        String upit = "INSERT INTO korisnici (korime, lozinka, prezime, ime) "
                + "VALUES (?, ?, ?, ?)";

        try
        {
            Class.forName(this.pbp.getDriverDatabase(this.url));
            try (
                    Connection con = DriverManager.getConnection(this.url, this.bpkorisnik, this.bplozinka);
                    PreparedStatement s = con.prepareStatement(upit))
            {
                s.setString(1, korisnik.korime);
                s.setString(2, korisnik.lozinka);
                s.setString(3, korisnik.prezime);
                s.setString(4, korisnik.ime);
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
     * Pretražuje bazu da vidi postoji li korisnik sa datim korisničkim imenom i lozinkom
     * @param korime - korisničko ime traženog korisnika
     * @param lozinka - lozinka traženog korisnika
     * @return - vraća istinu ako posotji, a laž ako ne postoji
     */
    public boolean postojiKorisnikLozinka(String korime, String lozinka)
    {
        String upit = "SELECT * FROM korisnici WHERE korime = ? AND lozinka=?";
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
                s.setString(2, lozinka);
                ResultSet rs = s.executeQuery();
                while (rs.next())
                {
                    return true;
                }
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
     * Dohvaća sve korisnike iz baze podataka
     * @return - Vraća ArrayListu svih korisnika (sa lažnom lozinkom)
     */
    public ArrayList<Korisnik> dohvatiSveKorisnike()
    {
        String upit = "SELECT * FROM korisnici";
        try
        {
            System.out.println(this.url);
            System.out.println(this.pbp.getDriverDatabase(this.url));
            Class.forName(this.pbp.getDriverDatabase(this.url));
            try (
                    Connection con = DriverManager.getConnection(this.url, this.bpkorisnik, this.bplozinka);
                    PreparedStatement s = con.prepareStatement(upit))
            {
                ResultSet rs = s.executeQuery();
                ArrayList<Korisnik> korisnici = new ArrayList();
                while (rs.next())
                {
                    String korime = rs.getString("korime");
                    String lozinka = "+++++++";
                    String prezime = rs.getString("prezime");
                    String ime = rs.getString("ime");
                    Korisnik korisnik = new Korisnik(korime, lozinka, prezime, ime);
                    korisnici.add(korisnik);
                }
                return korisnici;
            } catch (SQLException ex)
            {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Dohvaća podatke jednog korisnika iz baze podataka
     * @param trazeniKorisnik - korisnicko ime trazenog korisnika
     * @return - Objekt tipa Korisnik (sifra je lazna) koji odgovara traženom korisniku
     */
    public Korisnik dohvatiTrazenogKorisnika(String trazeniKorisnik)
    {
        String upit = "SELECT * FROM korisnici WHERE korime=?";
        try
        {
            System.out.println(this.url);
            System.out.println(this.pbp.getDriverDatabase(this.url));
            Class.forName(this.pbp.getDriverDatabase(this.url));
            try (
                    Connection con = DriverManager.getConnection(this.url, this.bpkorisnik, this.bplozinka);
                    PreparedStatement s = con.prepareStatement(upit))
            {
                s.setString(1, trazeniKorisnik);
                ResultSet rs = s.executeQuery();
                ArrayList<Korisnik> korisnici = new ArrayList();
                while (rs.next())
                {
                    String korime = rs.getString("korime");
                    String lozinka = "+++++++";
                    String prezime = rs.getString("prezime");
                    String ime = rs.getString("ime");
                    Korisnik korisnik = new Korisnik(korime, lozinka, prezime, ime);
                    return korisnik;
                }
            } catch (SQLException ex)
            {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
