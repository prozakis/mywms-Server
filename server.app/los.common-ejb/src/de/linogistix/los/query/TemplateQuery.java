/*
 * TemplateQuery.java
 *
 * Created on 8. Oktober 2006, 00:05
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.los.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Used for querying BusinessObjects by template.
 * 
 * @see QueryDetail
 * @see TemplateQueryWhereToken
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class TemplateQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Class<? extends Object> boClass;
    private List<TemplateQueryFilter> filters = new ArrayList<TemplateQueryFilter>();
    TemplateQueryFilter defaultFilter;
    private Class<? extends Object> newExprClass;
    
    private List<BODTOConstructorProperty> newExprProperties = new ArrayList<BODTOConstructorProperty>();
    
    private boolean distinct = false;

    
    
    public TemplateQuery() {
    	defaultFilter = addNewFilter();
    }

    public boolean isDistinct() {
		return distinct;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}


    public TemplateQueryFilter addNewFilter() {
    	TemplateQueryFilter filter = new TemplateQueryFilter();
    	filters.add(filter);
    	return filter;
    }
    
    public String getEntitySimpleClassname() {
        return getBoClass().getSimpleName();
    }



    /**
     * Constructor Expressions in the SELECT Clause. The clazz must have a constructor
     * that has as many arguments as <code>properties</code> has elements. Furthermore
     * the argumemts must match the 
     * entity properties specified by their names in  <code>properties</code>.
     *
     *@param clazz the class of the returned object
     *@param properties the properties of the entity that are used in the constructor
     */
    @SuppressWarnings("unchecked")
	void setSelectExpressionNew(Class<BODTO> clazz, List<BODTOConstructorProperty> properties) {

        if (properties == null || properties.size() < 1) {
            throw new IllegalArgumentException();
        }

        this.newExprClass = clazz;
        this.newExprProperties = properties;
    }

    public String getStatement() {
        StringBuffer s = new StringBuffer();

        s.append("SELECT ");
        
        if (isDistinct()) s.append(" DISTINCT");
        
        if (this.newExprClass == null) {
            s.append(" o FROM ");
        } else {
            s.append(getConstructorPropsStatement(this.newExprClass, this.newExprProperties));
            s.append(" FROM ");
        }
        s.append(getEntitySimpleClassname());
        s.append(" o ");
        
        for(BODTOConstructorProperty p:newExprProperties){
        	
        	if(p.isNullableReference()){
        		s.append("LEFT JOIN o."+p.getPropertyName()+" ");
        	}
        }

        return s.toString() + getWhereStatement();
    }
    
    public String getCountStatement() {
        StringBuffer s = new StringBuffer();

        s.append("SELECT ");
        s.append("count(o) FROM ");
        
        s.append(getEntitySimpleClassname());
        s.append(" o ");

        return s.toString() + getWhereStatement();
    }


    private String getWhereStatement() {
        StringBuffer where = new StringBuffer();
        boolean isNew = true;

        if( filters != null && filters.size() > 0 ) {
        	for( TemplateQueryFilter filter : filters ) {
        		String stmt = filter.getFilterStatement();
        		if( stmt == null || stmt.length() == 0 ) {
        			continue;
        		}
        		if( isNew ) {
        			isNew = false;
        		}
        		else {
        			where.append(" AND ");
        		}
        		where.append( stmt );
        	}
        }
        String whereStr = where.toString().trim();
        if( whereStr.length() > 0 ) {
        	return " WHERE " + whereStr;
        }
        return "";
    }
    
    public void setBoClass(Class<? extends Object> boClass) {
        this.boClass = boClass;
    }

    public Class<? extends Object> getBoClass() {
        return boClass;
    }

    public void addWhereToken(TemplateQueryWhereToken token) {
        defaultFilter.whereTokens.add(token);
    }
    
	public void setWhereTokens(List<TemplateQueryWhereToken> whereTokens) {
		defaultFilter.whereTokens = whereTokens;
	}

	public List<TemplateQueryWhereToken> getWhereTokens() {
		List<TemplateQueryWhereToken> tokens = new ArrayList<TemplateQueryWhereToken>();
		for( TemplateQueryFilter filter : filters ) {
			tokens.addAll(filter.getWhereTokens());
		}
		return tokens;
	}
	
//    /*
//     * Filters tokens with same parameter and parametername and appends a number
//     */
//    void processWhereTokens(){
//    	Map<String, Integer> m = new HashMap<String, Integer>();
//    	Map<String, String> p = new HashMap<String, String>();
//    	for (TemplateQueryWhereToken t : getWhereTokens()){
//    		
//    		if (p.containsKey(t.getParameterName())){
//    			String paramterExist = p.get(t.getParameterName());
//    			if (paramterExist.equals(t.getParameter())){
//	    			if (m.containsKey(t.getParameterName())){
//	        			Integer i = m.get(t.getParameterName());
//	        			t.setParameterName(t.getParameterName() + i.toString());
//	        			i = new Integer(i.intValue() + 1);
//	        			m.put(t.getParameterName(), i);
//	        		} else{
//	        			m.put(t.getParameterName(), new Integer(1));
//	        		}
//    			}
//    		} else{
//    			p.put(t.getParameterName(), t.getParameter());
//    		}
//    	}
//    }
    
    
    @Deprecated
    public static String getConstructorPropsStatement(Class<? extends Object> newExprClass, 
    												  String[] constructorProperties)
    {
    	List<BODTOConstructorProperty> propList = new ArrayList<BODTOConstructorProperty>();
    	
    	for(String p:constructorProperties){
    		propList.add(new BODTOConstructorProperty(p, false));
    	}
    	
    	return getConstructorPropsStatement(newExprClass, propList);
    }
    
    public static String getConstructorPropsStatement(Class<? extends Object> newExprClass, 
    												  List<BODTOConstructorProperty> constructorProperties)
    {
    	StringBuffer s = new StringBuffer();
    	s.append(" NEW ");
        s.append(newExprClass.getName());
        s.append("(");
        for (int i = 0; i < constructorProperties.size(); i++) {
            if (i > 0) {
                s.append(", ");
            }
            s.append("o.");
            s.append(constructorProperties.get(i).getPropertyName());
        }
        s.append(")");
        
        return new String(s);
    }

	public Class<? extends Object> getNewExprClass() {
		return newExprClass;
	}

	public List<BODTOConstructorProperty> getNewExprProperties() {
		return newExprProperties;
	}
    
    
}
