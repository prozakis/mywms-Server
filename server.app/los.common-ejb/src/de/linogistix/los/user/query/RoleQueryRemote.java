/*
 * StorageLocationQueryRemote.java
 *
 * Created on 14. September 2006, 06:59
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.user.query;


import javax.ejb.Remote;

import org.mywms.model.Role;

import de.linogistix.los.query.BusinessObjectQueryRemote;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Remote
public interface RoleQueryRemote extends BusinessObjectQueryRemote<Role>{ 
  
}
