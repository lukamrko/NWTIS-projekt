package org.foi.nwtis.lmrkonjic.app4;

import java.io.Serializable;

public class Poruka implements Serializable
{

    private String korime;
    private String lozinka;
    private int idSjednice;
    private String icao;
    private String akcija;

    public Poruka()
    {
        this.korime = null;
        this.lozinka = null;
        this.idSjednice = 0;
        this.icao = null;
        this.akcija = null;
    }
    
    public Poruka(String korime, String lozinka, int idSjednice, String icao, String akcija)
    {
        this.korime = korime;
        this.lozinka = lozinka;
        this.idSjednice = idSjednice;
        this.icao = icao;
        this.akcija = akcija;
    }

    public String getKorime()
    {
        return korime;
    }

    public void setKorime(String korime)
    {
        this.korime = korime;
    }

    public String getLozinka()
    {
        return lozinka;
    }

    public void setLozinka(String lozinka)
    {
        this.lozinka = lozinka;
    }

    public int getIdSjednice()
    {
        return idSjednice;
    }

    public void setIdSjednice(int idSjednice)
    {
        this.idSjednice = idSjednice;
    }

    public String getIcao()
    {
        return icao;
    }

    public void setIcao(String icao)
    {
        this.icao = icao;
    }

    public String getAkcija()
    {
        return akcija;
    }

    public void setAkcija(String akcija)
    {
        this.akcija = akcija;
    }

    @Override
    public String toString()
    {
        return "Poruka{" + "korime=" + korime + ", lozinka=" + lozinka +
                ", idSjednice=" + idSjednice + ", icao=" + icao + ", akcija=" + akcija + '}';
    }
    
    
 
    
    
    
    
}
