package org.foi.nwtis.lmrkonjic.vjezba_03.konfiguracije;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class KonfiguracijaTXT extends KonfiguracijaApstraktna{

    public KonfiguracijaTXT(String nazivDatoteke) {
        super(nazivDatoteke);
    }

    @Override
    public void ucitajKonfiguraciju(String nazivDatoteke) throws NeispravnaKonfiguracija {
        this.obrisiSvePostavke();
        
        if(nazivDatoteke == null || nazivDatoteke.length() == 0){
            throw new NeispravnaKonfiguracija("Nesipravni naziv datoteke: '"+nazivDatoteke+"'");
        }
        
        File f = new File(nazivDatoteke);
        if(f.exists() && f.isFile()){
            try {
                this.postavke.load(new FileInputStream(f));
            } catch (IOException ex) {
                 throw new NeispravnaKonfiguracija("Problem kod učitavanja daoteke konfiguracije: '"+nazivDatoteke+"'!");
            }
        } else {
            throw new NeispravnaKonfiguracija("Datoteka pod nazivom: '"+nazivDatoteke+"' ne postoji ili nije datoteka!");
        }
    }

    @Override
    public void spremiKonfiguraciju(String datoteka) throws NeispravnaKonfiguracija {
       
        if(datoteka == null || datoteka.length() == 0){
            throw new NeispravnaKonfiguracija("Nesipravni naziv datoteke: '"+datoteka+"'");
        }
        
        File f = new File(datoteka);
        if(!f.exists() || (f.exists() && f.isFile())){ 
            try {
                this.postavke.store(new FileOutputStream(f),"NWTiS 2021. Luka Mrkonjić");
            } catch (IOException ex) {
                 throw new NeispravnaKonfiguracija("Problem kod spremanja daoteke konfiguracije: '"+datoteka+"'!");
            }
        } else {
            throw new NeispravnaKonfiguracija("Datoteka pod nazivom: '"+datoteka+"' ne postoji ili nije datoteka!");
        }
    }
    
}
