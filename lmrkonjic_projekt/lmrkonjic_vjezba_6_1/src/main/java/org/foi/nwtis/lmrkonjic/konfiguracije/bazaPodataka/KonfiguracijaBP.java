package org.foi.nwtis.lmrkonjic.konfiguracije.bazaPodataka;

import java.util.Properties;
import org.foi.nwtis.lmrkonjic.vjezba_03.konfiguracije.Konfiguracija;


public interface KonfiguracijaBP extends Konfiguracija {
    String getAdminDatabase();
    String getAdminPassword();
    String getAdminUsername();
    String getDriverDatabase();
    String getDriverDatabase(String urlBazePodataka);
    Properties getDriversDatabase();
    String getServerDatabase();
    String getUserDatabase();
    String getUserPassword();
    String getUserUsername();  
}
