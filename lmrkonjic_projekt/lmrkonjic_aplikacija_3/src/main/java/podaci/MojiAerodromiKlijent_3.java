/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package podaci;

import jakarta.ws.rs.core.Response;

/**
 * Jersey REST client generated for REST resource:mojiAerodromi
 * [mojiAerodromi/{korisnik}/prati/{icao}]<br>
 * USAGE:
 * <pre>
 *        MojiAerodromiKlijent_3 client = new MojiAerodromiKlijent_3();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author Mrky
 */
public class MojiAerodromiKlijent_3
{

    private jakarta.ws.rs.client.WebTarget webTarget;
    private jakarta.ws.rs.client.Client client;
    private static final String BASE_URI = "http://localhost:8084/lmrkonjic_app_2/rest/";

    public MojiAerodromiKlijent_3(String korisnik, String icao)
    {
        client = jakarta.ws.rs.client.ClientBuilder.newClient();
        String resourcePath = java.text.MessageFormat.format("mojiAerodromi/{0}/prati/{1}", new Object[]{korisnik, icao});
        webTarget = client.target(BASE_URI).path(resourcePath);
    }

    public void setResourcePath(String korisnik, String icao)
    {
        String resourcePath = java.text.MessageFormat.format("mojiAerodromi/{0}/prati/{1}", new Object[]{korisnik, icao});
        webTarget = client.target(BASE_URI).path(resourcePath);
    }

    /**
     * @param responseType Class representing the response
     * @return response object (instance of responseType class)@param korime header parameter[REQUIRED]
     * @param lozinka header parameter[REQUIRED]
     */
    public Response izbrisiKorisnikuAerodrom(String korime, String lozinka) throws jakarta.ws.rs.ClientErrorException
    {
        return webTarget.request().header("korime", korime).header("lozinka", lozinka).delete();
    }

    public void close()
    {
        client.close();
    }
    
}
