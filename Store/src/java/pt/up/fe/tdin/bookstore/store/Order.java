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
enum State { WAITING, DISPATCHING, DISPATCHED }
    
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
    private String title;
    private int quantity;
    private String[] client;
    private Placement placement;
    
    public Order(String title, int quantity, String name, String address, String email) {
        this.id = totalOrders++;
        client = new String[3];
        this.title = title;
        this.quantity = quantity;
        client[NAME] = name;
        client[ADDRESS] = address;
        client[EMAIL] = email;
    }
    
    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getQuantity() { return quantity; }
    public String getClientName() { return client[NAME]; }
    public String getClientAddress() { return client[ADDRESS]; }
    public String getClientEmail() { return client[EMAIL]; }
    public State getOrderState() { return placement.state; }
    public Date getOrderDeliveryDate() { return placement.delivery; }
    
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setClientName(String name) { client[NAME] = name; }
    public void setClientAddress(String address) { client[ADDRESS] = address; }
    public void setClientEmail(String email) { client[EMAIL] = email; }
    public void setOrderState(State state) { this.placement.state = state; }
    public void setOrderDeliveryDate(Date date) { this.placement.delivery = date; }
    
}
