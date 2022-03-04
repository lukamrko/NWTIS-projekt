package poruke;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jms.JMSConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;

@Stateless
public class PosiljateljPoruka
{

    @Inject
    @JMSConnectionFactory("jms/NWTIS_lmrkonjiccf_1")
    private JMSContext context;

    @Resource(lookup = "jms/NWTIS_lmrkonjic_1")
    Queue requestQueue;

    /**
     * Salje poruku
     *
     * @param tekstPoruke - poruka koja se šalje
     */
    public void saljiPoruku(String tekstPoruke)
    {
        TextMessage poruka = context.createTextMessage(tekstPoruke);
        context.createProducer().send(requestQueue, poruka);
        System.out.println("app3-posiljateljPoruka-poruka:" + poruka);

    }
}
