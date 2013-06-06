<%-- 
    Document   : neworder
    Created on : May 21, 2013, 8:19:24 PM
    Author     : joaoguedes
--%>

<%@page import="pt.up.fe.tdin.bookstore.store.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8" import="java.util.List"%>
<!DOCTYPE html>
<html>
    <head>
         <link rel="stylesheet" href="css/blueprint/screen.css" type="text/css" media="screen, projection">
    <link rel="stylesheet" href="css/blueprint/print.css" type="text/css" media="print">
    <!--[if lt IE 8]><link rel="stylesheet" href="../../blueprint/ie.css" type="text/css" media="screen, projection"><![endif]-->
        
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>New order</title>
    </head>
    <body>
        <div class="container"><br/>
            <div class="prepend-8 append-8">
    
    <%-- start web service invocation --%>
    <%
    try {
	MyWebService_Service service = new MyWebService_Service();
	MyWebService port = service.getMyWebServicePort();
	 // TODO initialize WS operation arguments here
	int bookId = Integer.parseInt(request.getParameter("bookId"));
	int quantity = Integer.parseInt(request.getParameter("quantity"));
	java.lang.String name = request.getParameter("name");
	java.lang.String address = request.getParameter("address");
	java.lang.String email = request.getParameter("email");
        
	// TODO process result here
	java.lang.Boolean result = port.placeOrder(bookId, quantity, name, address, email, false);
	
        if (result) {
            %>
            <div class="success span-8">
                Order successfully placed. <br/>We will contact you by mail shortly.
            </div>
            <%            
        } else {
            %>
            <div class="error span-8 text-center">
                There was a problem with your order.
            </div>            
            <%
        }
        
    } catch (Exception ex) {
            %>
            <div class="error span-8">
                There was a problem with your order.
            </div>            
            <%
    }
    %>
            </div>
        </div>
    </body>
</html>
