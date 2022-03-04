/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package podaci;

import jakarta.ws.rs.core.Response;

/**
 * Jersey REST client generated for REST resource:mojiAerodromi [mojiAerodromi/{korisnik}/prati]<br>
 * USAGE:
 * <pre>
 *        MojiAerodromiKlijent_4 client = new MojiAerodromiKlijent_4();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author Mrky
 */
public class MojiAerodromiKlijent_4
{

    private jakarta.ws.rs.client.WebTarget webTarget;
    private jakarta.ws.rs.client.Client client;
    private static final String BASE_URI = "http://localhost:8084/lmrkonjic_app_2/rest/";

    public MojiAerodromiKlijent_4(String korisnik)
    {
        client = jakarta.ws.rs.client.ClientBuilder.newClient();
        String resourcePath = java.text.MessageFormat.format("mojiAerodromi/{0}/prati", new Object[]{korisnik});
        webTarget = client.target(BASE_URI).path(resourcePath);
    }

    public void setResourcePath(String korisnik)
    {
        String resourcePath = java.text.MessageFormat.format("mojiAerodromi/{0}/prati", new Object[]{korisnik});
        webTarget = client.target(BASE_URI).path(resourcePath);
    }

    /**
     * @param responseType Class representing the response
     * @return response object (instance of responseType class)@param korime header parameter[REQUIRED]
     * @param lozinka header parameter[REQUIRED]
     */
    public <T> T dohvatiAerodromeKorisnika(Class<T> responseType, String korime, String lozinka) throws jakarta.ws.rs.ClientErrorException
    {
        return webTarget.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).header("korime", korime).header("lozinka", lozinka).get(responseType);
    }

    /**
     * @param responseType Class representing the response
     * @return response object (instance of responseType class)@param korime header parameter[REQUIRED]
     * @param lozinka header parameter[REQUIRED]
     */
    public Response dodajAerodromKorisniku(Object requestEntity, String korime, String lozinka) throws jakarta.ws.rs.ClientErrorException
    {
        return webTarget.request().header("korime", korime).header("lozinka", lozinka).
                post(jakarta.ws.rs.client.Entity.entity(requestEntity, jakarta.ws.rs.core.MediaType.APPLICATION_JSON));
    }

    public void close()
    {
        client.close();
    }
    
}
