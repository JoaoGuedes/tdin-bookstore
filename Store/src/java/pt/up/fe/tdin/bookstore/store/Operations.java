/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.up.fe.tdin.bookstore.store;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import pt.up.fe.tdin.bookstore.common.Book;
import pt.up.fe.tdin.bookstore.common.WarehouseOrder;
/**
 *
 * @author joaoguedes
 */
@Singleton
@Startup
@LocalBean
public class Operations {
    @Resource(name = "mail/myMail")
    private javax.mail.Session mailmyMail;

 /**
     * List of placed orders
     */
    
    @PostConstruct
    private void postConstruct() {
       System.out.println("EJB Created");
       populateBookList();
    }
    
    /***
     * List of placed orders
     */
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
        bookList.add(new Book("1984", 10, 5.5));
        bookList.add(new Book("Bíblia", 20, 5));
        bookList.add(new Book("A Mensagem", 2, 20));
    }
    
    public List<Book> getBookList() {        
        System.out.println("[getBookList()] called");
        return bookList;
    }
    
    /***
     * Returns a book from book array
     * @param id    book id
     * @return
     */
    public Book getBook(int id) {
        for (Book b : bookList) {
            if (b.getId() == id)
                return b;
        }        
        return null;
    }
    
    public void setBookAvailability(int id, int availability) {
        System.out.println("[setBookAvailability()] called");
        getBook(id).setAvailability(availability);
    }
    
    /***
     * Returns stock of a given book
     * @param id    book id
     * @return 
     */
    public int getBookStockLeft(int id) {        
        return getBook(id).getAvailability();        
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
    public Boolean placeOrder(int bookId, int quantity, String name, String address, String email) {
        
        //Debug
        System.out.format("New order: "
                + "\n Book id: %d"
                + "\n Quantity: %d"
                + "\n Client's name: %s"
                + "\n Client's address: %s"
                + "\n Client's email: %s.", bookId, quantity, name, address, email);
        
        Book myBook = getBook(bookId);
        int stockLeft = myBook.getAvailability() - quantity;
        
        if (stockLeft < 0) { // Sends request to warehouse if not enough stock
            
            int stockToOrder = quantity*10;
            WarehouseOrder warehouseOrder = new WarehouseOrder(myBook,stockToOrder);
     
            sendWarehouse(warehouseOrder);
            
            // TODO: set order status accordingly and wait(?) for shipment
            return true;
        }       
        
        //There is enough stock to fulfill order
        //Set current stock
        myBook.setAvailability(stockLeft);
        
        //Create new order and set delivery date for tomorrow
        Order newOrder = new Order(bookId, quantity, name, address, email);
        newOrder.setOrderState("DISPATCHED");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, 1);
        newOrder.setOrderDeliveryDate(cal.getTime());
        
        //Compose message and send email to client
        try {
            String message = "Hi " + name + ",\n"
                    + "Your order of \"" + myBook.getTitle() + "\" is scheduled to be delivered by " 
                    + new SimpleDateFormat("EEE, dd MMM").format(newOrder.getOrderDeliveryDate()) + ".\n"
                    + "The total cost for your order is " + quantity*myBook.getPrice();
            sendMail(email, "Bookstore order", message);
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }

        //TODO: print receipt
        
        //Add order to array
        if (orders.add(new Order(bookId, quantity, name, address, email)))
            return true;
        else
            return false;
    }
    
    public void changeOrderState(Order order, String state) {       
        order.setOrderState(state);
    }
 
    public void setOrderDeliveryDate(Order order, Date date) {       
        order.setOrderDeliveryDate(date);
    }
    
    /**
     * Sends a message to the queue of the warehouse
     */
    private void sendWarehouse(WarehouseOrder warehouseOrder){
       
        try{
            Context ctx = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory)ctx.lookup("jms/ConnectionFactory");
            Queue queue = (Queue)ctx.lookup("jms/myQueue");
            javax.jms.Connection  connection = connectionFactory.createConnection();
            javax.jms.Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
            MessageProducer messageProducer = session.createProducer(queue);
            ObjectMessage message = session.createObjectMessage(warehouseOrder);
            
            messageProducer.send(message);

            //message sent , it was all
            
            //show what we have done in this servlet
            System.out.print("Servlet Sent: OrderID: "+ warehouseOrder.getOrderId()
                    + " | Book: " + warehouseOrder.getBook().getTitle()
                    + " | Quantity: " + warehouseOrder.getQuantity()
                    + " to this Queue: " + queue.getQueueName());
            
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void sendMail(String email, String subject, String body) throws NamingException, MessagingException {
        MimeMessage message = new MimeMessage(mailmyMail);
        message.setSubject(subject);
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
        message.setText(body);
        Transport.send(message);
    }

}
