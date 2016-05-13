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
import java.util.Iterator;

import org.eclipse.datatools.connectivity.sqm.core.rte.jdbc.JDBCForeignKey;
import org.eclipse.datatools.connectivity.sqm.internal.core.RDBCorePlugin;
import org.eclipse.datatools.modelbase.sql.constraints.ForeignKey;
import org.eclipse.datatools.modelbase.sql.constraints.SQLConstraintsPackage;
import org.eclipse.datatools.modelbase.sql.schema.Database;
import org.eclipse.datatools.modelbase.sql.tables.Column;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * This class is the ForeignKey implementation.
 * 
 * @author seunghun_kim@cubrid.com
 *
 */
public class CUBRIDCatalogForeignKey extends JDBCForeignKey {

	private static final long serialVersionUID = 3833460717268643894L;

	private boolean eAnnotationLoaded = false;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.sqm.core.rte.jdbc.JDBCForeignKey#refresh()
	 */
	public void refresh() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.sqm.core.rte.jdbc.JDBCForeignKey#isSystemObject()
	 */
	public boolean isSystemObject() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.sqm.core.rte.jdbc.JDBCForeignKey#getEAnnotations()
	 */
	public EList getEAnnotations() {
		if (!this.eAnnotationLoaded)
			this.loadEAnnotations();
		return this.eAnnotations;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.sqm.core.rte.jdbc.JDBCForeignKey#getConnection()
	 */
	public Connection getConnection() {
		Database database = this.getCatalogDatabase();
		return ((CUBRIDCatalogDatabase) database).getConnection();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.sqm.core.rte.jdbc.JDBCForeignKey#getCatalogDatabase()
	 */
	public Database getCatalogDatabase() {
		return this.getBaseTable().getSchema().getDatabase();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.sqm.core.rte.jdbc.JDBCForeignKey#eIsSet(org.eclipse.emf.ecore.EStructuralFeature)
	 */
	public boolean eIsSet(EStructuralFeature eFeature) {
		int id = eDerivedStructuralFeatureID(eFeature);
		if (id == SQLConstraintsPackage.FOREIGN_KEY__EANNOTATIONS) {
			this.getEAnnotations();
		}

		return super.eIsSet(eFeature);
	}

	private synchronized void loadEAnnotations() {
		if (this.eAnnotationLoaded)
			return;
		this.eAnnotationLoaded = true;
		super.getEAnnotations();

		boolean deliver = this.eDeliver();
		this.eSetDeliver(false);

		CUBRIDCatalogForeignKey.setAsIdentifyingRelatinship(this, this
				.isIdentifyingRelationship(super.getMembers()));

		this.eSetDeliver(deliver);
	}

	public static void setAsIdentifyingRelatinship(ForeignKey fk,
			boolean identifying) {
		EAnnotation eAnnotation = fk
				.addEAnnotation(RDBCorePlugin.FK_MODELING_RELATIONSHIP);
		fk.addEAnnotationDetail(eAnnotation,
				RDBCorePlugin.FK_IS_IDENTIFYING_RELATIONSHIP, new Boolean(
						identifying).toString());
		fk.addEAnnotationDetail(eAnnotation,
				RDBCorePlugin.FK_CHILD_MULTIPLICITY, RDBCorePlugin.MANY);
		fk.addEAnnotationDetail(eAnnotation, RDBCorePlugin.FK_CHILD_ROLE_NAME,
				new String());
		fk.addEAnnotationDetail(eAnnotation,
				RDBCorePlugin.FK_PARENT_MULTIPLICITY,
				(fk.getMembers().size() > 0) ? RDBCorePlugin.ZERO_TO_ONE
						: RDBCorePlugin.ONE);
		fk.addEAnnotationDetail(eAnnotation, RDBCorePlugin.FK_PARENT_ROLE_NAME,
				new String());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.sqm.core.rte.jdbc.JDBCForeignKey#isIdentifyingRelationship(org.eclipse.emf.common.util.EList)
	 */
	public boolean isIdentifyingRelationship(EList columns) {
		boolean isIdentifying = true;
		Iterator it = columns.iterator();
		while (it.hasNext()) {
			Column column = (Column) it.next();
			if (!column.isPartOfPrimaryKey()) {
				isIdentifying = false;
				break;
			}
		}
		return isIdentifying;
	}

}
