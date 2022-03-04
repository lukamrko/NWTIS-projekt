package org.foi.nwtis.lmrkonjic.projekt.app1.Entitet;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

/**
 * Klasa koja predstavlja RestFull Java klijent za dodavanje dnevnika u nwtis_lmrkonjic_bp_2 
 * baze podataka (aplikacija 2)
 */
public class DnevnikKlijent
{

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8084/lmrkonjic_app_2/rest/";

    public DnevnikKlijent()
    {
        client = jakarta.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("dnevnik");
    }


    /**
     * Funkcija koja dodaje zapis dnevnika unutar tablice dnevnik u bazi podataka nwtis_lmrkonjic_bp_2
     * @param requestEntity - Tablica prima serijalizarni objekt Dnevnik kao ulazni parametar
     * @return - vraća odgovor koji može biti true/false, ali i String s oblikom neke greške
     * @throws ClientErrorException 
     */
    public Response dodajZapis(Object requestEntity) throws ClientErrorException
    {
        return webTarget.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).
                post(jakarta.ws.rs.client.Entity.entity(requestEntity, jakarta.ws.rs.core.MediaType.APPLICATION_JSON));
    }

    public void close()
    {
        client.close();
    }
    
}
