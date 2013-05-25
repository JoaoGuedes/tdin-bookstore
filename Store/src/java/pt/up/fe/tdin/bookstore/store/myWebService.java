/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.up.fe.tdin.bookstore.store;

import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.ejb.Stateless;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author ctrler
 */
@WebService(serviceName = "myWebService")
@Stateless()
public class myWebService {
    @EJB
    private Operations ejbRef;// Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Web Service Operation")

    @WebMethod(operationName = "populateBookList")
    @Oneway
    public void populateBookList() {
        ejbRef.populateBookList();
    }

    @WebMethod(operationName = "getBookList")
    public List<Book> getBookList() {
        return ejbRef.getBookList();
    }

    @WebMethod(operationName = "placeOrder")
    public Boolean placeOrder(@WebParam(name = "title") String title, @WebParam(name = "quantity") int quantity, @WebParam(name = "name") String name, @WebParam(name = "address") String address, @WebParam(name = "email") String email) {
        return ejbRef.placeOrder(title, quantity, name, address, email);
    }
    
}
