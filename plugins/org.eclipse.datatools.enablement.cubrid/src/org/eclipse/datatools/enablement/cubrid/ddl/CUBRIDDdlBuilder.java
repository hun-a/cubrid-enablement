/*******************************************************************************
 * Copyright (c) 2016. Seunghun
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Seunghun - initial API and implementation
 *******************************************************************************/
package org.eclipse.datatools.enablement.cubrid.ddl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.datatools.enablement.cubrid.catalog.CUBRIDCatalogTable;
import org.eclipse.datatools.enablement.cubrid.ddl.shared.CUBRIDDdlBuilderHelper;
import org.eclipse.datatools.modelbase.sql.constraints.Index;
import org.eclipse.datatools.modelbase.sql.constraints.PrimaryKey;
import org.eclipse.datatools.modelbase.sql.statements.SQLStatement;
import org.eclipse.datatools.modelbase.sql.tables.ActionGranularityType;
import org.eclipse.datatools.modelbase.sql.tables.ActionTimeType;
import org.eclipse.datatools.modelbase.sql.tables.BaseTable;
import org.eclipse.datatools.modelbase.sql.tables.CheckType;
import org.eclipse.datatools.modelbase.sql.tables.Column;
import org.eclipse.datatools.modelbase.sql.tables.Table;
import org.eclipse.datatools.modelbase.sql.tables.Trigger;
import org.eclipse.datatools.modelbase.sql.tables.ViewTable;

/**
 * This class generates the actual sql statements for CUBRID
 * 
 * @author seunghun_kim@cubrid.com
 *
 */
public class CUBRIDDdlBuilder extends CUBRIDDdlBuilderHelper {

	public String dropIndex(Index index, boolean quoteIdentifiers,
			boolean qualifyNames) {
		/*
		 * **************************************************
		 * DROP [UNIQUE] INDEX index_name [ON table_name];	*
		 * **************************************************
		 * UNIQUE: Specifies that the index to be dropped is a unique index.
		 * index_name: Specifies the name of the index to be dropped.
		 * table_name: Specifies the name of the table whose index is dropped.
		 */
		Table table = index.getTable();
		return DROP + SPACE + INDEX + SPACE
				+ getName(index, quoteIdentifiers, qualifyNames) + SPACE
				+ ON + SPACE + getName(table, quoteIdentifiers, qualifyNames);
	}

	public String createTable(BaseTable table, boolean quoteIdentifiers,
			boolean qualifyNames, boolean generatePk) {
		PrimaryKey pk = table.getPrimaryKey();
		boolean hasPk = false;
		if (pk != null && pk.getMembers() != null && !pk.getMembers().isEmpty()) {
			hasPk = true;
		}

		String statement = CREATE + SPACE + TABLE + SPACE 
				+ getName(table, quoteIdentifiers, qualifyNames) + SPACE 
				+ LEFT_PARENTHESIS + NEWLINE;

		Iterator it = table.getColumns().iterator();
		while (it.hasNext()) {
			Column column = (Column) it.next();
			statement += TAB + getColumnString(column, quoteIdentifiers, generatePk);
			if (it.hasNext()) {
				statement += COMMA;
			} else if (hasPk && generatePk) {
				statement += COMMA;
			}
		}

		if (hasPk && generatePk) {
			setCreateDone(pk);
			String pkStatement = TAB + PRIMARY_KEY + SPACE + LEFT_PARENTHESIS;
			// PRIMARY KEY (class_a_id)
			ArrayList colList = new ArrayList();
			Iterator iter = pk.getMembers().iterator();
			while (iter.hasNext()) {
				Column c = (Column) iter.next();
				if (c.getIdentitySpecifier() != null) {
					colList.add(c);
				}
			}

			iter = pk.getMembers().iterator();
			while (iter.hasNext()) {
				Column c = (Column) iter.next();
				if (c.getIdentitySpecifier() == null) {
					colList.add(c);
				}
			}

			iter = colList.iterator();
			while (iter.hasNext()) {
				Column c = (Column) iter.next();
				String columnName = c.getName();

				if (quoteIdentifiers) {
					pkStatement += this.getQuotedString(columnName);
				} else {
					pkStatement += columnName;
				}

				if (iter.hasNext()) {
					pkStatement += COMMA;
				}
			}
			pkStatement += RIGHT_PARENTHESIS + NEWLINE;
			statement += pkStatement;
		}
		statement += RIGHT_PARENTHESIS;

		if (table instanceof CUBRIDCatalogTable) {
			CUBRIDCatalogTable cubridTable = (CUBRIDCatalogTable) table;
			String tableType = cubridTable.getTableType();
			if (tableType != null) {
				// This block is needless for CUBRID
			}
		}
		return statement;
	}

	public String createView(ViewTable view, boolean quoteIdentifiers,
			boolean qualifyNames) {
		/*
		 * CREATE [OR REPLACE] {VIEW | VCLASS} view_name
		 * [<subclass_definition>]
		 * [(view_column_name, ...)]
		 * [INHERIT <resolution>, ...]
		 * [AS <select_statement>]
		 * [WITH CHECK OPTION];
		 * 
		 * <subclass_definition> ::= {UNDER | AS SUBCLASS OF} table_name, ...
		 * <resulusion> ::= [CLASS] {column_name} OF superclass_name [AS alias]
		 * 
		 * WITH CHECK OPTION: If this option is specified, the update or insert operation
		 * is possible only when the condition specified in the WHERE clause of the
		 * <select_statement> is satisfied. Therefore, this option is to disallow the
		 * update of virtual table that violates the condition.  
		 */
		String viewDefinition = CREATE + SPACE + VIEW + SPACE
				+ getName(view, quoteIdentifiers, qualifyNames) + SPACE;
		String columns = getViewColumnList(view);
		if (columns != null) {
			viewDefinition += LEFT_PARENTHESIS + columns + RIGHT_PARENTHESIS + SPACE;
		}
		viewDefinition += AS + NEWLINE + view.getQueryExpression().getSQL();

		CheckType checkType = view.getCheckType();
		if (checkType == CheckType.CASCADED_LITERAL) {
			viewDefinition += NEWLINE + WITH + SPACE + CASCADED + SPACE + CHECK
					+ SPACE + OPTION;
		} else if (checkType == CheckType.LOCAL_LITERAL) {
			//			viewDefinition += NEWLINE + WITH + SPACE + LOCAL + SPACE + CHECK
			// WITH LOCAL CHECK OPTION is needless for CUBRID
			viewDefinition += NEWLINE + WITH + SPACE + CHECK
					+ SPACE + OPTION;
		}
		return viewDefinition;
	}

	public String createIndex(Index index, boolean quoteIdentifiers,
			boolean qualifyNames) {
		/*
		 * CREATE [UNIQUE] INDEX index_name ON table_name <index_col_desc>;
		 * 
		 * <index_col_desc> ::=
		 * 		( column_name [ASC | DESC] [{, column_name [ASC | DESC]} ...] ) 
		 * 		[WHERE <filter_predicate>]
		 * 		| ( function_name (argument_list) )
		 * 
		 * UNIQUE: Creates an index with unique values.
		 * index_name: Specifies the name of the index to be created. 
		 * 				The index name must be unique in the table.
		 * table_name: Specifies the name of the table where the index is to be created.
		 * column_name: Specifies the name of the column where the index is to be applied.
		 * 				To create a composite index, specify two or more column names.
		 * ASC | DESC: Specifies the sorting order of columns.
		 * <filter_predicate>: Defines the conditions to create filtered indexes.
		 * 						When there are several comparison conditions between a column and a constant,
		 * 						filtering is available only when the conditions are connected by using AND.
		 * 						Regarding this, definitely watch Filtered Index.
		 * function_name(argument_list): Defines the conditions to create function-base indexes.
		 * 								Regarding this, definitely watch Function-based Index.
		 */
		// TODO need to implement the Function-based Index.
		String statement = CREATE + SPACE;
		if (index.isUnique()) {
			statement += UNIQUE + SPACE;
		}
		return statement += INDEX + SPACE
				+ getName(index, quoteIdentifiers, qualifyNames) + SPACE
				+ ON + SPACE 
				+ getName(index.getTable(), quoteIdentifiers, qualifyNames) + SPACE
				+ LEFT_PARENTHESIS 
				+ getIndexKeyColumns(index, quoteIdentifiers)
				+ RIGHT_PARENTHESIS;
	}

	public String createTrigger(Trigger trigger, boolean quoteIdentifiers,
			boolean qualifyNames) {
		/*
		 * CREATE TRIGGER trigger_name
		 * [ STATUS { ACTIVE | INACTIVE } ]
		 * [ PRIORITY key ]
		 * <event_time> <event_type> [<event_target>]
		 * [ IF condition ]
		 * EXECUTE [ AFTER | DEFERRED ] action ;
		 * 
		 * <event_time> ::= BEFORE | AFTER | DEFERRED
		 * 
		 * <event_type> ::= INSERT | STATEMENT INSERT |
		 * 					UPDATE | STATEMENT UPDATE |
		 * 					DELETE | STATEMENT DELETE |
		 * 					ROLLBACK | COMMIT
		 * 
		 * <event_target> ::= ON table_name |
		 * 						ON table_name [ (column_name) ]
		 *
		 * <condition> ::= expression
		 * 
		 * <action> ::= REJECT | INVALIDATE TRANSACTION |
		 * 				PRINT message_string | 
		 * 				INSERT statement | UPDATE statement |
		 * 				DELETE statement
		 */
		String statement = CREATE + SPACE + TRIGGER + SPACE
				+ getName(trigger, quoteIdentifiers, qualifyNames) + SPACE;

		final ActionTimeType actionTime = trigger.getActionTime();
		if (actionTime == ActionTimeType.AFTER_LITERAL) {
			statement += AFTER + SPACE;
		} else if (actionTime == ActionTimeType.BEFORE_LITERAL) {
			statement += BEFORE + SPACE;
		} else if (actionTime == ActionTimeType.INSTEADOF_LITERAL) {
			// Nothing to do
		}

		if (trigger.isDeleteType()) {
			statement += DELETE + SPACE;
		} else if (trigger.isInsertType()) {
			statement += INSERT + SPACE;
		} else if (trigger.isUpdateType()) {
			statement += UPDATE + SPACE;
		} 
		statement += ON + SPACE 
				+ getName(trigger.getSubjectTable(), quoteIdentifiers, qualifyNames);

		Collection columns = trigger.getTriggerColumn();
		if (columns != null) {
			statement += LEFT_PARENTHESIS;
			Iterator it = columns.iterator();
			while (it.hasNext()) {
				Column c = (Column) it.next();
				statement += c.getName();
				if (it.hasNext()) {
					statement += COMMA + SPACE;
				}
			}
			statement += RIGHT_PARENTHESIS;
		}
		statement += NEWLINE;
		
		Iterator it = trigger.getActionStatement().iterator();
		while (it.hasNext()) {
			SQLStatement s = (SQLStatement) it.next();
			statement += s.getSQL();
		}
		// TODO need to implement the condition and action statement. 
		
		// 1. condition 
		
		// 2. action 

		return statement;
	}

	protected String getName(Index index, boolean quoteIdentifiers,
			boolean qualifyNames) {
		String indexName = index.getName();
		String dbName = null;

		if (quoteIdentifiers) {
			indexName = this.getQuotedString(indexName);
			if (qualifyNames) {
				dbName = index.getSchema().getDatabase().getName();
				dbName = this.getQuotedString(dbName);
				indexName = dbName + DOT + indexName;
			}
		}
		return indexName;
	}
}
