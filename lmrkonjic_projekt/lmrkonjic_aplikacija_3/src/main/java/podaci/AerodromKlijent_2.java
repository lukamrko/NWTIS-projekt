/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package podaci;

import jakarta.ws.rs.core.Response;

/**
 * Jersey REST client generated for REST resource:aerodromi [aerodromi/{icao}]<br>
 * USAGE:
 * <pre>
 *        AerodromKlijent_2 client = new AerodromKlijent_2();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author Mrky
 */
public class AerodromKlijent_2
{

    private jakarta.ws.rs.client.WebTarget webTarget;
    private jakarta.ws.rs.client.Client client;
    private static final String BASE_URI = "http://localhost:8084/lmrkonjic_app_2/rest/";

    public AerodromKlijent_2(String icao)
    {
        client = jakarta.ws.rs.client.ClientBuilder.newClient();
        String resourcePath = java.text.MessageFormat.format("aerodromi/{0}", new Object[]{icao});
        webTarget = client.target(BASE_URI).path(resourcePath);
    }

    public void setResourcePath(String icao)
    {
        String resourcePath = java.text.MessageFormat.format("aerodromi/{0}", new Object[]{icao});
        webTarget = client.target(BASE_URI).path(resourcePath);
    }

    /**
     * @param responseType Class representing the response
     * @return response object (instance of responseType class)@param korime header parameter[REQUIRED]
     * @param lozinka header parameter[REQUIRED]
     */
    public Response dajAerodromICAO(String korime, String lozinka) throws jakarta.ws.rs.ClientErrorException
    {
        return webTarget.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).header("korime", korime).header("lozinka", lozinka).get();
    }

    public void close()
    {
        client.close();
    }
    
}
