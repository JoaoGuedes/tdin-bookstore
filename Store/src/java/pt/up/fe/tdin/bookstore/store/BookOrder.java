/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.up.fe.tdin.bookstore.store;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Joao
 */
@Entity
public class BookOrder implements Serializable {

    public static enum State { WAITING, DISPATCHING, DISPATCHED; }
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int quantity;
    private int bookId;
    private String clientName;
    private String clientAddress;
    private String clientEmail;
    private State orderState;
    
    @Temporal(TemporalType.DATE)
    private Date delivery;
    
    public BookOrder() {};
    
     public BookOrder(int bookId, int quantity, String name, String address, String email) {
        this.bookId = bookId;
        this.quantity = quantity;
        clientName = name;
        clientAddress = address;
        clientEmail = email;
    }
    
    public long getId() { return id; }
    public int getBookId() { return bookId; }
    public int getQuantity() { return quantity; }
    public String getClientName() { return clientName; }
    public String getClientAddress() { return clientAddress; }
    public String getClientEmail() { return clientEmail; }
    public String getOrderState() { return orderState.toString(); }
    public Date getOrderDeliveryDate() { return delivery; }
    public State getState() { return this.orderState; }
    
    public void setId(long id) { this.id = id; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setClientName(String name) { clientName = name; }
    public void setClientAddress(String address) { clientAddress = address; }
    public void setClientEmail(String email) { clientEmail = email; }
    public void setOrderState(String state) { this.orderState = State.valueOf(state); }
    public void setOrderDeliveryDate(Date date) { this.delivery = date; }
    public void setState(State state){ this.orderState = state;}

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BookOrder)) {
            return false;
        }
        BookOrder other = (BookOrder) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pt.up.fe.tdin.bookstore.store.EntityOrder[ id=" + id + " ]";
    }
    
}
