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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.datatools.connectivity.sqm.core.rte.ICatalogProvider;
import org.eclipse.datatools.modelbase.sql.schema.Database;

public class CUBRIDCatalogProvider implements ICatalogProvider, IExecutableExtension {

    private String product;
    private String version;

	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		this.product = config.getAttribute("product"); //$NON-NLS-1$
		this.version = config.getAttribute("version"); //$NON-NLS-1$
	}

	public Database getCatalogDatabase(Connection connection) {
		Database database = new CUBRIDCatalogDatabase(connection);
		database.setVendor(this.product);
		database.setVersion(this.version);
		return database;
	}


}
