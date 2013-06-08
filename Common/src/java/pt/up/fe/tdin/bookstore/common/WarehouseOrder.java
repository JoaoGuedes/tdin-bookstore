/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.up.fe.tdin.bookstore.common;

import java.io.Serializable;

/**
 *
 * @author ctrler
 */
public class WarehouseOrder implements Serializable{
    
    private static int ORDERID=0;
    private static final long serialVersionUID = 143242L;
    
    long orderId;
    Book book;
    int quantity;

    public WarehouseOrder(Book book, int quantity) {
        this.orderId = -1;
        this.book = book;
        this.quantity = quantity;
    }
    
    public WarehouseOrder(long id, Book book, int quantity) {
        this.orderId = id;
        this.book = book;
        this.quantity = quantity;
    }

    public WarehouseOrder(){
        
    }
    
    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
   
}