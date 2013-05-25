<%-- 
    Document   : index
    Created on : May 21, 2013, 5:30:33 PM
    Author     : joaoguedes
--%>


<%@page import="pt.up.fe.tdin.bookstore.store.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8" import="java.util.List"%>
<!DOCTYPE html>
<html>
    <head>
         <!-- Framework CSS -->
    <link rel="stylesheet" href="css/blueprint/screen.css" type="text/css" media="screen, projection">
    <link rel="stylesheet" href="css/blueprint/print.css" type="text/css" media="print">
    <!--[if lt IE 8]><link rel="stylesheet" href="../../blueprint/ie.css" type="text/css" media="screen, projection"><![endif]-->
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Bookstore</title>   
    </head>
    <body>
        <div class="container">
            <h1>Bookstore</h1>
                <hr/>
                <div class="span-12">
                    
                    <form method="POST" action="neworder.jsp">
                    <fieldset>
                        <legend>Place a new order</legend>
                        <p>
                            <label for="title">Book title:</label>
                            <br>
                                <select id="title" name="title">                                
                                    <%-- start web service invocation --%>
                                    <%
                                    try {
                                        MyWebService_Service service = new MyWebService_Service();
                                        MyWebService port = service.getMyWebServicePort();
                                        // TODO process result here
                                        List<Book> result = port.getBookList();

                                        for (Book book : result) {
                                            %>
                                               <option value="<%= book.getId() %>"><%=  book.getTitle() %></option>
                                            <%
                                        }

                                    } catch (Exception ex) {
                                        // TODO handle custom exceptions here
                                    }
                                    %>
                                    <%-- end web service invocation --%>
                                </select>
                        </p>
                         <p>
                            <label for="quantity">Quantity:</label>
                            <br>
                            <input type="text" class="text" name="quantity" id="quantity">
                        </p>
                        <p>
                            <label for="name">Your name:</label>
                            <br>
                            <input type="text" class="text" name="name" id="name">
                        </p>
                        <p>
                            <label for="address">Your address:</label>
                            <br>
                            <input type="text" class="text" name="address" id="address">
                        </p>
                        <p>
                            <label for="address">Your email:</label>
                            <br>
                            <input type="text" class="text" name="email" id="email">
                        </p>
                        <input type="submit" value="Order">
                    </fieldset>
                    </form>
                </div>
        </div>
        
    </body>
</html>
