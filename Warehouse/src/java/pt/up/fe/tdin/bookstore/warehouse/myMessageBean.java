/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.up.fe.tdin.bookstore.warehouse;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import pt.up.fe.tdin.bookstore.common.WarehouseOrder;

/**
 *
 * @author ctrler
 */
@MessageDriven(mappedName = "jms/myQueue", activationConfig = {
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class myMessageBean implements MessageListener {
    @EJB
    private WarehouseOperations warehouseOperations;

    @Resource
    private MessageDrivenContext mdc;
    
    public myMessageBean() {
    }
    
    @Override
    public void onMessage(Message message) {
        WarehouseOrder receivedOrder = null;  
        try {
            ObjectMessage objMessage = (ObjectMessage) message;
            receivedOrder = (WarehouseOrder) objMessage.getObject();
        } catch (JMSException ex) {
            Logger.getLogger(myMessageBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(receivedOrder!=null){
            warehouseOperations.addOrder(receivedOrder);
            System.out.print("WarehouseQUEUE: Book:" + receivedOrder.getBook().getTitle() + " | Qty: " + receivedOrder.getQuantity());
        }
        else
            System.err.println("WarehouseQUEUE: Shit has hit the fan.");
    }
}