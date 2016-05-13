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
package org.eclipse.datatools.enablement.cubrid.catalog.loaders;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.eclipse.datatools.connectivity.sqm.core.rte.ICatalogObject;
import org.eclipse.datatools.connectivity.sqm.loader.JDBCSchemaLoader;
import org.eclipse.datatools.modelbase.sql.schema.Schema;

public class CUBRIDSchemaLoader extends JDBCSchemaLoader {

	public CUBRIDSchemaLoader() {
		super(null, null);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.sqm.loader.JDBCSchemaLoader#initialize(org.eclipse.datatools.modelbase.sql.schema.Schema, java.sql.ResultSet)
	 */
	protected void initialize(Schema schema, ResultSet rs) throws SQLException {
		schema.setName("Default");
	}

	public void loadSchemas(List containmentList, Collection existingSchemas)
		throws SQLException {
		Schema schema = (Schema) getAndRemoveSQLObject(existingSchemas, "Default");
		if (schema == null) {
			schema = processRow(null);
			if (schema != null) {
				containmentList.add(schema);
			}
		}
		else {
			containmentList.add(schema);
			if (schema instanceof ICatalogObject) {
				((ICatalogObject) schema).refresh();
			}
		}
	}
}