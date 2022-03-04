package org.foi.nwtis.lmrkonjic.app4;

import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;

@Startup
@Singleton
public class BankaPoruka
{

    @Setter
    @Getter
    private ArrayList<Poruka> svePoruke;

    private String putanjaPoruka;

    public void ucitajPodatke(String putanjaPoruka)
    {
        this.putanjaPoruka = putanjaPoruka;
        System.out.println("APP4-ucitavanje podataka, putanja:"+this.putanjaPoruka);
        this.svePoruke = new ArrayList<Poruka>();
        try
        {
            FileInputStream fileInputStream = new FileInputStream(this.putanjaPoruka);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            this.svePoruke = (ArrayList) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();

        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(BankaPoruka.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ClassNotFoundException ex)
        {
            Logger.getLogger(BankaPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Poruka poruka : this.svePoruke)
        {
            System.out.println("APP4-Ucitavanje pourka: " + poruka);
        }
    }

    public void dodajPoruku(String porukaStr)
    {
        try
        {
            String[] dijelovi = porukaStr.split(";");

            String[] dio = dijelovi[0].split(":");
            String korime = dio[1];

            dio = dijelovi[1].split(":");
            String lozinka = dio[1];

            dio = dijelovi[2].split(":");
            int idSjednice = Integer.parseInt(dio[1]);

            dio = dijelovi[3].split(":");
            String icao = dio[1];

            dio = dijelovi[4].split(":");
            String akcija = dio[1];

            Poruka poruka = new Poruka(korime, lozinka, idSjednice, icao, akcija);
            this.svePoruke.add(poruka);
        } catch (Exception e)
        {
        }
    }

    //@PreDestroy
    public void obrisiPoruke()
    {
        System.out.println("APP4-brisanje podataka");
        try
        {
            FileOutputStream fileOutputStream = new FileOutputStream(this.putanjaPoruka);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this.svePoruke);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(BankaPoruka.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(BankaPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.svePoruke.clear();
    }

}
