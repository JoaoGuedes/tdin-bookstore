/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.up.fe.tdin.bookstore.warehouse;

import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.ejb.Stateless;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import pt.up.fe.tdin.bookstore.common.WarehouseOrder;

/**
 *
 * @author ctrler
 */
@WebService(serviceName = "warehouseWebservice")
@Stateless()
public class warehouseWebservice {
    @EJB
    private WarehouseOperations ejbRef;// Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Web Service Operation")

    @WebMethod(operationName = "getOrderList")
    public List<WarehouseOrder> getOrderList() {
        return ejbRef.getOrderList();
    }

    @WebMethod(operationName = "addOrder")
    @Oneway
    public void addOrder(@WebParam(name = "wo") WarehouseOrder wo) {
        ejbRef.addOrder(wo);
    }

    @WebMethod(operationName = "completedOrder")
    @Oneway
    public void completedOrder(@WebParam(name = "orderId") long orderId) {
        ejbRef.completedOrder(orderId);
    }
    
}
