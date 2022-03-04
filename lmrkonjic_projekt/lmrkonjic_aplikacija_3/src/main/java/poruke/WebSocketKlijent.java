package poruke;

import jakarta.annotation.PostConstruct;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import jakarta.jms.TextMessage;
import jakarta.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ClientEndpoint()
public class WebSocketKlijent
{

    private Session session;

    /**
     * Spaja se na webSocket
     */
    @PostConstruct
    public void spajanje()
    {
        String uri = "ws://localhost:8280/lmrkonjic_aplikacija_4/login";
        try
        {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(this, new URI(uri));
        } catch (IOException | URISyntaxException | DeploymentException ex)
        {
            Logger.getLogger(WebSocketKlijent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Salje dalje poruku  sesiji
     * @param poruka - poruka u pitanju
     */
    public void posaljiPoruku(String poruka)
    {
        System.out.println("Oglasnik poruka: " + poruka);
        try
        {
            session.getBasicRemote().sendText(poruka);
        } catch (IOException ex)
        {
            Logger.getLogger(WebSocketKlijent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * U slučaju primtka poruka da postoji neka metoda koja hvata
     * @param message 
     */
    @OnMessage
    private void onMessage(String message)
    {
        System.out.println(message);
    }
}
