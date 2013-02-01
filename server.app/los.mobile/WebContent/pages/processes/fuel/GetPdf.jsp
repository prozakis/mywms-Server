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
        <link rel="stylesheet" href="<%=request.getContextPath()%>/pages/stylesheet.css" type="text/css" />
    </head>
    
    <body class="verticalscroll" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0" onload="load()">
        <f:view locale="#{FuelBean.locale}">	
            <f:loadBundle var="bundle" basename ="de.linogistix.mobile.processes.fuel.FuelBundle" /> 

            <h:form id="Form" styleClass="form" >

                <p id="pHeader" class="pageheader">
                	<h:outputText id="pagetitle" value="#{bundle.TitleEnterItem}" styleClass="pagetitle" />
	                <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
                </p>
                
                <div class="space">
                    <h:messages id="messages" styleClass="error"/>
                     
                    <table width="100%" border="0" cellspacing="0">
                        
                        <tr><td>&#160;</td></tr>

                        <tr>
                            <td>
                            	<h:outputLabel id="input1Label" value="#{bundle.LabelEnterItem}" styleClass="label" />
                            </td>
                        </tr><tr>
                            <td>
                               	<h:inputText id="input1" 
                             			 value="#{FuelBean.inputCode}" 
                             			 styleClass="input" /> 
                            </td>
                        </tr>
                    </table>
                    
                </div>
                                        
                <%-- Command Buttons --%>
                <div class="buttonbar">  
	                 <h:commandButton id="backButton" 
	                 				 value="#{bundle.ButtonMenu}" 
	                 				 action="#{FuelBean.processEnterItemCancel}" 
	                 				 styleClass="commandButton"  />
                </div>
            </h:form>
        </f:view> 
    </body>
</html>
