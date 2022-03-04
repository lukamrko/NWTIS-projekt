package org.foi.nwtis.lmrkonjic.app4.wsep;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ServerEndpoint("/ws")
public class InfoAerodroma
{

    private List<Session> sesije = new ArrayList<Session>();

    /**
     * Šalje poruku svim sesijama, nakon što primi poruku
     *
     * @param message - poruka koja se šalje
     * @param session - primljena sesija
     */
    @OnMessage
    public void stiglaPoruka(String message, Session session)
    {
        for (Session sesija : sesije)
        {
            try
            {
                sesija.getBasicRemote().sendText(message);
            } catch (IOException ex)
            {
                Logger.getLogger(InfoAerodroma.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("3.3|Stigla WS poruka:" + message);
        //TODO poslati poruku svim aktivnim sjednicama -> mislim da je ovo rješeno
    }

    /**
     * Kad se otvori sesija dodaje se u listu sesija
     *
     * @param session - otvorena sesija
     */
    @OnOpen
    public void onOpen(Session session)
    {
        sesije.add(session);
    }

    /**
     * kad se sesija zatvori miče se iz liste sesija
     *
     * @param session - zatvorena sesija
     */
    @OnClose
    public void onClose(Session session)
    {
        sesije.remove(session);
    }

}
