/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.up.fe.tdin.bookstore.warehouse;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import pt.up.fe.tdin.bookstore.common.WarehouseOrder;

/**
 *
 * @author ctrler
 */
@Singleton
@Startup
@LocalBean
public class WarehouseOperations {

    private List<WarehouseOrder> orders;
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @PostConstruct
    private void postConstruct() {
        orders = new ArrayList<WarehouseOrder>();
    }
    
     public List<WarehouseOrder> getOrderList() {        
        System.out.println("[WarehouseOperations.getOrderList()] called");
        return orders;
    }
     
    public void addOrder(WarehouseOrder wo){
         System.out.println("[WarehouseOperations.addOrder()] called for order " + wo.getOrderId());
         orders.add(wo);
    }
    
    public void completedOrder(int orderId){
        for(WarehouseOrder wo : orders){
            if(wo.getOrderId() == orderId){ //TODO Feio....
                System.out.println("[WarehouseOperations.completedOrder()] called for order " + wo.getOrderId() + " " + wo.getBook().getTitle());
            }
        }
    }
}
