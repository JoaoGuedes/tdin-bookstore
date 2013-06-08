/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.up.fe.tdin.bookstore.common;

import java.io.Serializable;


/**
 *
 * @author joaoguedes
 */
public class Book implements Serializable {
    
    private static final long serialVersionUID = 1112132213L;
    
    private int id;
    private String title;
    private int available;
    private double price;
    
    /**
     * Creates a new book
     * @param title book title
     * @param available available quantity 
     */
    public Book(String title, int available, double price, int id) {
        this.id = id;
        this.title = title;
        this.available = available;
        this.price = price;
    }
    
    public Book(int id, String title){
        this.id = id;
        this.title = title;
    }
    
    public double getPrice() {
        return price;
    }
    
    public String getTitle() {
        return title;
    }
    
    public int getAvailability() {
        return available;
    } 
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
 
    public void setAvailability(int available) {
        this.available = available;
    }  
    
    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + this.id;
        hash = 29 * hash + (this.title != null ? this.title.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Book other = (Book) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
    
    
}
