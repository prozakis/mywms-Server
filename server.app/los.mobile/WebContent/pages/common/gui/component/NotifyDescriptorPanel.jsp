<%-- 
  Copyright (c) 2006 - 2010 LinogistiX GmbH

  www.linogistix.com
  
  Project: myWMS-LOS
--%>

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>MELISA</title>
        <%--    <link rel="stylesheet" type="text/css" href="pages/picking/stylesheet.css" />--%>
        <%--  <link rel="stylesheet" type="text/css" href="stylesheet.css" /> --%>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/pages/stylesheet.css" type="text/css" />
    </head>
    
    <f:loadBundle var="bundle" basename ="de.linogistix.mobile.res.Bundle" />
    
    <body topmargin="0" leftmargin="0" marginwidth="0" marginheight="0" onload="load()">
        <f:view>
            <f:loadBundle var="bundle" basename ="de.linogistix.mobile.res.Bundle" /> 
            
            <h:form id="successForm" styleClass="form">
                <%-- Page Title--%>
                <p id="pHeader"class="pageheader">
	                <h:outputText id="pagetitle" value="#{NotifyDescriptorBean.pageTitle}" styleClass="pagetitle"/>
	                <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
                </p>
           <%--<p id="pForm">--%>
                <%-- Form --%>
                <%-- User (Driver) --%>   
                <div style=  padding-left:5px;>
                    <%--                <h:outputFormat id="infoMsg"  value="" styleClass="info"/> --%>
                    
                    <table width="300" border="0">
                        <tr>
                            <td width="15%"><h:graphicImage id="infoImg" url="#{NotifyDescriptorBean.icon}" /></td>
                            <td width="85%"><h:outputText id="message" value="#{NotifyDescriptorBean.message}"  styleClass="label"/></td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                   <div align="center" style="padding-top:5px" >
                                       <h:commandButton id="cancelButton" value="#{NotifyDescriptorBean.buttonText}" action="#{NotifyDescriptorBean.buttonActionPerformedListener}"  styleClass="commandButton"  />
                                   </div>
                            </td>
                        </tr>
                    </table>
                    
                    <%-- Command Buttons --%>
                    
                </div>                
            </h:form>
        </f:view>
        
        <script type="text/javascript">
            
            function load() {            
                setFocus();    
            }    
            
            function setFocus() {
                document.getElementById('successForm:cancelButton').focus();
            }    
            
        </script>
         
    </body>
</html>
