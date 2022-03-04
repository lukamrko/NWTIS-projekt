package org.foi.nwtis.lmrkonjic.app2.podaci;

import org.foi.nwtis.lmrkonjic.app2.entiteti.Dnevnik;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.lmrkonjic.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

public class DnevnikDAO
{

    private String url;
    private String bpkorisnik;
    private String bplozinka;
    private PostavkeBazaPodataka pbp;

    /**
     * Konstuktor klase DnevnikDAO. Prosljeđuje mu se PostavkeBazePodatak iz koje učitaje potrebne
     * podatke
     *
     * @param pbp - Objekt PostavkeBazaPodataka
     */
    public DnevnikDAO(PostavkeBazaPodataka pbp)
    {
        this.pbp = pbp;
        this.url = pbp.getServerDatabase() + pbp.getUserDatabase();
        this.bpkorisnik = pbp.getUserUsername();
        this.bplozinka = pbp.getUserPassword();
    }

    /**
     * Dodaje zapis unutar tablice dnevnik u nwtis_lmrkonjic_bp_2 bazi podataka
     *
     * @param korime - korisničko ime osobe koja izvršuje zapis
     * @param vrijemePrimitka - vrijeme kad je komanda primljena na obradu
     * @param komanda - komanda u pitanju
     * @param odgovor - odgovor na komandu. može biti ok ili error x, gdje x predstavlja broj
     * @return - vraća true ako je zapis dodan, i false ako zapis nije dodan
     */
    public boolean dodajZapis(String korime, long vrijemePrimitka, String komanda, String odgovor)
    {
        String upit = "INSERT INTO dnevnik(korime, vrijemePrimitka, komanda, odgovor) "
                + "VALUES (?, ?, ?, ?)";
        try
        {
            Class.forName(this.pbp.getDriverDatabase(this.url));
            try (
                    Connection con = DriverManager.getConnection(this.url, this.bpkorisnik, this.bplozinka);
                    PreparedStatement s = con.prepareStatement(upit))
            {
                s.setString(1, korime);
                s.setLong(2, vrijemePrimitka);
                s.setString(3, komanda);
                s.setString(4, odgovor);
                int brojAzuriranja = s.executeUpdate();
                return brojAzuriranja == 1;
            } catch (SQLException ex)
            {
                Logger.getLogger(DnevnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(DnevnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Dohvaća sve zapise dnevnika iz nwtis_lmrkonjic_bp_2 baze pojedinog korisnika uz moguće uvjete
     *
     * @param korisnik - korisničko ime osobe koja je unijela zapis
     * @param odBool - boolean paramater koji ako je true ukazuje da treba gledati paramter odLong
     * @param doBool - boolean paramater koji ako je true ukazuje da treba gledati paramter doLong
     * @param pomakBool - boolean paramater koji ako je true ukazuje da treba gledati paramter
     * pomakInt
     * @param stranicaBool - boolean paramater koji ako je true ukazuje da treba gledati paramter
     * stranicaInt
     * @param odLong - vrijeme u longu OD kojeg se gleda zapis dnevnika
     * @param doLong - vrijeme u longu DO kojeg se gleda zapis dnevnika
     * @param pomakInt - broj zapisa koji se preskače
     * @param stranicaInt - sveukupni broj zapisa
     * @return - vraća listu Dnevnika za gore navedene uvjete
     */
    public ArrayList<Dnevnik> dohvatiSveDnevnike(String korisnik, boolean odBool, boolean doBool,
            boolean pomakBool, boolean stranicaBool, long odLong, long doLong, int pomakInt, int stranicaInt)
    {
        String upit = generirajUpitZaDohvatDnevnika(korisnik, odBool, doBool, pomakBool,
                stranicaBool, odLong, doLong, pomakInt, stranicaInt);
        try
        {
            Class.forName(this.pbp.getDriverDatabase(this.url));
            try (
                    Connection con = DriverManager.getConnection(this.url, this.bpkorisnik, this.bplozinka);
                    PreparedStatement s = con.prepareStatement(upit))
            {
                ResultSet rs = s.executeQuery();
                ArrayList<Dnevnik> dnevnici = new ArrayList();
                while (rs.next())
                {
                    String korime = rs.getString("korime");
                    long vrijemePrimitka = rs.getLong("vrijemePrimitka");
                    String komanda = rs.getString("komanda");
                    String odgovor = rs.getString("odgovor");
                    Dnevnik dnevnik = new Dnevnik(korime, vrijemePrimitka, komanda, odgovor);
                    dnevnici.add(dnevnik);
                }
                return dnevnici;
            } catch (SQLException ex)
            {
                Logger.getLogger(DnevnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(DnevnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Generira upit za dohvaćanje zapisa unutar dnevevnika
     *
     * @param korisnik - korisničko ime osobe koja je unijela zapis
     * @param odBool - boolean paramater koji ako je true ukazuje da treba gledati paramter odLong
     * @param doBool - boolean paramater koji ako je true ukazuje da treba gledati paramter doLong
     * @param pomakBool - boolean paramater koji ako je true ukazuje da treba gledati paramter
     * pomakInt
     * @param stranicaBool - boolean paramater koji ako je true ukazuje da treba gledati paramter
     * stranicaInt
     * @param odLong - vrijeme u longu OD kojeg se gleda zapis dnevnika
     * @param doLong - vrijeme u longu DO kojeg se gleda zapis dnevnika
     * @param pomakInt - broj zapisa koji se preskače
     * @param stranicaInt - sveukupni broj zapisa
     * @return - vraća String koji predstavlja upit koji je potrebno poslati bazi podataka
     */
    private String generirajUpitZaDohvatDnevnika(String korisnik, boolean odBool, boolean doBool,
            boolean pomakBool, boolean stranicaBool, long odLong, long doLong, int pomakInt, int stranicaInt)
    {
        StringBuilder sb = new StringBuilder("SELECT * FROM dnevnik WHERE korime='");
        sb.append(korisnik);
        sb.append("'");
        if (odBool)
        {
            sb.append(" AND vrijemePrimitka>=");
            sb.append(odLong);
        }
        if (doBool)
        {
            sb.append(" AND vrijemePrimitka<=");
            sb.append(doLong);
        }
        if (stranicaBool && !pomakBool)
        {
            sb.append(" LIMIT ");
            sb.append(stranicaInt);
        }
        if (!stranicaBool && pomakBool)
        {
            sb.append(" LIMIT ");
            sb.append(pomakInt);
            sb.append(",");
            int maxBrojZapisaZaKorisnika = dohvatiBrojZapisaKorisnika(korisnik);
            sb.append(maxBrojZapisaZaKorisnika);
        }
        if (stranicaBool && pomakBool)
        {
            sb.append(" LIMIT ");
            sb.append(pomakInt);
            sb.append(",");
            sb.append(stranicaInt);
        }
        return sb.toString();
    }

    /**
     * Dohvaća broj zapisa dnevnika pojedinog korisnika. Služi ukoliko se zapisi preskaču a postoji
     * pomak
     *
     * @param korisnik - korisničko ime korisnika u pitanju
     * @return - vraća broj koji predstavlja sveukupne zapise korisnika
     */
    private int dohvatiBrojZapisaKorisnika(String korisnik)
    {
        String upit = "SELECT COUNT(id) AS broj FROM dnevnik WHERE korime=?";
        try
        {
            Class.forName(pbp.getDriverDatabase(url));
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit))
            {
                s.setString(1, korisnik);
                ResultSet rs = s.executeQuery();

                while (rs.next())
                {
                    return rs.getInt("broj");
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
     * Dohvaća broj zapisa unutar Dnevnika pojedinog korisnika uz moguće uvjete
     *
     * @param korisnik - korisničko ime korisnika u pitanju
     * @param odBool - boolean paramater koji ako je true ukazuje da treba gledati paramter odLong
     * @param doBool - boolean paramater koji ako je true ukazuje da treba gledati paramter doLong
     * @param odLong - vrijeme u longu OD kojeg se gleda zapis dnevnika
     * @param doLong - vrijeme u longu DO kojeg se gleda zapis dnevnika
     * @return - vraća broj koji predstavlja broj zapisa koji odgovaraju gore navedenim uvjetim
     */
    public int dohvatiBrojDnevnika(
            String korisnik,
            boolean odBool,
            boolean doBool,
            long odLong,
            long doLong)
    {
        String upit = generirajUpitZaBrojDnevnika(korisnik, odBool, doBool, odLong, doLong);
        try
        {
            Class.forName(this.pbp.getDriverDatabase(this.url));
            try (
                    Connection con = DriverManager.getConnection(this.url, this.bpkorisnik, this.bplozinka);
                    PreparedStatement s = con.prepareStatement(upit))
            {
                ResultSet rs = s.executeQuery();

                while (rs.next())
                {
                    return rs.getInt("broj");
                }
            } catch (SQLException ex)
            {
                Logger.getLogger(DnevnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(DnevnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    /**
     * metoda koja pomoću parametara generira upi ta bazu podataka
     *
     * @param korisnik - korisničko ime korisnika u pitanju
     * @param odBool - boolean paramater koji ako je true ukazuje da treba gledati paramter odLong
     * @param doBool - boolean paramater koji ako je true ukazuje da treba gledati paramter doLong
     * @param odLong - vrijeme u longu OD kojeg se gleda zapis dnevnika
     * @param doLong - vrijeme u longu DO kojeg se gleda zapis dnevnika
     * @return - vraća String koji predstavlja upit koji je potrebno slati bazi podataka
     */
    private String generirajUpitZaBrojDnevnika(
            String korisnik,
            boolean odBool,
            boolean doBool,
            long odLong,
            long doLong)
    {
        StringBuilder sb = new StringBuilder("SELECT COUNT(id) AS broj FROM dnevnik WHERE korime='");
        sb.append(korisnik);
        sb.append("'");
        if (odBool)
        {
            sb.append(" AND vrijemePrimitka>=");
            sb.append(odLong);
        }
        if (doBool)
        {
            sb.append(" AND vrijemePrimitka<=");
            sb.append(doLong);
        }
        return sb.toString();
    }

}
