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
        <link rel="stylesheet" href="<%=request.getContextPath()%>/pages/stylesheet.css" type="text/css" />
    </head>
    
    <body class="verticalscroll" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0" onload="load()">
        <f:view locale="#{StockTakingBean.locale}">
            <f:loadBundle var="bundle" basename ="de.linogistix.mobile.processes.stocktaking.StockTakingBundle" /> 
            
            <h:form id="Form" styleClass="form" >
                <%-- Page Title--%>
                <p id="pHeader"class="pageheader">
	                <h:outputText id="pagetitle" value="#{bundle.TitleEnterClient}" styleClass="pagetitle"/>
	                <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
                </p>
                
                <div class="space">
                    <%--                <h:outputFormat id="infoMsg"  value="" styleClass="info"/> --%>
                    <h:messages id="messages"  styleClass="error"/> 
                    
                    <table  width="100%" border="0" cellspacing="0">
                    	<tr >
                    		<td>
                    			&nbsp;
                    		</td>
                    	</tr>
                        <tr>
                            <td>
                            	<h:outputLabel id="ClientLabel" value="#{bundle.LabelEnterClient}" styleClass="label" /> 
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <h:inputText id="INPUT1" 
                                	value="#{StockTakingBean.inputClientNumber}" 
                                    styleClass="input"  />
                            </td>
                        </tr>
                        
                    </table>
                </div>
                    
				<h:inputText value="IE-Dummy" style="display:none" />

                
                <%-- Command Buttons --%>
                <div class="buttonbar">  
		                    <h:commandButton id="BUTTON_COUNT" 
		                    				 value="#{bundle.ButtonContinue}" 
		                    				 action="#{StockTakingBean.processEnterClient}" 
		                    				 styleClass="commandButton"  />
		                    <h:commandButton id="BUTTON_MENU" 
		                    				 value="#{bundle.ButtonBackToMenu}" 
		                    				 action="#{StockTakingBean.backToMenu}" 
		                    				 styleClass="commandButton"  />
                </div>
            </h:form>
        </f:view> 
        <script type="text/javascript">
            
            function load() {            
                setFocus();    
            }    
            
            function setFocus() {
                document.getElementById('Form:INPUT1').focus();
                document.getElementById('Form:INPUT1').select();
            }    
            
        </script>
        
    </body>
</html>

