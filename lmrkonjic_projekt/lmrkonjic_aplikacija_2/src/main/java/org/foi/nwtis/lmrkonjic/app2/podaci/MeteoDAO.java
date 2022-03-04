package org.foi.nwtis.lmrkonjic.app2.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.lmrkonjic.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.rest.podaci.Meteo;
import org.foi.nwtis.rest.podaci.MeteoOriginal;

public class MeteoDAO
{

    private String url;
    private String bpkorisnik;
    private String bplozinka;
    private PostavkeBazaPodataka pbp;

    /**
     * Konstuktor klase MeteoDAO. Prosljeđuje mu se PostavkeBazePodatak iz koje učitaje potrebne
     * podatke
     *
     * @param pbp - Objekt PostavkeBazaPodataka
     */
    public MeteoDAO(PostavkeBazaPodataka pbp)
    {
        this.pbp = pbp;
        this.url = pbp.getServerDatabase() + pbp.getUserDatabase();
        this.bpkorisnik = pbp.getUserUsername();
        this.bplozinka = pbp.getUserPassword();
    }

    /**
     * Dodaje meteo podatak unutar baze podataka
     *
     * @param meteoOriginal - objekt tipa MeteoOriginal u kojem se nalaze različiti vremenski podaci
     * @param ident - identifikacijska oznaka aerodroma (icao) za kojegg se dodaju vremenski podaci
     * @return - true ako je podatak dodan, false ako nije
     */
    public boolean dodajMeteoPodatak(MeteoOriginal meteoOriginal, String ident)
    {
        String upit = dohvatiMeteoUnesiUpit();
        try
        {
            Class.forName(this.pbp.getDriverDatabase(this.url));
            try (
                    Connection con = DriverManager.getConnection(this.url, this.bpkorisnik, this.bplozinka);
                    PreparedStatement s = con.prepareStatement(upit))
            {
                postaviStatementZaUnesi(s, meteoOriginal, ident);
                postaviStatementNesigurnihZaUnesi(s, meteoOriginal);
                System.out.println(s.toString());
                System.out.println(meteoOriginal);
                int brojAzuriranja = s.executeUpdate();
                return brojAzuriranja == 1;

            } catch (SQLException ex)
            {
                Logger.getLogger(MeteoDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(MeteoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Postavlja upit za bazu podataka za one podatke kod koje ne može doći greška prilikom
     * parsiranja
     *
     * @param s - PreparedStatement objekt u kojem se postavljaju sigurni podaci
     * @param meteoOriginal - objekt u kojem se nalaze podaci koje je potrebno postaviti
     * @param ident - identifikacijska oznaka aerodroma (icao) za kojegg se dodaju vremenski podaci
     * @throws SQLException
     */
    private void postaviStatementZaUnesi(PreparedStatement s, MeteoOriginal meteoOriginal, String ident) throws SQLException
    {
        s.setString(1, ident);
        s.setString(2, meteoOriginal.getCoordLat());
        s.setString(3, meteoOriginal.getCoordLon());
        s.setInt(4, meteoOriginal.getWeatherId());
        s.setString(5, meteoOriginal.getWeatherMain());
        s.setString(6, meteoOriginal.getWeatherDescription());
        s.setString(7, meteoOriginal.getWeatherIcon());
        s.setString(8, meteoOriginal.getBase());
        s.setFloat(9, meteoOriginal.getMainTemp());
        s.setFloat(10, meteoOriginal.getMainFeels_like());
        s.setFloat(11, meteoOriginal.getMainPressure());
        s.setInt(12, meteoOriginal.getMainHumidity());
        s.setFloat(13, meteoOriginal.getMainTemp_min());
        s.setFloat(14, meteoOriginal.getMainTemp_max());
        s.setInt(17, meteoOriginal.getVisibility());
        s.setFloat(18, meteoOriginal.getWindSpeed());
        s.setFloat(19, meteoOriginal.getWindDeg());
        s.setInt(21, meteoOriginal.getCloudsAll());
        s.setLong(26, meteoOriginal.getDt());
        s.setString(30, meteoOriginal.getSysCountry());
        s.setInt(31, meteoOriginal.getSysSunrise());
        s.setInt(32, meteoOriginal.getSysSunset());
        s.setInt(33, meteoOriginal.getTimezone());
        s.setInt(34, meteoOriginal.getCityId());
        s.setString(35, meteoOriginal.getCityName());
        s.setInt(36, meteoOriginal.getCod());
    }

    /**
     * Postavlja upit za bazu podataka za one podatke kod koje MOŽE doći do greška prilikom
     * parsiranja. Svaki podatak se pojedinačno pokušaje parsirati te greška na jednom ne utječe na
     * pokušaj parsiranja drugog
     *
     * @param s - PreparedStatement objekt u kojem se postavljaju sigurni podaci
     * @param meteoOriginal - objekt u kojem se nalaze podaci koje je potrebno postaviti
     * @throws SQLException
     */
    private void postaviStatementNesigurnihZaUnesi(PreparedStatement s, MeteoOriginal meteoOriginal) throws SQLException
    {
        if (meteoOriginal.getMainSea_level() == null)
        {
            s.setNull(15, java.sql.Types.NULL);
        } else
        {
            s.setFloat(15, meteoOriginal.getMainSea_level());
        }
        if (meteoOriginal.getMainGrnd_level() == null)
        {
            s.setNull(16, java.sql.Types.NULL);
        } else
        {
            s.setFloat(16, meteoOriginal.getMainGrnd_level());
        }
        if (meteoOriginal.getWindGust() == null)
        {
            s.setNull(20, java.sql.Types.NULL);
        } else
        {
            s.setFloat(20, meteoOriginal.getWindGust());
        }
        if (meteoOriginal.getRain1h() == null)
        {
            s.setNull(22, java.sql.Types.NULL);
        } else
        {
            s.setFloat(22, meteoOriginal.getRain1h());
        }
        if (meteoOriginal.getRain3h() == null)
        {
            s.setNull(23, java.sql.Types.NULL);
        } else
        {
            s.setFloat(23, meteoOriginal.getRain3h());
        }
        if (meteoOriginal.getSnow1h() == null)
        {
            s.setNull(24, java.sql.Types.NULL);
        } else
        {
            s.setFloat(24, meteoOriginal.getSnow1h());
        }
        if (meteoOriginal.getSnow3h() == null)
        {
            s.setNull(25, java.sql.Types.NULL);
        } else
        {
            s.setFloat(25, meteoOriginal.getSnow3h());
        }
        if (meteoOriginal.getSysType() == null)
        {
            s.setNull(27, java.sql.Types.NULL);
        } else
        {
            s.setInt(27, meteoOriginal.getSysType());
        }
        if (meteoOriginal.getSysId() == null)
        {
            s.setNull(28, java.sql.Types.NULL);
        } else
        {
            s.setInt(28, meteoOriginal.getSysId());
        }
        if (meteoOriginal.getSysMessage() == null)
        {
            s.setNull(29, java.sql.Types.NULL);
        } else
        {
            s.setFloat(29, meteoOriginal.getSysMessage());
        }
    }

    /**
     * Generira upit koji se šalje bazi podataka za unos podataka
     *
     * @return - gore navedeni upit u obliku string
     */
    private String dohvatiMeteoUnesiUpit()
    {
        return "INSERT INTO meteo (ident, latitude, longitude, weatherId, weatherMain, "
                + "weatherDescription, weatherIcon, base, mainTemp, mainFeelsLike, "
                + "mainPressure, mainHumidity, mainTempMin, mainTempMax, mainSeaLevel, "
                + "mainGrndLevel, visibility, windSpeed, windDeg, windGust, cloudsAll, "
                + "rain1h, rain3h, snow1h, snow3h, date, sysType, sysId, sysMessage, "
                + "sysCountry, sysSunrise, sysSunset, timezone, cityId, cityName, cod) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
                + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    /**
     * Dohvaća meteo podatke za ICAO na pojedini dan
     *
     * @param icao - identifkacijska oznaka aerodroma za kojeg se dohvaćaju podaci
     * @param dan - dan na koji se traže podaci. koristi se yyyy-MM-dd format za vrijeme
     * @return - vraća listu Meteo podataka
     */
    public ArrayList<Meteo> dohvatiMeteoPodatkeICAOnaDan(String icao, String dan)
    {
        String upit = "SELECT * FROM meteo WHERE ident=? AND date>=? AND date<=?+86400";
        try
        {
            Class.forName(this.pbp.getDriverDatabase(this.url));
            try (
                    Connection con = DriverManager.getConnection(this.url, this.bpkorisnik, this.bplozinka);
                    PreparedStatement s = con.prepareStatement(upit))
            {
                postaviStatementZaDohvacanjeNaDan(s, icao, dan);
                ResultSet rs = s.executeQuery();
                ArrayList<Meteo> meteoi = new ArrayList<Meteo>();
                while (rs.next())
                {
                    Meteo meteo = preuzmiMeteoPodatke(rs);
                    meteoi.add(meteo);
                }
                return meteoi;

            } catch (SQLException ex)
            {
                Logger.getLogger(MeteoDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(MeteoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Postavlja upit za dohvaćanje podataka na određeni dan
     *
     * @param s - PreparedStatement objekt na kojem se dodavaju svojstva
     * @param icao - identifikacijska oznaka aerodroma
     * @param dan - dan na koji se traže podaci. koristi se yyyy-MM-dd format za vrijeme. dan se
     * pretvara u epoch vrijeme
     * @throws SQLException
     */
    private void postaviStatementZaDohvacanjeNaDan(PreparedStatement s, String icao, String dan) throws SQLException
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
        }
    }

    /**
     * Generira Meteo objekt za dobiveni red iz baze podataka
     *
     * @param rs - red iz baze podataka
     * @return - vraća Meteo objekt za zadani red iz baze podataka
     * @throws SQLException
     */
    private Meteo preuzmiMeteoPodatke(ResultSet rs) throws SQLException
    {
        String ident = rs.getString("ident");
        Long date = rs.getLong("date");
        String longituda = rs.getString("longitude");
        String latitude = rs.getString("latitude");
        int weatherId = rs.getInt("weatherId");
        String weatherMain = rs.getString("weatherMain");
        String weatherDescription = rs.getString("weatherDescription");
        String base = rs.getString("base");
        Float mainTemp = rs.getFloat("mainTemp");
        Float mainFeels_like = rs.getFloat("mainFeelsLike");
        Float mainPressure = rs.getFloat("mainPressure");
        int mainHumidity = rs.getInt("mainHumidity");
        Float mainTemp_min = rs.getFloat("mainTempMin");
        Float mainTemp_max = rs.getFloat("mainTempMax");
        int visibility = rs.getInt("visibility");
        Float windSpeed = rs.getFloat("windSpeed");
        int windDeg = rs.getInt("windDeg");
        int cloudsAll = rs.getInt("cloudsAll");
        String sysCountry = rs.getString("sysCountry");
        int sysSunrise = rs.getInt("sysSunrise");
        int sysSunset = rs.getInt("sysSunset");
        int timezone = rs.getInt("timezone");
        int cityId = rs.getInt("cityId");
        String cityName = rs.getString("cityName");
        int cod = rs.getInt("cod");
        String weatherIcon = "";
        try
        {
            weatherIcon = rs.getString("weatherIcon");
        } catch (SQLException sQLException)
        {
        }

        Float mainSeaLevel = Float.NaN;
        Float mainGrndLevel = Float.NaN;
        Float windGust = Float.NaN;
        Float rain1h = Float.NaN;
        Float rain3h = Float.NaN;
        Float snow1h = Float.NaN;
        Float snow3h = Float.NaN;
        Float sysMessage = Float.NaN;
        try
        {
            mainSeaLevel = rs.getFloat("mainSeaLevel");
        } catch (SQLException sQLException)
        {
        }
        try
        {
            mainGrndLevel = rs.getFloat("mainGrndLevel");
        } catch (SQLException sQLException)
        {
        }
        try
        {
            windGust = rs.getFloat("windGust");
        } catch (SQLException sQLException)
        {
        }
        try
        {
            rain1h = rs.getFloat("rain1h");
        } catch (SQLException sQLException)
        {
        }
        try
        {
            rain3h = rs.getFloat("rain3h");
        } catch (SQLException sQLException)
        {
        }
        try
        {
            snow1h = rs.getFloat("snow1h");
        } catch (SQLException sQLException)
        {
        }
        try
        {
            snow3h = rs.getFloat("snow3h");
        } catch (SQLException sQLException)
        {
        }
        try
        {
            sysMessage = rs.getFloat("sysMessage");
        } catch (SQLException sQLException)
        {
        }

        int sysType = 0;
        int sysId = 0;
        try
        {
            sysType = rs.getInt("sysType");
        } catch (SQLException sQLException)
        {
        }
        try
        {
            sysId = rs.getInt("sysId");
        } catch (SQLException sQLException)
        {
        }
        Meteo meteo = new Meteo(ident, date, longituda, latitude, weatherId, weatherMain,
                weatherDescription, weatherIcon, base, mainTemp, mainFeels_like, mainPressure,
                mainHumidity, mainTemp_min, mainTemp_max, mainSeaLevel, mainGrndLevel, visibility,
                windSpeed, windDeg, windGust, cloudsAll, rain1h, rain3h, snow1h, snow3h, sysType,
                sysId, sysMessage, sysCountry, sysSunrise, sysSunset, timezone, cityId, cityName, cod);
        return meteo;
    }

    /**
     * Vraća jedan objekt Meteo podatka za točno određeni dan
     *
     * @param icao - identifikacijska oznaka aerodroma za kojeg se traže Meteo podaci
     * @param dan - Timestamp za koji se traži meteo podatak. u obliku je yyyy-MM-dd hh:mm:ss
     * @return - vraća Meteo objekt za gore navadenu funkciju
     */
    public Meteo dohvatiMeteoPodatak(String icao, String dan)
    {
        String upit = "SELECT * FROM meteo WHERE ident=? and date>=? ORDER BY date LIMIT 1";
        try
        {
            Class.forName(this.pbp.getDriverDatabase(this.url));
            try (
                    Connection con = DriverManager.getConnection(this.url, this.bpkorisnik, this.bplozinka);
                    PreparedStatement s = con.prepareStatement(upit))
            {
                postaviStatementZa1MeteoPodatak(s, icao, dan);
                ResultSet rs = s.executeQuery();
                while (rs.next())
                {
                    Meteo meteo = preuzmiMeteoPodatke(rs);
                    return meteo;
                }
            } catch (SQLException ex)
            {
                Logger.getLogger(MeteoDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(MeteoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Postavlja statement za dohvaćanje jednog meteo podatka
     *
     * @param s - PreparedStatement objekt koji se postavlja
     * @param icao - identifikacijska oznaka aerodroma za kojeg se traže Meteo podaci
     * @param dan - Timestamp za koji se traži meteo podatak. u obliku je yyyy-MM-dd hh:mm:ss.
     * pretvara se u epoch vrijeme
     * @throws SQLException
     */
    private void postaviStatementZa1MeteoPodatak(PreparedStatement s, String icao, String dan)
            throws SQLException
    {
        s.setString(1, icao);
        boolean timestamp = false;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try
        {
            Date datum = dateFormat.parse(dan);
            long longVrijeme = datum.getTime() / 1000;
            int vrijeme = (int) longVrijeme;
            s.setInt(2, vrijeme);
            timestamp = true;
        } catch (ParseException ex)
        {
            Logger.getLogger(AirportDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!timestamp)
        {
            long longVrijeme = Long.parseLong(dan);
            int vrijeme = (int) longVrijeme;
            s.setInt(2, vrijeme);
        }
    }

}
