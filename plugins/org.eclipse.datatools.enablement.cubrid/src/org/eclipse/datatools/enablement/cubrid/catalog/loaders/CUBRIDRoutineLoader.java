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
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

import org.eclipse.datatools.connectivity.sqm.core.rte.ICatalogObject;
import org.eclipse.datatools.connectivity.sqm.core.rte.jdbc.JDBCProcedure;
import org.eclipse.datatools.connectivity.sqm.loader.JDBCRoutineLoader;
import org.eclipse.datatools.connectivity.sqm.loader.Messages;
import org.eclipse.datatools.connectivity.sqm.loader.JDBCRoutineLoader.IRoutineFactory;
import org.eclipse.datatools.enablement.cubrid.CUBRIDPlugin;
import org.eclipse.datatools.enablement.cubrid.log.PluginLogger;
import org.eclipse.datatools.modelbase.sql.routines.Routine;
import org.eclipse.datatools.modelbase.sql.routines.SQLRoutinesPackage;
import org.eclipse.datatools.modelbase.sql.schema.Schema;
import org.eclipse.emf.ecore.EClass;

import com.ibm.icu.text.MessageFormat;

public class CUBRIDRoutineLoader extends JDBCRoutineLoader {

	private IRoutineFactory mProcedureFactory;

	public CUBRIDRoutineLoader() {
		super(null, null);
		mProcedureFactory = new CUBRIDProcedureFactory();
	}
	
	public static class CUBRIDProcedureFactory implements IRoutineFactory {

		/**
		 * @see org.eclipse.datatools.connectivity.sqm.loader.JDBCRoutineLoader.IRoutineFactory#getRoutineEClass()
		 * 
		 * @return SQLRoutinesPackage.eINSTANCE.getProcedure()
		 */
		public EClass getRoutineEClass() {
			return SQLRoutinesPackage.eINSTANCE.getProcedure();
		}

		/**
		 * Creates and initializes a new Procedure object from the meta-data in
		 * the result set.
		 * 
		 * @see org.eclipse.datatools.connectivity.sqm.loader.JDBCRoutineLoader.IRoutineFactory#createRoutine(java.sql.ResultSet)
		 */
		public Routine createRoutine(ResultSet rs) throws SQLException {
			Routine retVal = newRoutine();
			initialize(retVal, rs);
			return retVal;
		}

		/**
		 * Internal factory method. The default implementation returns a new
		 * JDBCProcedure object.
		 * 
		 * @return a new Routine object
		 */
		protected Routine newRoutine() {
			return new JDBCProcedure();
		}

		/**
		 * Initializes the new Routine object using the meta-data in the result
		 * set. This method initializes the name and description of the
		 * procedure.
		 * 
		 * @param routine a new Routine object
		 * @param rs the result set
		 * @throws SQLException if anything goes wrong
		 */
		public void initialize(Routine routine, ResultSet rs)
				throws SQLException {
			routine.setName(rs.getString("sp_name"));
			routine.setDescription(rs.getString("sp_type"));
		}

	}

	protected ResultSet createResultSet() throws SQLException {
		String sql = "SELECT * FROM [db_stored_procedure]";
		String pattern = getJDBCFilterPattern();
		if (pattern != null) {
			sql += " WHERE sp_name LIKE '" + pattern + "'";
		}
		Statement stmt = getCatalogObject().getConnection().createStatement();
		return stmt.executeQuery(sql);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.sqm.loader.JDBCRoutineLoader#loadRoutines(java.util.List, java.util.Collection)
	 */
	public void loadRoutines(List containmentList, Collection existingRoutines)
			throws SQLException {
		ResultSet rs = null;
		try {
			initActiveFilter();
			for(rs = createResultSet(); rs.next(); ) {
				String spName = rs.getString("sp_name");
				Routine routine = (Routine) getAndRemoveSQLObject(
						existingRoutines, spName);
				if (routine == null) {
					routine = processRow(rs);
					
					if (routine != null) {
						containmentList.add(routine);
					}
				} else {
					if (isProcedure(rs)) {
						mProcedureFactory.initialize(routine, rs);
					}
					containmentList.add(routine);
					if (routine instanceof ICatalogObject) {
						((ICatalogObject) routine).refresh();
					}
				}
			}
		} finally {
			if (rs != null) {
				closeResultSet(rs);
			}
		}
	}
	
	protected Routine processRow(ResultSet rs) throws SQLException {
		IRoutineFactory routineFactory = mProcedureFactory;
		return routineFactory.createRoutine(rs);
	}
	
	protected boolean isProcedure(ResultSet rs) throws SQLException {
		return "FUNCTION".equals(rs.getString("sp_type")) ||
				"PROCEDURE".equals(rs.getString("sp_type"));
	}
}
