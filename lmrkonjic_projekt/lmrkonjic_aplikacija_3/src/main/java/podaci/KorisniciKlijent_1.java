/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package podaci;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

/**
 * Jersey REST client generated for REST resource:korisnici [korisnici/]<br>
 * USAGE:
 * <pre>
 *        KorisniciKlijent_1 client = new KorisniciKlijent_1();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author Mrky
 */
public class KorisniciKlijent_1
{

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8084/lmrkonjic_app_2/rest/";

    public KorisniciKlijent_1()
    {
        client = jakarta.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("korisnici");
    }

    /**
     * @param responseType Class representing the response
     * @return response object (instance of responseType class)@param korime header
     * parameter[REQUIRED]
     * @param korime header parameter[REQUIRED]
     * @param lozinka header parameter[REQUIRED]
     */
    public Response dajKorisnike(String korime, String lozinka) throws ClientErrorException
    {
        return webTarget.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).header("korime", korime).header("lozinka", lozinka).get();
    }
	

    /**
     * @param responseType Class representing the response
     * @param requestEntity request data@return response object (instance of responseType class)
     */
    public Response dodajKorisnika(Object requestEntity) throws ClientErrorException
    {
        return webTarget.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).
                post(jakarta.ws.rs.client.Entity.entity(requestEntity, jakarta.ws.rs.core.MediaType.APPLICATION_JSON));
    }

    public void close()
    {
        client.close();
    }

}
