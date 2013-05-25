/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.up.fe.tdin.bookstore.warehouse;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 *
 * @author ctrler
 */
@MessageDriven(mappedName = "jms/myQueue", activationConfig = {
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class myMessageBean implements MessageListener {

    @Resource
    private MessageDrivenContext mdc;
    
    public myMessageBean() {
    }
    
    @Override
    public void onMessage(Message message) {
        try {
            System.out.print("WarehouseQUEUE: " + ((TextMessage) message).getText());
        } catch (JMSException ex) {
            Logger.getLogger(myMessageBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
