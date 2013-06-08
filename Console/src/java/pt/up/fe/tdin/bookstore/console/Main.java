/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.up.fe.tdin.bookstore.console;

import javax.xml.ws.WebServiceRef;

/**
 *
 * @author ctrler
 */
public class Main {
    @WebServiceRef(wsdlLocation = "META-INF/wsdl/localhost_8080/myWebService/myWebService.wsdl")
    private static MyWebService_Service service;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("######## Printer started #######");
        while(true) {
            //Pause for 5 seconds
            Thread.sleep(5000);
            //Print a message
            String receipt = getReceipt();
            if(receipt!=null)
                System.out.println(receipt);
        }
    }

    private static String getReceipt() {
        pt.up.fe.tdin.bookstore.console.MyWebService port = service.getMyWebServicePort();
        return port.getReceipt();
    }
}
