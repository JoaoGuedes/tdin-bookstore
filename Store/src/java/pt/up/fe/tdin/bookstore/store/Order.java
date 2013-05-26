/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.up.fe.tdin.bookstore.store;

import java.util.Date;

/**
 *
 * @author joaoguedes
 */ 
enum State { WAITING, DISPATCHING, DISPATCHED; }
class Placement {
    State state;
    Date delivery;
};

public class Order {    
    
    
    public static int totalOrders=0;
    
    private int NAME = 0;
    private int ADDRESS = 1;
    private int EMAIL = 2;
    
    private int id;
    private int bookId;
    private int quantity;
    private String[] client;
    private Placement placement;
    
    public Order(int bookId, int quantity, String name, String address, String email) {
        this.id = totalOrders++;
        this.bookId = bookId;
        client = new String[3];
        this.quantity = quantity;
        client[NAME] = name;
        client[ADDRESS] = address;
        client[EMAIL] = email;
        placement = new Placement();
    }
    
    public int getId() { return id; }
    public int getBookId() { return bookId; }
    public int getQuantity() { return quantity; }
    public String getClientName() { return client[NAME]; }
    public String getClientAddress() { return client[ADDRESS]; }
    public String getClientEmail() { return client[EMAIL]; }
    public String getOrderState() { return placement.state.toString(); }
    public Date getOrderDeliveryDate() { return placement.delivery; }
    
    public void setId(int id) { this.id = id; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setClientName(String name) { client[NAME] = name; }
    public void setClientAddress(String address) { client[ADDRESS] = address; }
    public void setClientEmail(String email) { client[EMAIL] = email; }
    public void setOrderState(String state) { this.placement.state = State.valueOf(state); }
    public void setOrderDeliveryDate(Date date) { this.placement.delivery = date; }
    
}
