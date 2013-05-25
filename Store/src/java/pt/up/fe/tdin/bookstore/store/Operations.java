/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.up.fe.tdin.bookstore.store;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 *
 * @author joaoguedes
 */
@Singleton
@Startup
@LocalBean
public class Operations {

 /**
     * List of placed orders
     */
    
    @PostConstruct
    private void postConstruct() {
       System.out.println("EJB Created");
       populateBookList();
    }
    
    private List<Order> orders;
    
    /**
     * List of available books
     */
    private List<Book> bookList;

    public Operations () {
        orders = new ArrayList();
        bookList = new ArrayList();
    }
    
    /**
     * Populates book list. 
     * Should be fetched from a file in the future (?).
     */
    public void populateBookList() {
        System.out.println("[populateBookList()] called");
        bookList.add(new Book("1984", 10));
        bookList.add(new Book("Bíblia", 20));
        bookList.add(new Book("A Mensagem", 2));
    }
    
    public List<Book> getBookList() {        
        System.out.println("[getBookList()] called");
        return bookList;
    }

    /**
     * Places a new order on the system
     * @param title book title
     * @param quantity  wanted quantity
     * @param name  client's name
     * @param address   client's address
     * @param email client's email
     * @return true on successful order
     */
    public Boolean placeOrder(String title, int quantity, String name, String address, String email) {
        
        //Debug
        System.out.format("New order: %s, %d, %s, %s, %s.", title, quantity, name, address, email);
        
        sendWarehouse("Faz de conta que isto é um livro!");

        if (orders.add(new Order(title, quantity, name, address, email)))
            return true;
        else
            return false;
    }
    
    
    /**
     * Sends a message to the queue of the warehouse
     */
    public void sendWarehouse(String messageToSend){
       
        try{
            Context ctx = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory)ctx.lookup("jms/ConnectionFactory");
            Queue queue = (Queue)ctx.lookup("jms/myQueue");
            javax.jms.Connection  connection = connectionFactory.createConnection();
            javax.jms.Session        session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
            MessageProducer messageProducer = session.createProducer(queue);
            TextMessage message = session.createTextMessage();
            message.setText(messageToSend);
            System.out.println( "It come from Servlet:"+ message.getText());
            messageProducer.send(message);

            //message sent , it was all
            
            //show what we have done in this servlet
            System.out.print("Servlet Send this message "+ messageToSend + "  to this Queue : " + queue.getQueueName());
            
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

}
