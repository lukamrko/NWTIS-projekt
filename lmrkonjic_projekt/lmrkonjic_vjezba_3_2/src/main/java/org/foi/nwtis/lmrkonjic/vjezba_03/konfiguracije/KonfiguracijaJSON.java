package org.foi.nwtis.lmrkonjic.vjezba_03.konfiguracije;

import java.io.File;
import java.io.IOException;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KonfiguracijaJSON extends KonfiguracijaApstraktna
{

    public KonfiguracijaJSON(String nazivDatoteke)
    {
        super(nazivDatoteke);
    }

    @Override
    public void ucitajKonfiguraciju(String nazivDatoteke) throws NeispravnaKonfiguracija
    {
        this.obrisiSvePostavke();

        if (nazivDatoteke == null || nazivDatoteke.length() == 0)
        {
            throw new NeispravnaKonfiguracija("Nesipravni naziv datoteke: '" + nazivDatoteke + "'");
        }

        File f = new File(nazivDatoteke);
        if (f.exists() && f.isFile())
        {
            try
            {
                FileReader fileReader = new FileReader(f);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                Gson gson = new Gson();
                this.postavke = gson.fromJson(bufferedReader, Properties.class);
                bufferedReader.close();
                fileReader.close();
            } catch (FileNotFoundException ex)
            {
                Logger.getLogger(KonfiguracijaJSON.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex)
            {
                Logger.getLogger(KonfiguracijaJSON.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    @Override
    public void spremiKonfiguraciju(String datoteka) throws NeispravnaKonfiguracija
    {
        if (datoteka == null || datoteka.length() == 0)
        {
            throw new NeispravnaKonfiguracija("Neispravni naziv datoteke");
        }
        File file = new File(datoteka);
        if (!file.exists() || (file.exists() && file.isFile()))
        {
            try
            {
                Gson gson = new Gson();
                FileWriter fileWriter = new FileWriter(file);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                gson.toJson(this.postavke, bufferedWriter);
                bufferedWriter.close();
                fileWriter.close();
            } catch (IOException ex)
            {
                throw new NeispravnaKonfiguracija("Problem kod učitavanja konfiguracije!");
            }
        } else
        {
            throw new NeispravnaKonfiguracija("Datoteka pod nazivom " + datoteka + " ne postoji ili nije datoteka");
        }
    }
}
