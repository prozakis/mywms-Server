/*
/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;

import org.mywms.model.Client;

import de.linogistix.los.model.LOSSystemProperty;
import de.linogistix.los.query.dto.LOSSystemPropertyTO;

/**
 * @author krane
 *
 */
@Stateless
public class LOSSystemPropertyQueryBean extends BusinessObjectQueryBean<LOSSystemProperty> implements LOSSystemPropertyQueryRemote {

	
	static List<BODTOConstructorProperty> BODTOConstructorProperties = new ArrayList<BODTOConstructorProperty>();

	static{
		BODTOConstructorProperties.add(new BODTOConstructorProperty("id", false));
		BODTOConstructorProperties.add(new BODTOConstructorProperty("version", false));
		BODTOConstructorProperties.add(new BODTOConstructorProperty("groupName", false));
		BODTOConstructorProperties.add(new BODTOConstructorProperty("key", false));
		BODTOConstructorProperties.add(new BODTOConstructorProperty("client.number", false));
		BODTOConstructorProperties.add(new BODTOConstructorProperty("workstation", false));
		BODTOConstructorProperties.add(new BODTOConstructorProperty("value", false));
	}
	
	@Override
	protected List<BODTOConstructorProperty> getBODTOConstructorProperties() {
		return BODTOConstructorProperties;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class getBODTOClass() {
		return LOSSystemPropertyTO.class;
	}

	public String getUniqueNameProp() {
	    return "key";
	}

	
    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
    	Long id;
		try{
			id = Long.parseLong(value);
		} catch (Throwable t){
			id = Long.valueOf(-1);
		}
		TemplateQueryWhereToken idt = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_EQUAL, "id", id);
		idt.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(idt);

		TemplateQueryWhereToken client= new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "client.number",
				value);
		client.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(client);

		TemplateQueryWhereToken key = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "key",
				value);
		key.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(key);
		
		TemplateQueryWhereToken workstation = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "workstation",
				value);
		workstation.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(workstation);

		TemplateQueryWhereToken valuet = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "value",
				value);
		valuet.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(valuet);
		
		TemplateQueryWhereToken group = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "groupName",
				value);
		group.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(group);

		return ret;
	}

    @Override
	public  LOSResultList<BODTO<LOSSystemProperty>> autoCompletion(String typed, Client client,
			TemplateQueryWhereToken[] tokens, QueryDetail det) {
    	
    	List<TemplateQueryWhereToken>tokenList = new ArrayList<TemplateQueryWhereToken>(Arrays.asList(tokens));
		
		TemplateQueryWhereToken group = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_EQUAL, "hidden", false);
		group.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
		tokenList.add(group);
		
		return super.autoCompletion(typed, client, tokenList.toArray(tokens), det);
	}

}
