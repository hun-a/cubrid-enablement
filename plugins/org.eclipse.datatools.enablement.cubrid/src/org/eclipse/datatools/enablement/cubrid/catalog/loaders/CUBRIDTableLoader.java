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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.datatools.connectivity.sqm.core.rte.ICatalogObject;
import org.eclipse.datatools.connectivity.sqm.core.rte.jdbc.JDBCView;
import org.eclipse.datatools.connectivity.sqm.loader.JDBCTableLoader;
import org.eclipse.datatools.connectivity.sqm.loader.Messages;
import org.eclipse.datatools.connectivity.sqm.loader.JDBCTableLoader.ITableFactory;
import org.eclipse.datatools.enablement.cubrid.catalog.CUBRIDCatalogTable;
import org.eclipse.datatools.enablement.cubrid.catalog.CUBRIDCatalogView;
import org.eclipse.datatools.modelbase.sql.tables.SQLTablesPackage;
import org.eclipse.datatools.modelbase.sql.tables.Table;
import org.eclipse.emf.ecore.EClass;

import com.ibm.icu.text.MessageFormat;

public class CUBRIDTableLoader extends JDBCTableLoader  {
	
	private static final String COL_TABLE_NAME = "class_name";	//$NON-NLS-1$
	private static final String COL_TABLE_TYPE = "class_type";	//$NON-NLS-1$
	private static final String TABLE = "TABLE";	//$NON-NLS-1$
	private static final String VIEW = "VIEW";	//$NON-NLS-1$
	
	private static final String[] POSSIBLE_TABLE_TYPE_COL_NAMES =
			new String[] {TABLE, VIEW}; //$NON-NLS-1$ //$NON-NLS-2$
	
	public CUBRIDTableLoader() {
		super(null, null);
		registerTableFactory(TABLE, new CUBRIDTableFactory());
		registerTableFactory(VIEW, new CUBRIDViewFactory());
	}

	/**
	 * Base factory implementation for tables.
	 */
	public static class CUBRIDTableFactory extends TableFactory {

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.datatools.connectivity.sqm.loader.JDBCTableLoader.TableFactory#newTable()
		 */
		protected Table newTable() {
			return new CUBRIDCatalogTable();
		}

		/*
		 * Initializes the isLocal attribute in addition to the the attributes
		 * initialized by super().
		 * 
		 * @see org.eclipse.datatools.connectivity.sqm.loader.JDBCTableLoader.TableFactory#initialize(org.eclipse.datatools.modelbase.sql.tables.Table,
		 *      java.sql.ResultSet)
		 */
		public void initialize(Table table, ResultSet rs)
				throws SQLException {
			table.setName(rs.getString(COL_TABLE_NAME));
		}
	}
	
	/**
	 * Base factory implementation for views.
	 */
	public static class CUBRIDViewFactory extends TableFactory {
		
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.datatools.connectivity.sqm.loader.JDBCTableLoader.TableFactory#getTableEClass()
		 */
		public EClass getTableEClass() {
			return SQLTablesPackage.eINSTANCE.getViewTable();
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.datatools.connectivity.sqm.loader.JDBCTableLoader.TableFactory#newTable()
		 */
		protected Table newTable() {
			return new CUBRIDCatalogView();
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.datatools.connectivity.sqm.loader.JDBCTableLoader.TableFactory#initialize(org.eclipse.datatools.modelbase.sql.tables.Table, java.sql.ResultSet)
		 */
		public void initialize(Table table, ResultSet rs)
			throws SQLException {
			table.setName(rs.getString(COL_TABLE_NAME));
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.sqm.loader.JDBCTableLoader#loadTables(java.util.List, java.util.Collection)
	 */
	public void loadTables(List containmentList, Collection existingTables)
		throws SQLException {
		ResultSet rs = null;
		try {
			initActiveFilter();
			rs = createResultSet();
			
			if (!mSupportedColumnsInitialized) {
				Set supportedColumns = new TreeSet();
				ResultSetMetaData rsmd = rs.getMetaData();
				for (int colNum = 1, colCount = rsmd.getColumnCount(); colNum <= colCount; ++colNum) {
					supportedColumns.add(rsmd.getColumnName(colNum));
				}
				for (Iterator it = mTableFactories.values().iterator(); it
						.hasNext();) {
					((ITableFactory) it.next())
					.setSupportedColumns(supportedColumns);
				}
			}
			
			while (rs.next()) {
				String tableName = rs.getString(COL_TABLE_NAME);
				if (tableName == null || isFiltered(tableName)) {
					continue;
				}
				
				Table table = (Table) getAndRemoveSQLObject(
						existingTables, tableName);
				if (table == null) {
					table = processRow(rs);
					
					if (table != null) {
						containmentList.add(table);
					}
				} else {
					ITableFactory tableFactory = getTableFactory(rs
							.getString(COL_TABLE_TYPE));
					if (tableFactory != null) {
						tableFactory.initialize(table, rs);
					}
					containmentList.add(table);
					if (table instanceof ICatalogObject) {
						((ICatalogObject) table).refresh();
					}
				}
			}
		} finally {
			if (rs != null) {
				closeResultSet(rs);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.sqm.loader.JDBCTableLoader#createResultSet()
	 */
	protected ResultSet createResultSet() throws SQLException {
		try {
			String query = "SELECT * FROM db_class";
			if (getJDBCFilterPattern() != null && getJDBCFilterPattern().trim().length() > 0)
				query += " WHERE class_name LIKE '" + getJDBCFilterPattern() + "'"; //$NON-NLS-1$ //$NON-NLS-2$
			Statement s = getCatalogObject().getConnection().createStatement();
			ResultSet r = s.executeQuery(query);
			return r;
		}
		catch (RuntimeException e) {
			SQLException error = new SQLException(MessageFormat.format(
					Messages.Error_Unsupported_DatabaseMetaData_Method,
					new Object[] { "java.sql.DatabaseMetaData.getTables()"})); //$NON-NLS-1$
			error.initCause(e);
			throw error;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.sqm.loader.JDBCTableLoader#processRow(java.sql.ResultSet)
	 */
	protected Table processRow(ResultSet rs) throws SQLException {
		String tableType = rs.getString(COL_TABLE_TYPE); 
		ITableFactory tableFactory = null;
		
		if ("CLASS".equals(tableType.toUpperCase())) {	//$NON-NLS-1$
			tableFactory = getTableFactory(TABLE);
		} else if ("VCLASS".equals(tableType.toUpperCase())) {	//$NON-NLS-1$
			if (rs.getString("is_system_class").equals("YES")) {
				tableFactory = getTableFactory(TABLE);
			} else {
				tableFactory = getTableFactory(VIEW);
			}
		}
		 
		if (tableFactory == null) {
			return null;
		}
		
		return tableFactory.createTable(rs);
	}
}
