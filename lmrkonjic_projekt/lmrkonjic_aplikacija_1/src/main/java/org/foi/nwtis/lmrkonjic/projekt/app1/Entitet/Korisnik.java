package org.foi.nwtis.lmrkonjic.projekt.app1.Entitet;

import java.util.Objects;

public class Korisnik
{
    public String korime;
    public String lozinka;
    public String prezime;
    public String ime;

    public Korisnik(String korime, String lozinka, String prezime, String ime)
    {
        this.korime = korime;
        this.lozinka = lozinka;
        this.prezime = prezime;
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
