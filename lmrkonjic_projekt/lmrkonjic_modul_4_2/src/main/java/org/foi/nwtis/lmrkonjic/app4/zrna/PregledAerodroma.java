package org.foi.nwtis.lmrkonjic.app4.zrna;

import jakarta.ejb.EJB;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.MeteoPodaci;

@Named(value = "pregledAerodroma")
@RequestScoped
public class PregledAerodroma
{
//    /*
//
//    @EJB
//    OsobniAerodromi osobniAerodromi;
//
//    @Getter
//    @Setter
//    private String odabraniAerodrom;
//
//    private List<Aerodrom> evidentiraniAerodromi = new ArrayList<>();
//
//    public PregledAerodroma()
//    {
//    }
//
//    /**
//     * Prikupla sve evidentirane aerodome
//     * @return - Lista evidntiranih Aerodorma
//     */
//    public List<Aerodrom> getEvidentiraniAerodromi()
//    {
//        //TODO preuzmi aerodrome iz aplikacijske evidenticej
//        evidentiraniAerodromi = osobniAerodromi.dohvatiAerodromeBanke();
//        return evidentiraniAerodromi;
//    }
//
//    /**
//     * Daje MeteoPodatke za odabraniAerodom
//     * @return - MetoPodaci odabranog Aerodroma
//     */
//    public MeteoPodaci dajMeteoPodatke()
//    {
//        System.out.println("Usao unutar dajMeteoPodatke|PA");
//        Meteorolog meteorolog = new Meteorolog();
//        Aerodrom aerodrom = osobniAerodromi.dohvatiAerodromICAO(odabraniAerodrom);
//        MeteoPodaci podaci = meteorolog.dajMeteoPodaci(aerodrom);
//        //TODO preuzeti metoe podatke
//        return podaci;
//    }
//
//    /**
//     * Osvjezava listu aerodorma
//     */
//    public void osvjeziAerodrome()
//    {
//        getEvidentiraniAerodromi();
//        getBrojAerodroma();
//        //TODO preuzmi aerodrome iz app evidencije i nhihov broj
//    }
//
//    /**
//     * Dohvaća broj evidentiranih Aerodroma
//     * @return - broj evidentiranih Aerodroma
//     */
//    public int getBrojAerodroma()
//    {
//        return evidentiraniAerodromi.size();
//    }
//
//    /**
//     * Dohvaća temperaturu iz metoPodataka traženog aerodorma
//     * @return - temperatura traženog aerodorma
//     */
//    public String getTemperatura()
//    {
//        try
//        {
//            String temperatura = dajMeteoPodatke().getTemperatureValue().toString();
//            String mjeraTemperature = dajMeteoPodatke().getTemperatureUnit();
//            return temperatura + " " + mjeraTemperature;
//        } catch (Exception e)
//        {
//            return "";
//        }
//    }
//
//    /**
//     * Dohvaća vlagu iz metoPodataka traženog aerodorma
//     * @return - vlaga traženog aerodorma
//     */
//    public String getVlaga()
//    {
//        try
//        {
//            String vlaga = dajMeteoPodatke().getHumidityValue().toString();
//            String mjeraVlage = dajMeteoPodatke().getHumidityUnit();
//            return vlaga + " " + mjeraVlage;
//        } catch (Exception e)
//        {
//            return "";
//        }
//
//    }
//
//    /**
//     * Dohvaća tlak iz metoPodataka traženog aerodorma
//     * @return - tlak traženog aerodorma
//     */
//    public String getTlak()
//    {
//        try
//        {
//            String tlak = dajMeteoPodatke().getPressureValue().toString();
//            String mjeraTlaka = dajMeteoPodatke().getPressureUnit();
//            return tlak + " " + mjeraTlaka;
//        } catch (Exception e)
//        {
//            return "";
//        }
//    }

}
