/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.up.fe.tdin.bookstore.store;

import com.sun.xml.ws.api.tx.at.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.UserTransaction;
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
    
    @PostConstruct
    private void postConstruct() {
        orders = new ArrayList();
        bookList = new ArrayList();
        receipts = new LinkedList<String>();
        
        
        TypedQuery<BookOrder> query = em.createQuery("SELECT b FROM BookOrder b", BookOrder.class);
        orders = query.getResultList();

       System.out.println("EJB Created");
       
       populateBookList();
    }
    
    /***
     * List of placed orders and available books
     */
    private List<BookOrder> orders;
    private List<Book> bookList;
    
    /** Queue where the receipts are stored
     */
    java.util.Queue<String> receipts;
    
    @PersistenceContext(unitName = "StorePU")
    private EntityManager em;
    
    /**
     * Populates book list. 
     * Should be fetched from a file in the future (?).
     */
    private void populateBookList() {
        System.out.println("[populateBookList()] called");
        bookList.add(new Book("1984", 10, 5.5, 1));
        bookList.add(new Book("Bíblia", 20, 5, 2));
        bookList.add(new Book("A Mensagem", 2, 20, 3));
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
    
    /*
     * Returns order by id from order list
     */
    private BookOrder getOrderById(long id) {
        for (BookOrder bo: orders) {
            if (bo.getId() == id)
                return bo;
        }
        return null;
    }
    
    public List<Book> getBookList() {        
        System.out.println("[getBookList()] called");
        return bookList;
    }
    
    /*
     * Returns all orders for a certain book.
     */
    private List<BookOrder> getOrdersByBook(int bookId) {
        List<BookOrder> ordersBook = new ArrayList<BookOrder>();
        System.out.print(orders);
        for (BookOrder bo: orders) {
            if (bo.getBookId() == bookId)
                ordersBook.add(bo);
        }
        return ordersBook;
    }
    
    /*
     * Returns all pending orders (orders whose state equals dispatching)
     * for a certain book
     */
    public List<BookOrder> getPendingOrders(int bookId) {
        List<BookOrder> ordersBook = getOrdersByBook(bookId);
        List<BookOrder> pendingOrders = new ArrayList<BookOrder>();
        for (BookOrder bo: ordersBook) {
               if (bo.getOrderState().equals(BookOrder.State.DISPATCHING.toString()))
                   pendingOrders.add(bo);
        }
        return pendingOrders;
    }
    /***
     * Returns stock of a given book
     * @param id    book id
     * @return 
     */
    public int getBookStockLeft(int id) {        
        return getBook(id).getAvailability();        
    }
    
    /*
     * Sets stock of a given book
     */
    public void setBookAvailability(int id, int availability) {
        System.out.println("[setBookAvailability()] called");
        getBook(id).setAvailability(availability);      
    }

    /*
     * Satisfies all pending orders for a given book
     * Schedules a new delivery and sends mail to costumer.
     */
    public void satisfyPendingOrders(int bookId) {
        List<BookOrder> pendingOrders = getPendingOrders(bookId);
        
        for (BookOrder po: pendingOrders) {
            int currentStockMinusThisOrder = getBookStockLeft(bookId) - po.getQuantity();
            if (currentStockMinusThisOrder >= 0) {
                getBook(bookId).setAvailability(currentStockMinusThisOrder);
                po.setOrderState(BookOrder.State.DISPATCHED.toString());
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                po.setOrderDeliveryDate(cal.getTime());
                Book myBook = getBook(po.getBookId());
                String message = "Hi " + po.getClientName() + ",\n"
                    + "Your order of \"" + myBook.getTitle() + "\" is scheduled to be delivered by " 
                    + new SimpleDateFormat("EEE, dd MMM").format(po.getOrderDeliveryDate()) + ".\n"
                    + "The total cost for your order is " + po.getQuantity()*myBook.getPrice();
                
                try {
                    sendMail(po.getClientEmail(), "Bookstore order", message);
                } catch (NamingException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MessagingException ex) {
                    Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                }
            } 
            else
                return;
        }
        
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
    public Boolean placeOrder(int bookId, int quantity, String name, String address, String email, boolean boughtOnStore) {
        
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
            // Create a new order and set its state as awaiting expedition.
            BookOrder newOrder = new BookOrder(bookId, quantity, name, address, email);
            
            newOrder.setOrderState(BookOrder.State.WAITING.toString());
            if (!boughtOnStore) {
                try {
                    String message = "Hi " + name + ",\n"
                            + "Your order of \"" + myBook.getTitle() + "\" is on back order."
                            + "\nYou will inform you as soon as we can fulfill your order." 
                            + "The total cost for your order is " + quantity*myBook.getPrice();
                    sendMail(email, "Bookstore order", message);
                } catch (Exception e) {
                    System.out.print(e.getMessage());
            }
        }
            persist(newOrder);
            return orders.add(newOrder);
         }       
        
        //There is enough stock to fulfill order
        //Set current stock
        myBook.setAvailability(stockLeft);
        
        //Create new order and set delivery date for tomorrow
        BookOrder newOrder = new BookOrder(bookId, quantity, name, address, email);
        newOrder.setOrderState(BookOrder.State.DISPATCHED.toString());
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, 1);
        newOrder.setOrderDeliveryDate(cal.getTime());
        
        //Compose message and send email to client, if the order was placed online
        if (!boughtOnStore) {
            try {
                String message = "Hi " + name + ",\n"
                        + "Your order of \"" + myBook.getTitle() + "\" is scheduled to be delivered by " 
                        + new SimpleDateFormat("EEE, dd MMM").format(newOrder.getOrderDeliveryDate()) + ".\n"
                        + "The total cost for your order is " + quantity*myBook.getPrice();
                sendMail(email, "Bookstore order", message);
            } catch (Exception e) {
                System.out.print(e.getMessage());
            }
        }
        else{ // It was bought on store. Lets print a receipt
            newOrder.setOrderDeliveryDate(new Date()); // Delivery is now
            
            String receipt = "Receipt for purchase" + 
                             "\nBook: " + myBook.getTitle() + 
                             "\nUnit Price: " + myBook.getPrice() + " €" +
                             "\nQuantity: " + quantity +
                             "\nTotal: " + quantity*myBook.getPrice() +
                             "\n.................." + 
                             "\nCustomer: " + name +
                             "\nDate: " + new SimpleDateFormat("EEE, dd MMM").format(newOrder.getOrderDeliveryDate()) +
                             "\n#################\n";
            receipts.add(receipt);
        }
        
        
        //Add order to array and store it in the DB
        persist(newOrder);
        return orders.add(newOrder);
    }
    
    /***
     * Changes the state of an order, locally and on the database
     * @param id    the id of the order
     * @param state changed state
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void changeOrderState(long id, String state) {  
        BookOrder order = getOrderById(id);
        order.setOrderState(state);
        BookOrder dbOrder = em.find(BookOrder.class, order.getId());
        //em.getTransaction().begin();
        dbOrder.setOrderState(state);
        //em.getTransaction().commit();
        //em.close();

    }
    
    /***
     * Changes the delivery date of an order, locally and on the database
     * @param id    the id of the order
     * @param date  date to be changed
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void setOrderDeliveryDate(long id, Date date) {    
        BookOrder order = getOrderById(id);
        order.setOrderDeliveryDate(date);
        BookOrder dbOrder = em.find(BookOrder.class, order.getId());
        
        //em.getTransaction().begin();
        dbOrder.setOrderDeliveryDate(date);
        //em.getTransaction().commit();
        
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
            System.out.print("Servlet Sent: Book: " + warehouseOrder.getBook().getTitle()
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

    /*
     * Persists object in the EntityManager
     */
    private void persist(Object object) {
        em.persist(object);
    }
    
    /**
     * Signals the Store that an WarehouseOrder was shipped to the store.
     * @param bookId the book that was shipped
     * @param qty number of books shipped to the store.
     */
    public void warehouseOrderShipped(int bookId, int qty){
          
        // Gets all the orders for this book that are marked as Waiting
        List<BookOrder> waitingOrders = getOrdersForBook(bookId, BookOrder.State.WAITING);
        
        int localQty = qty;
        for(BookOrder bo : waitingOrders){
            if(bo.getQuantity() <= localQty){
                changeOrderState(bo.getId(), BookOrder.State.DISPATCHING.toString());
                
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.DAY_OF_YEAR, 2);
                setOrderDeliveryDate(bo.getId(),cal.getTime());
                
                localQty -= bo.getQuantity();
                
                System.err.println("[Store.Operations.warehouseOrderShipped] updated order " + bo.getId() +  " qty: " + bo.getQuantity());
            }
            
        }
 
    }
    
    /**
     * Gets the orders for a book that have a given state
     * @param bookId
     * @param state
     * @return 
     */
    private List<BookOrder> getOrdersForBook(int bookId, BookOrder.State state){
        
        List<BookOrder> result = new ArrayList<BookOrder>();
        
        for(BookOrder bo : orders){
            if(bo.getBookId()==bookId && bo.getState() == state){
                result.add(bo);
            }
        }
        
        return result;
    }
    
    /** Returns a receipts (the first in the queue) *
     * To be used by the console app.
     * @return 
     */
    public String getReceipt(){
        
        return receipts.poll();
    }

}
