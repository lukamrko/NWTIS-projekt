package org.foi.nwtis.lmrkonjic.app2.entiteti;

import java.io.Serializable;
import java.util.Objects;

public class Korisnik implements Serializable
{
    public String korime;
    public String lozinka;
    public String prezime;
    public String ime;
    
    public Korisnik()
    {
        this.korime = null;
        this.lozinka = null;
        this.prezime = null;
        this.ime = null;
    }
    

    public Korisnik(String korime, String lozinka, String prezime, String ime)
    {
        this.korime = korime;
        this.lozinka = lozinka;
        this.prezime = prezime;
        this.ime = ime;
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

    public String getPrezime()
    {
        return prezime;
    }

    public void setPrezime(String prezime)
    {
        this.prezime = prezime;
    }

    public String getIme()
    {
        return ime;
    }

    public void setIme(String ime)
    {
        this.ime = ime;
    }
    
    

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.korime);
        hash = 67 * hash + Objects.hashCode(this.lozinka);
        hash = 67 * hash + Objects.hashCode(this.prezime);
        hash = 67 * hash + Objects.hashCode(this.ime);
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final Korisnik other = (Korisnik) obj;
        if (!Objects.equals(this.korime, other.korime))
        {
            return false;
        }
        if (!Objects.equals(this.lozinka, other.lozinka))
        {
            return false;
        }
        if (!Objects.equals(this.prezime, other.prezime))
        {
            return false;
        }
        if (!Objects.equals(this.ime, other.ime))
        {
            return false;
        }
        return true;
    }
    
}
