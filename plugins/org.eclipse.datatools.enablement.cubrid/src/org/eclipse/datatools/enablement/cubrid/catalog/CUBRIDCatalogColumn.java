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

import java.sql.Connection;

import org.eclipse.datatools.connectivity.sqm.core.rte.jdbc.JDBCColumn;
import org.eclipse.datatools.modelbase.sql.schema.Database;

/**
 * This class holds the column information.
 * 
 * @author seunghun_kim@cubrid.com
 *
 */
public class CUBRIDCatalogColumn extends JDBCColumn {

	private static final long serialVersionUID = 3257008765202151480L;

	public Connection getConnection() {
		Database database = this.getCatalogDatabase();
		return ((CUBRIDCatalogDatabase) database).getConnection();
	}

	public Database getCatalogDatabase() {
		return this.getTable().getSchema().getDatabase();
	}
}