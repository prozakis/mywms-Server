<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>MELISA</title>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/pages/stylesheet.css" type="text/css" />
    </head>
    
    <body class="verticalscroll" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0" onload="load()">
        <f:view locale="#{FuelOrderLogBean.locale}">	
            <f:loadBundle var="bundle" basename ="de.linogistix.mobile.processes.fuel.FuelBundle" /> 
            
            <h:form id="Form" styleClass="form" >

                <p id="pHeader" class="pageheader">
                	<h:outputText id="pagetitle" value="#{bundle.TitleEnterDeliverer}" styleClass="pagetitle"/>
	                <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
               	</p>
                
                <div class="space">
                    <h:messages id="messages"  styleClass="error"/> 
                    
                    <table  width="100%" border="0" cellspacing="0">

	            		<tr><td>&#160;</td></tr>

                        <tr>
                            <td>
                            <h:outputLabel id="input1Label" value="#{bundle.LabelEnterStartDate}" styleClass="label" />
                            </td>
							</tr>
							<tr>
                            <td>
							<a4j:outputPanel id="calendar1" layout="block">
        				            <rich:calendar value="#{FuelOrderLogBean.startDateInput}"
        				                locale="en/US"
        				                popup="true"
        				                datePattern="d/MMM/y"
        				                showApplyButton="false" cellWidth="20px" cellHeight="20px" />
        				    </a4j:outputPanel>
								<%--
                               	<h:inputText id="input1" 
                             			 value="#{FuelOrderLogBean.driver}" 
                             			 styleClass="input" /> 
								--%>
                            </td>
                        </tr>

                        <tr>
                            <td>
                            	<h:outputLabel id="input1Label2" value="#{bundle.LabelEnterEndDate}" styleClass="label" />
                            </td>
                        </tr>
						<tr>
                            <td>
							<a4j:outputPanel id="calendar2" layout="block">
        				            <rich:calendar value="#{FuelOrderLogBean.endDateInput}"
        				                locale="en/US"
        				                popup="true"
        				                datePattern="d/MMM/y"
        				                showApplyButton="false" cellWidth="20px" cellHeight="20px" />
        				    </a4j:outputPanel>
								<%--
                               	<h:inputText id="input2" 
                             			 value="#{FuelOrderLogBean.plateNumber}" 
                             			 styleClass="input" /> 
								--%>
                            </td>
                        </tr>
                    </table>
                    
					<h:inputText value="IE-Dummy" style="display:none" />
                  	
                </div>
                
                <div class="buttonbar">  
	                 <h:commandButton id="forwardButton" 
	                 				 value="#{bundle.ButtonForward}" 
	                 				 action="#{FuelOrderLogBean.processEnterDates}" 
	                 				 styleClass="commandButton"  />
	                 				 
	                 <h:commandButton id="backButton" 
	                 				 value="#{bundle.ButtonCancel}" 
	                 				 action="#{FuelOrderLogBean.processEnterDatesCancel}" 
	                 				 styleClass="commandButton"  />

                </div>
                
            </h:form>
        </f:view> 
        <script type="text/javascript">
            
            /*function load() {            
                setFocus();    
            }   
            
            function setFocus() {
                document.getElementById('Form:input1').focus();
            } */   
            
        </script>
        
    </body>
</html>
