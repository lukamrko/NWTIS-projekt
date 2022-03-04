/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package podaci;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;

/**
 * Jersey REST client generated for REST resource:korisnici [korisnici/{korisnik}]<br>
 * USAGE:
 * <pre>
 *        KorisniciKlijent_2 client = new KorisniciKlijent_2();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author Mrky
 */
public class KorisniciKlijent_2
{

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8084/lmrkonjic_app_2/rest/";

    public KorisniciKlijent_2(String korisnik)
    {
        client = jakarta.ws.rs.client.ClientBuilder.newClient();
        String resourcePath = java.text.MessageFormat.format("korisnici/{0}", new Object[]{korisnik});
        webTarget = client.target(BASE_URI).path(resourcePath);
    }

    public void setResourcePath(String korisnik)
    {
        String resourcePath = java.text.MessageFormat.format("korisnici/{0}", new Object[]{korisnik});
        webTarget = client.target(BASE_URI).path(resourcePath);
    }

    /**
     * @param responseType Class representing the response
     * @return response object (instance of responseType class)@param korime header parameter[REQUIRED]
     * @param lozinka header parameter[REQUIRED]
     */
    public <T> T dajKorisnika(Class<T> responseType, String korime, String lozinka) throws ClientErrorException
    {
        return webTarget.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).header("korime", korime).header("lozinka", lozinka).get(responseType);
    }

    public void close()
    {
        client.close();
    }
    
}
