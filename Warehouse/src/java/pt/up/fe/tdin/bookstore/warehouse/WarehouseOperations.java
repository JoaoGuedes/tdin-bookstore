/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.up.fe.tdin.bookstore.warehouse;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.ws.WebServiceRef;
import pt.up.fe.tdin.bookstore.common.Book;
import pt.up.fe.tdin.bookstore.common.WarehouseOrder;
import pt.up.fe.tdin.bookstore.store.MyWebService_Service;
import pt.up.fe.tdin.bookstore.warehouse.TransportOrder.State;

/**
 *
 * @author ctrler
 */
@Singleton
@Startup
@LocalBean
public class WarehouseOperations {
    @WebServiceRef(wsdlLocation = "META-INF/wsdl/localhost_8080/myWebService/myWebService.wsdl")
    private MyWebService_Service service;

    private List<WarehouseOrder> orders;
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @PersistenceContext(unitName = "CommonPU")
    private EntityManager em;

    @PostConstruct
    private void postConstruct() {
        orders = new ArrayList<WarehouseOrder>();
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<WarehouseOrder> getOrderList() {        
        System.out.println("[WarehouseOperations.getOrderList()] called");
        
        orders.clear();
        
        Query query = em.createQuery("select o from TransportOrder o where o.status = :status");
        query.setParameter("status", State.NEW);
        //query.setParameter("age", 12);
        List<TransportOrder> result = query.getResultList();
        
        for(TransportOrder to : result){
            
            // Creates a WarehouseOrder for each TransportOrder in the database
            Book book = new Book(to.getBookId(),to.getBookTitle());
            WarehouseOrder wo = new WarehouseOrder(to.getId(), book, to.getQuantity());
            
            // Adds the WarehouseOrder to the list
            orders.add(wo);

            System.err.println("getOrderList() " + to.getId() + " Book: " + to.getBookId() + " Title: " + to.getBookTitle() + " qty " + to.getQuantity());
        }
        
        return orders;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void addOrder(WarehouseOrder wo){
         System.out.println("[WarehouseOperations.addOrder()] called");
         orders.add(wo);
         
         // Saves the order in the Database
         TransportOrder order = new TransportOrder();
         order.setBookId(wo.getBook().getId());
         order.setQuantity(wo.getQuantity());
         order.setBookTitle(wo.getBook().getTitle());
         order.setStatus(State.NEW);
         
         em.persist(order);
    }
    
    /** Completes an order and notifies the store the order has shipped
     * 
     * @param orderId 
     */
    public void completedOrder(long orderId){
        for(WarehouseOrder wo : orders){ //TODO
            if(wo.getOrderId() == orderId){
                System.out.println("[WarehouseOperations.completedOrder()] called for order " + wo.getOrderId() + " " + wo.getBook().getTitle());
                
                warehouseOrderShipped(wo.getBook().getId(), wo.getQuantity());

                // Updates WarehouseOrder status
                updateOrder(wo);
            }
        }
    }

    private void warehouseOrderShipped(int bookId, int qty) {
        pt.up.fe.tdin.bookstore.store.MyWebService port = service.getMyWebServicePort();
        port.warehouseOrderShipped(bookId, qty);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private void updateOrder(WarehouseOrder order){
        
         TransportOrder to = em.find(TransportOrder.class, order.getOrderId());
         to.setStatus(State.DONE);
         em.merge(to);
         
    }

}
