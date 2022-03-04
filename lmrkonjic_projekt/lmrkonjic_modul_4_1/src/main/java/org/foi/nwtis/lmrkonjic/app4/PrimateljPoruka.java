package org.foi.nwtis.lmrkonjic.app4;

import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.JMSConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

@MessageDriven(mappedName = "jms/NWTIS_lmrkonjic_1")
public class PrimateljPoruka implements MessageListener
{

    @Inject
    @JMSConnectionFactory("jms/NWTIS_lmrkonjiccf_1")
    private JMSContext context;
    
    @EJB
    private BankaPoruka bankaPoruka;
    

    /**
     * Prilikom primanja poruke proslijedi je dalje
     * @param message - poruka koja je prosliješenja
     */
    @Override
    public void onMessage(Message message)
    {
        System.out.println("App4-na primljenu poruku:"+message);
        String poruka;
        try
        {
            poruka = ((TextMessage) message).getText();
            this.bankaPoruka.dodajPoruku(poruka);
            System.out.println("App4-modul_4_1|PrimateljPoruka: " + poruka);
        } catch (JMSException ex)
        {
            Logger.getLogger(PrimateljPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
