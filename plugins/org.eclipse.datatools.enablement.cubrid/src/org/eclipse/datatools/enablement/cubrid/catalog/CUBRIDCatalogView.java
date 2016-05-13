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

import java.util.Iterator;

import org.eclipse.datatools.connectivity.sqm.core.rte.jdbc.JDBCView;
import org.eclipse.datatools.modelbase.sql.constraints.Index;
import org.eclipse.datatools.modelbase.sql.constraints.IndexMember;
import org.eclipse.datatools.modelbase.sql.schema.Database;
import org.eclipse.emf.common.util.EList;

/**
 * 
 * @author Seunghun
 *
 */
public class CUBRIDCatalogView extends JDBCView {
	
	private static final long serialVersionUID = 3761127145711088689L;

	public Index findIndexWithColumnName(String colName) {
		EList eList = this.getIndex();
		for (Iterator it = eList.iterator(); it.hasNext();) {
			CUBRIDCatalogIndex index = (CUBRIDCatalogIndex) it.next();
			EList list = index.getMembers();
			for (Iterator iter = list.iterator(); iter.hasNext();) {
				IndexMember member = (IndexMember) iter.next();
				if (member.getColumn().getName().equals(colName)) {
					return index;
				}
			}
		}
		return null;
	}

	public Database getCatalogDatabase() {
		return this.getSchema().getDatabase();		
	}
}
