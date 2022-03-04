package org.foi.nwtis.lmrkonjic.projekt.app1.Entitet;

import java.time.Instant;

public class Sjednica
{
    public int idSjednice;
    public String korime;
    public long trenutnoVrijeme;
    public long vrijemeTrajanja;
    public int brojZahtjeva;

    public Sjednica(int idSjednice, String korime, long vrijemeTrajanja, int brojZahtjeva)
    {
        this.idSjednice = idSjednice;
        this.korime = korime;
        this.trenutnoVrijeme = Instant.now().toEpochMilli();
        this.vrijemeTrajanja = vrijemeTrajanja + this.trenutnoVrijeme;
        this.brojZahtjeva = brojZahtjeva;
    }
   
    /**
     * Pomakne trenutno vrijeme i vrijeme trajanja
     * @param vrijemeTrajanja - količina za koju će se vrijeme trajanja pomaknuti
     */
    public void azurirajVrijeme(long vrijemeTrajanja)
    {
        this.trenutnoVrijeme = Instant.now().toEpochMilli();
        this.vrijemeTrajanja = vrijemeTrajanja + this.trenutnoVrijeme;
    }
    
    
}
