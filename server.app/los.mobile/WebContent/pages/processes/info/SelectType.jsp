<%-- 
  Copyright (c) 2010 LinogistiX GmbH

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
        <f:view locale="#{InfoBean.locale}">
            <f:loadBundle var="bundle" basename ="de.linogistix.mobile.processes.info.InfoBundle" /> 
            
            <h:form id="Form" styleClass="form" >
                <%-- Page Title--%>
                <p id="pHeader"class="pageheader">
                	<h:outputText id="pagetitle" value="#{bundle.TitleSelectType}" styleClass="pagetitle"/>
	                <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
               	</p>
                
                <div class="space">
                    <h:messages id="messages"  styleClass="error"/> 
                    
                    <table  width="100%" border="0" cellspacing="0">
                    	<colgroup>
							<col width="20%"/>
							<col width="80%"/>
						</colgroup>

	            		<tr><td>&#160;</td></tr>

                        <tr><td>
		                    <h:commandButton id="btUnitLoad" 
			                				 value="#{bundle.LabelUnitLoad}" 
			                				 action="#{InfoBean.processSelectUnitLoad}" 
			                				 styleClass="commandButton" 
			                				 rendered="#{InfoBean.unitLoadSelected}"  />
						</td></tr>
                        <tr><td>
		                    <h:commandButton id="btItemData" 
			                				 value="#{bundle.LabelItem" 
			                				 action="#{InfoBean.processSelectItemData}" 
			                				 styleClass="commandButton" 
			                				 rendered="#{InfoBean.itemDataSelected}"  />
						</td></tr>
                        <tr><td>
		                    <h:commandButton id="btLocation" 
			                				 value="#{bundle.LabelLocation}" 
			                				 action="#{InfoBean.processSelectLocation}" 
			                				 styleClass="commandButton" 
			                				 rendered="#{InfoBean.locationSelected}"  />
						</td></tr>
                    </table>
                </div>
                

                <%-- Command Buttons --%>
                <div class="buttonbar">  
	                    <h:commandButton id="BUTTON_CANCEL" 
		                				 value="#{bundle.ButtonBack}" 
		                				 action="#{InfoBean.processSelectCancel}" 
		                				 styleClass="commandButton"  />
                </div>
            </h:form>
        </f:view> 
        <script type="text/javascript">
            
            function load() {            
                setFocus();    
            }    
            
            function setFocus() {
                document.getElementById('Form:btUnitLoad').focus();
            }    
            
        </script>
        
    </body>
</html>
