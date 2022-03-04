package org.foi.nwtis.lmrkonjic.projekt.app1.Entitet;

import java.io.Serializable;
import java.util.Objects;

public class Dnevnik implements Serializable
{
    public String korime;
    public long vrijemePrimitka;
    public String komanda;
    public String odgovor;
    
    public Dnevnik()
    {
        this.korime = null;
        this.vrijemePrimitka = 0;
        this.komanda = null;
        this.odgovor = null;
    }
    
    public Dnevnik(String korime, long vrijemePrimitka, String komanda, String odgovor)
    {
        this.korime = korime;
        this.vrijemePrimitka = vrijemePrimitka;
        this.komanda = komanda;
        this.odgovor = odgovor;
    }

    public String getKorime()
    {
        return korime;
    }

    public void setKorime(String korime)
    {
        this.korime = korime;
    }

    public long getVrijemePrimitka()
    {
        return vrijemePrimitka;
    }

    public void setVrijemePrimitka(long vrijemePrimitka)
    {
        this.vrijemePrimitka = vrijemePrimitka;
    }

    public String getKomanda()
    {
        return komanda;
    }

    public void setKomanda(String komanda)
    {
        this.komanda = komanda;
    }

    public String getOdgovor()
    {
        return odgovor;
    }

    public void setOdgovor(String odgovor)
    {
        this.odgovor = odgovor;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.korime);
        hash = 97 * hash + (int) (this.vrijemePrimitka ^ (this.vrijemePrimitka >>> 32));
        hash = 97 * hash + Objects.hashCode(this.komanda);
        hash = 97 * hash + Objects.hashCode(this.odgovor);
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
        final Dnevnik other = (Dnevnik) obj;
        if (this.vrijemePrimitka != other.vrijemePrimitka)
        {
            return false;
        }
        if (!Objects.equals(this.korime, other.korime))
        {
            return false;
        }
        if (!Objects.equals(this.komanda, other.komanda))
        {
            return false;
        }
        if (!Objects.equals(this.odgovor, other.odgovor))
        {
            return false;
        }
        return true;
    }
    
    

    

   
    
}
