/*******************************************************************************
 * Copyright (c) 2016 Seunghun
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Seunghun - initial API and implementation
 *******************************************************************************/
package org.eclipse.datatools.enablement.cubrid.catalog;


import org.eclipse.datatools.connectivity.sqm.core.rte.jdbc.JDBCSchema;
import org.eclipse.datatools.modelbase.sql.schema.Database;

/**
 * This class is the Schema implementation, its purpose is to load tables.
 * 
 * @author seunghun_kim@cubrid.com
 *
 */
public class CUBRIDCatalogSchema extends JDBCSchema {

	public Database getCatalogDatabase() {
		return super.getDatabase();
	}

}