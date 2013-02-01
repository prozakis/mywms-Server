/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mywms.model.BasicClientAssignedEntity;

@Entity
@Table(name="los_sysprop", uniqueConstraints={
    @UniqueConstraint(columnNames={"client_id","syskey","workstation"})
})

public class LOSSystemProperty extends BasicClientAssignedEntity {

	private static final long serialVersionUID = 1L;
	
	public final static String WORKSTATION_DEFAULT = "DEFAULT";

	private String key;
	
	private String value;

	private String description;
	
	private String workstation = WORKSTATION_DEFAULT;
	
	private String groupName;
	
	private boolean hidden = false;
	
	@Column(name="syskey", nullable=false)
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Column(name="sysvalue", nullable=false)
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Column(nullable=false)
	public String getWorkstation() {
		return workstation;
	}
	public void setWorkstation(String workstation) {
		this.workstation = workstation;
	}

	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public boolean isHidden() {
		return hidden;
	}
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	
	
}
