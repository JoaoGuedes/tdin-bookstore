/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.up.fe.tdin.bookstore.store;


/**
 *
 * @author joaoguedes
 */
public class Book {
    private static int BOOKID=0;
    
    private int id;
    private String title;
    private int available;
    
    /**
     * Creates a new book
     * @param title book title
     * @param available available quantity 
     */
    public Book(String title, int available) {
        this.id = BOOKID++;
        this.title = title;
        this.available = available;
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
    
}
