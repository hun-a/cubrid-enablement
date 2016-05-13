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

import java.lang.ref.SoftReference;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.connectivity.sqm.core.definition.DatabaseDefinition;
import org.eclipse.datatools.connectivity.sqm.core.rte.jdbc.JDBCDatabase;
import org.eclipse.datatools.connectivity.sqm.core.util.CatalogLoaderOverrideManager;
import org.eclipse.datatools.connectivity.sqm.internal.core.RDBCorePlugin;
import org.eclipse.datatools.connectivity.sqm.loader.JDBCBaseLoader;
import org.eclipse.datatools.enablement.cubrid.catalog.loaders.CUBRIDAuthorizationIdentifierLoader;
import org.eclipse.datatools.modelbase.sql.accesscontrol.SQLAccessControlPackage;
import org.eclipse.datatools.modelbase.sql.schema.SQLSchemaPackage;
import org.eclipse.datatools.modelbase.sql.schema.Schema;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;

/**
 * This is the Database implementation, and contains schemas.
 * 
 * @author seunghun_kim@cubrid.com
 *
 */
public class CUBRIDCatalogDatabase extends JDBCDatabase {
	
	private Boolean schemasLoaded = Boolean.FALSE;
	private Boolean catalogsLoaded = Boolean.FALSE;
	private Boolean authorizationIdsLoaded = Boolean.FALSE;

	private transient SoftReference authorizationIdLoaderRef;

	public CUBRIDCatalogDatabase(Connection connection){
		super(connection);
	}

	public void refresh() {
		synchronized (schemasLoaded) {
			if (schemasLoaded.booleanValue()) {
				schemasLoaded = Boolean.FALSE;
			}
		}

		synchronized (catalogsLoaded) {
			if (catalogsLoaded.booleanValue()) {
				catalogsLoaded = Boolean.FALSE;
			}
		}

		synchronized (authorizationIdsLoaded) {
			if (authorizationIdsLoaded.booleanValue()) {
				authorizationIdsLoaded = Boolean.FALSE;
			}
		}

		super.refresh();
	}

	public EList getSchemas() {
		synchronized (schemasLoaded) {
			if(!schemasLoaded.booleanValue()) { 
				if (schemas == null) {
					schemas = new EObjectWithInverseResolvingEList(Schema.class, this,
							SQLSchemaPackage.DATABASE__SCHEMAS,
							SQLSchemaPackage.SCHEMA__DATABASE);
					Schema schema = new CUBRIDCatalogSchema();
					schema.setName(getName());
					schemas.add(schema);
					schemasLoaded = Boolean.TRUE;
				}
				return this.schemas;
			}
		}
		return super.getSchemas();
	}

	public EList getCatalogs() {
		synchronized (catalogsLoaded) {
			if(!catalogsLoaded.booleanValue()) { 
				if (catalogs == null) {
					catalogs = new EObjectWithInverseResolvingEList(Schema.class, this,
									SQLSchemaPackage.DATABASE__CATALOGS,
									SQLSchemaPackage.SCHEMA__DATABASE);
					catalogsLoaded = Boolean.TRUE;
				}
			}
		}
		return catalogs;
	}

	public EList getAuthorizationIds() {
		synchronized (this.authorizationIdsLoaded) {
			if (!authorizationIdsLoaded.booleanValue())
				this.loadAuthorizationIdentifiers();
		}

		return super.getAuthorizationIds();
	}

	private void loadAuthorizationIdentifiers() {
		boolean deliver = eDeliver();
		try {
			List container = super.getAuthorizationIds();
			List existingAuthorizationIds = new ArrayList(container);

			eSetDeliver(false);

			container.clear();
			getAuthorizationIdentifierLoader().loadAuthorizationIdentifiers(container, existingAuthorizationIds);
			getAuthorizationIdentifierLoader().clearAuthorizationIdentifiers(existingAuthorizationIds);

			authorizationIdsLoaded = Boolean.TRUE;

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			eSetDeliver(deliver);
		}
	}

	protected final CUBRIDAuthorizationIdentifierLoader getAuthorizationIdentifierLoader() {
		// cache the AuthorizationIdentifierLoader for better performance
		if (authorizationIdLoaderRef == null || authorizationIdLoaderRef.get() == null) {
			authorizationIdLoaderRef = new SoftReference(createAuthorizationIdentifierLoader());
		}

		return (CUBRIDAuthorizationIdentifierLoader) authorizationIdLoaderRef.get();
	}

	/**
	 * Creates and returns an instance of the AuthorizationIdentifierLoader. By default an instance of the
	 * <code>MySqlAuthorizationIdentifierLoader</code> is returned. This behavior can be changed by providing an
	 * <code>overrideLoader</code> using the eclass org.eclipse.datatools.modelbase.sql.accesscontrol.
	 * AuthorizationIdentifier.
	 * 
	 * @return An instance of MySqlAuthorizationIdentifierLoader.
	 */
	private CUBRIDAuthorizationIdentifierLoader createAuthorizationIdentifierLoader() {
		// get the database definition for the actual database
		DatabaseDefinition databaseDefinition = RDBCorePlugin.getDefault().getDatabaseDefinitionRegistry()
				.getDefinition(this.getCatalogDatabase());

		// see if someone is interested in providing an own authorization identifier loader
		JDBCBaseLoader loader = CatalogLoaderOverrideManager.INSTANCE.getLoaderForDatabase(databaseDefinition,
				SQLAccessControlPackage.eINSTANCE.getAuthorizationIdentifier().getInstanceClassName());

		if (loader != null) {
			CUBRIDAuthorizationIdentifierLoader authorizationIdLoader = (CUBRIDAuthorizationIdentifierLoader) loader;
			authorizationIdLoader.setCatalogObject(this);
			return authorizationIdLoader;
		}

		return new CUBRIDAuthorizationIdentifierLoader(this);
	}

}
