/*******************************************************************************
 * Copyright (c) 2008 Sybase, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Brian Fitzpatrick - initial API and implementation
 *******************************************************************************/
package org.eclipse.datatools.enablement.cubrid.dbdefcreation;

/*
 * Example file to create the db definition file for SQLite 
 */

import java.util.HashMap;
import java.util.Map;

import org.eclipse.datatools.modelbase.dbdefinition.CheckOption;
import org.eclipse.datatools.modelbase.dbdefinition.ColumnDefinition;
import org.eclipse.datatools.modelbase.dbdefinition.ConstraintDefinition;
import org.eclipse.datatools.modelbase.dbdefinition.DatabaseDefinitionFactory;
import org.eclipse.datatools.modelbase.dbdefinition.DatabaseVendorDefinition;
import org.eclipse.datatools.modelbase.dbdefinition.LengthUnit;
import org.eclipse.datatools.modelbase.dbdefinition.ParentDeleteDRIRuleType;
import org.eclipse.datatools.modelbase.dbdefinition.ParentUpdateDRIRuleType;
import org.eclipse.datatools.modelbase.dbdefinition.PredefinedDataTypeDefinition;
import org.eclipse.datatools.modelbase.dbdefinition.ViewDefinition;
import org.eclipse.datatools.modelbase.sql.datatypes.PrimitiveType;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

/**
 * @author brianf
 */
public class ExampleForVendorPrimitiveWrite {

	public static void main(String[] arg) {
		URI uri = URI.createFileURI("CUBRID_9.3.6.xmi");
		Resource rf = new XMIResourceImpl(uri);

		// Database vendor definitions
		DatabaseVendorDefinition databaseVendorDefinition = DatabaseDefinitionFactory.eINSTANCE.createDatabaseVendorDefinition();
		databaseVendorDefinition.setVendor("CUBRID");
		databaseVendorDefinition.setVersion("9.3.6");
		databaseVendorDefinition.setMaximumIdentifierLength(64);
		databaseVendorDefinition.setMaximumCommentLength(64);
		databaseVendorDefinition.setMQTSupported(true);
		databaseVendorDefinition.setAliasSupported(true);
//		databaseVendorDefinition.setSequenceSupported(true);
		databaseVendorDefinition.setStoredProcedureSupported(true);

		ColumnDefinition columnDefinition = DatabaseDefinitionFactory.eINSTANCE.createColumnDefinition();
		columnDefinition.setIdentitySupported(true);
		columnDefinition.setComputedSupported(true);
		columnDefinition.setIdentityStartValueSupported(true);
		columnDefinition.setIdentityIncrementSupported(true);
		columnDefinition.setIdentityMaximumSupported(true);
		columnDefinition.setIdentityMinimumSupported(true);
		columnDefinition.setIdentityCycleSupported(true);
		databaseVendorDefinition.setColumnDefinition(columnDefinition);

		ConstraintDefinition constraintDefinition = DatabaseDefinitionFactory.eINSTANCE.createConstraintDefinition();
		constraintDefinition.setClusteredPrimaryKeySupported(true);
		constraintDefinition.setClusteredUniqueConstraintSupported(true);
		constraintDefinition.getParentDeleteDRIRuleType().add(ParentDeleteDRIRuleType.RESTRICT_LITERAL);
		constraintDefinition.getParentDeleteDRIRuleType().add(ParentDeleteDRIRuleType.CASCADE_LITERAL);
		constraintDefinition.getParentDeleteDRIRuleType().add(ParentDeleteDRIRuleType.SET_NULL_LITERAL);
		constraintDefinition.getParentDeleteDRIRuleType().add(ParentDeleteDRIRuleType.NO_ACTION_LITERAL);
		constraintDefinition.getParentUpdateDRIRuleType().add(ParentUpdateDRIRuleType.RESTRICT_LITERAL);
		constraintDefinition.getParentUpdateDRIRuleType().add(ParentUpdateDRIRuleType.NO_ACTION_LITERAL);
		constraintDefinition.getCheckOption().add(CheckOption.NONE_LITERAL);
		constraintDefinition.getCheckOption().add(CheckOption.LOCAL_LITERAL);
		constraintDefinition.getCheckOption().add(CheckOption.CASCADE_LITERAL);
		databaseVendorDefinition.setConstraintDefinition(constraintDefinition);
		
		ViewDefinition viewDefinition = DatabaseDefinitionFactory.eINSTANCE.createViewDefinition();
		viewDefinition.setIndexSupported(true);
		viewDefinition.setCheckOptionSupported(true);
		viewDefinition.setCheckOptionLevelsSupported(true);
		databaseVendorDefinition.setViewDefinition(viewDefinition);
		
		// Primitive type definitions

		// INTEGER
		PredefinedDataTypeDefinition integerDataTypeDefinition = DatabaseDefinitionFactory.eINSTANCE.createPredefinedDataTypeDefinition();
		integerDataTypeDefinition.setPrimitiveType(PrimitiveType.INTEGER_LITERAL);
		integerDataTypeDefinition.getName().add("INTEGER");
		integerDataTypeDefinition.setKeyConstraintSupported(true);
		integerDataTypeDefinition.setIdentitySupported(true);
		integerDataTypeDefinition.getDefaultValueTypes().add("NULL");
		integerDataTypeDefinition.setJdbcEnumType(4);
		integerDataTypeDefinition.setJavaClassName("int");
		databaseVendorDefinition.getPredefinedDataTypeDefinitions().add(integerDataTypeDefinition);
		((XMIResource)rf).setID(integerDataTypeDefinition, PrimitiveType.INTEGER_LITERAL+"_1");

		// NUMERIC
		PredefinedDataTypeDefinition numericDataTypeDefinition = DatabaseDefinitionFactory.eINSTANCE.createPredefinedDataTypeDefinition();
		numericDataTypeDefinition.setPrimitiveType(PrimitiveType.NUMERIC_LITERAL);
		numericDataTypeDefinition.getName().add("NUMERIC");
		numericDataTypeDefinition.setPrecisionSupported(true);
		numericDataTypeDefinition.setScaleSupported(true);
		numericDataTypeDefinition.setKeyConstraintSupported(true);
		numericDataTypeDefinition.setIdentitySupported(true);
		numericDataTypeDefinition.getDefaultValueTypes().add("NULL");
		numericDataTypeDefinition.setJdbcEnumType(2);
		numericDataTypeDefinition.setJavaClassName("java.math.BigDecimal");
		databaseVendorDefinition.getPredefinedDataTypeDefinitions().add(numericDataTypeDefinition);
		((XMIResource)rf).setID(numericDataTypeDefinition, PrimitiveType.NUMERIC_LITERAL+"_1");

		// DECIMAL
		PredefinedDataTypeDefinition decimalDataTypeDefinition = DatabaseDefinitionFactory.eINSTANCE.createPredefinedDataTypeDefinition();
		decimalDataTypeDefinition.setPrimitiveType(PrimitiveType.DECIMAL_LITERAL);
		decimalDataTypeDefinition.getName().add("DECIMAL");
		decimalDataTypeDefinition.setPrecisionSupported(true);
		decimalDataTypeDefinition.setScaleSupported(true);
		decimalDataTypeDefinition.setKeyConstraintSupported(true);
		decimalDataTypeDefinition.setIdentitySupported(true);
		decimalDataTypeDefinition.getDefaultValueTypes().add("NULL");
		decimalDataTypeDefinition.setJdbcEnumType(3);
		decimalDataTypeDefinition.setJavaClassName("java.math.BigDecimal");
		databaseVendorDefinition.getPredefinedDataTypeDefinitions().add(decimalDataTypeDefinition);
		((XMIResource)rf).setID(decimalDataTypeDefinition, PrimitiveType.DECIMAL_LITERAL+"_1");
		
		// REAL
		PredefinedDataTypeDefinition realDataTypeDefinition = DatabaseDefinitionFactory.eINSTANCE.createPredefinedDataTypeDefinition();
		realDataTypeDefinition.setPrimitiveType(PrimitiveType.REAL_LITERAL);
		realDataTypeDefinition.getName().add("REAL");
		realDataTypeDefinition.setKeyConstraintSupported(true);
		realDataTypeDefinition.setIdentitySupported(true);
		realDataTypeDefinition.getDefaultValueTypes().add("NULL");
		realDataTypeDefinition.setJdbcEnumType(7);
		realDataTypeDefinition.setJavaClassName("float");
		databaseVendorDefinition.getPredefinedDataTypeDefinitions().add(realDataTypeDefinition);
		((XMIResource)rf).setID(realDataTypeDefinition, PrimitiveType.REAL_LITERAL+"_1");
		
		// FLOAT
		PredefinedDataTypeDefinition floatDataTypeDefinition = DatabaseDefinitionFactory.eINSTANCE.createPredefinedDataTypeDefinition();
		floatDataTypeDefinition.setPrimitiveType(PrimitiveType.FLOAT_LITERAL);
		floatDataTypeDefinition.getName().add("FLOAT");
		floatDataTypeDefinition.setKeyConstraintSupported(true);
		floatDataTypeDefinition.setIdentitySupported(true);
		floatDataTypeDefinition.getDefaultValueTypes().add("NULL");
		floatDataTypeDefinition.setJdbcEnumType(6);
		floatDataTypeDefinition.setJavaClassName("float");
		databaseVendorDefinition.getPredefinedDataTypeDefinitions().add(floatDataTypeDefinition);
		((XMIResource)rf).setID(floatDataTypeDefinition, PrimitiveType.FLOAT_LITERAL+"_1");
		
		// DOUBLE
		PredefinedDataTypeDefinition doubleDataTypeDefinition = DatabaseDefinitionFactory.eINSTANCE.createPredefinedDataTypeDefinition();
		doubleDataTypeDefinition.setPrimitiveType(PrimitiveType.DOUBLE_PRECISION_LITERAL);
		doubleDataTypeDefinition.getName().add("DOUBLE");
		doubleDataTypeDefinition.setPrecisionSupported(true);
		doubleDataTypeDefinition.setScaleSupported(true);
		doubleDataTypeDefinition.setKeyConstraintSupported(true);
		doubleDataTypeDefinition.setIdentitySupported(true);
		doubleDataTypeDefinition.getDefaultValueTypes().add("NULL");
		doubleDataTypeDefinition.setJdbcEnumType(8);
		doubleDataTypeDefinition.setJavaClassName("double");
		databaseVendorDefinition.getPredefinedDataTypeDefinitions().add(doubleDataTypeDefinition);
		((XMIResource)rf).setID(doubleDataTypeDefinition, PrimitiveType.DOUBLE_PRECISION_LITERAL+"_1");
		
		// DOUBLE PRECISION
		PredefinedDataTypeDefinition doublePrecisionDataTypeDefinition = DatabaseDefinitionFactory.eINSTANCE.createPredefinedDataTypeDefinition();
		doublePrecisionDataTypeDefinition.setPrimitiveType(PrimitiveType.DOUBLE_PRECISION_LITERAL);
		doublePrecisionDataTypeDefinition.getName().add("DOUBLE PRECISION");
		doublePrecisionDataTypeDefinition.setPrecisionSupported(true);
		doublePrecisionDataTypeDefinition.setScaleSupported(true);
		doublePrecisionDataTypeDefinition.setKeyConstraintSupported(true);
		doublePrecisionDataTypeDefinition.setIdentitySupported(true);
		doublePrecisionDataTypeDefinition.getDefaultValueTypes().add("NULL");
		doublePrecisionDataTypeDefinition.setJdbcEnumType(8);
		doublePrecisionDataTypeDefinition.setJavaClassName("double");
		databaseVendorDefinition.getPredefinedDataTypeDefinitions().add(doublePrecisionDataTypeDefinition);
		((XMIResource)rf).setID(doublePrecisionDataTypeDefinition, PrimitiveType.DOUBLE_PRECISION_LITERAL+"_2");
		
		// CHAR
		PredefinedDataTypeDefinition charDataTypeDefinition = DatabaseDefinitionFactory.eINSTANCE.createPredefinedDataTypeDefinition();
		charDataTypeDefinition.setPrimitiveType(PrimitiveType.CHARACTER_LITERAL);
		charDataTypeDefinition.getName().add("CHAR");
		charDataTypeDefinition.setMaximumLength(1073741823);
		charDataTypeDefinition.setKeyConstraintSupported(true);
		charDataTypeDefinition.getDefaultValueTypes().add("CURRENT_USER");
		charDataTypeDefinition.getDefaultValueTypes().add("NULL");
		charDataTypeDefinition.setLengthSupported(true);
		charDataTypeDefinition.setJdbcEnumType(1);
		charDataTypeDefinition.setJavaClassName("java.lang.String");
		databaseVendorDefinition.getPredefinedDataTypeDefinitions().add(charDataTypeDefinition);
		((XMIResource)rf).setID(charDataTypeDefinition, PrimitiveType.CHARACTER_LITERAL+"_1");
		
		// VARCHAR
		PredefinedDataTypeDefinition varcharDataTypeDefinition = DatabaseDefinitionFactory.eINSTANCE.createPredefinedDataTypeDefinition();
		varcharDataTypeDefinition.setPrimitiveType(PrimitiveType.CHARACTER_VARYING_LITERAL);
		varcharDataTypeDefinition.getName().add("VARCHAR");
		varcharDataTypeDefinition.setMaximumLength(1073741823);
		varcharDataTypeDefinition.setKeyConstraintSupported(true);
		varcharDataTypeDefinition.getDefaultValueTypes().add("CURRENT_USER");
		varcharDataTypeDefinition.getDefaultValueTypes().add("NULL");
		varcharDataTypeDefinition.setLengthSupported(true);
		varcharDataTypeDefinition.setJdbcEnumType(12);
		varcharDataTypeDefinition.setJavaClassName("java.lang.String");
		databaseVendorDefinition.getPredefinedDataTypeDefinitions().add(varcharDataTypeDefinition);
		((XMIResource)rf).setID(varcharDataTypeDefinition, PrimitiveType.CHARACTER_VARYING_LITERAL+"_1");

		// STRING
		PredefinedDataTypeDefinition stringDataTypeDefinition = DatabaseDefinitionFactory.eINSTANCE.createPredefinedDataTypeDefinition();
		stringDataTypeDefinition.setPrimitiveType(PrimitiveType.CHARACTER_VARYING_LITERAL);
		stringDataTypeDefinition.getName().add("STRING");
		stringDataTypeDefinition.setMaximumLength(1073741823);
		stringDataTypeDefinition.setKeyConstraintSupported(true);
		stringDataTypeDefinition.getDefaultValueTypes().add("CURRENT_USER");
		stringDataTypeDefinition.getDefaultValueTypes().add("NULL");
		stringDataTypeDefinition.setLengthSupported(true);
		stringDataTypeDefinition.setJdbcEnumType(12);
		stringDataTypeDefinition.setJavaClassName("java.lang.String");
		databaseVendorDefinition.getPredefinedDataTypeDefinitions().add(stringDataTypeDefinition);
		((XMIResource)rf).setID(stringDataTypeDefinition, PrimitiveType.CHARACTER_VARYING_LITERAL+"_2");

		// DATE
		PredefinedDataTypeDefinition dateDataTypeDefinition = DatabaseDefinitionFactory.eINSTANCE.createPredefinedDataTypeDefinition();
		dateDataTypeDefinition.setPrimitiveType(PrimitiveType.DATE_LITERAL);
		dateDataTypeDefinition.getName().add("DATE");
		dateDataTypeDefinition.setIdentitySupported(true);
		dateDataTypeDefinition.setKeyConstraintSupported(true);
		dateDataTypeDefinition.getDefaultValueTypes().add("NULL");
		dateDataTypeDefinition.setJdbcEnumType(91);
		dateDataTypeDefinition.setJavaClassName("java.sql.Date");
		databaseVendorDefinition.getPredefinedDataTypeDefinitions().add(dateDataTypeDefinition);
		((XMIResource)rf).setID(dateDataTypeDefinition, PrimitiveType.DATE_LITERAL+"_1");
		
		// TIME
		PredefinedDataTypeDefinition timeDataTypeDefinition = DatabaseDefinitionFactory.eINSTANCE.createPredefinedDataTypeDefinition();
		timeDataTypeDefinition.setPrimitiveType(PrimitiveType.TIME_LITERAL);
		timeDataTypeDefinition.getName().add("TIME");
		timeDataTypeDefinition.setIdentitySupported(true);
		timeDataTypeDefinition.setKeyConstraintSupported(true);
		timeDataTypeDefinition.getDefaultValueTypes().add("NULL");
		timeDataTypeDefinition.setJdbcEnumType(92);
		timeDataTypeDefinition.setJavaClassName("java.sql.Time");
		databaseVendorDefinition.getPredefinedDataTypeDefinitions().add(timeDataTypeDefinition);
		((XMIResource)rf).setID(timeDataTypeDefinition, PrimitiveType.TIME_LITERAL+"_1");
		
		// TIMESTAMP
		PredefinedDataTypeDefinition timestampDataTypeDefinition = DatabaseDefinitionFactory.eINSTANCE.createPredefinedDataTypeDefinition();
		timestampDataTypeDefinition.setPrimitiveType(PrimitiveType.TIMESTAMP_LITERAL);
		timestampDataTypeDefinition.getName().add("TIMESTAMP");
		timestampDataTypeDefinition.setIdentitySupported(true);
		timestampDataTypeDefinition.setKeyConstraintSupported(true);
		timestampDataTypeDefinition.getDefaultValueTypes().add("NULL");
		timestampDataTypeDefinition.setJdbcEnumType(93);
		timestampDataTypeDefinition.setJavaClassName("java.sql.Timestamp");
		databaseVendorDefinition.getPredefinedDataTypeDefinitions().add(timestampDataTypeDefinition);
		((XMIResource)rf).setID(timestampDataTypeDefinition, PrimitiveType.TIMESTAMP_LITERAL+"_1");
		
		// DATETIME
		PredefinedDataTypeDefinition datetimeDataTypeDefinition = DatabaseDefinitionFactory.eINSTANCE.createPredefinedDataTypeDefinition();
		datetimeDataTypeDefinition.setPrimitiveType(PrimitiveType.TIMESTAMP_LITERAL);
		datetimeDataTypeDefinition.getName().add("TIMESTAMP");
		datetimeDataTypeDefinition.setIdentitySupported(true);
		datetimeDataTypeDefinition.setKeyConstraintSupported(true);
		datetimeDataTypeDefinition.getDefaultValueTypes().add("NULL");
		datetimeDataTypeDefinition.setJdbcEnumType(93);
		datetimeDataTypeDefinition.setJavaClassName("java.sql.Date");
		databaseVendorDefinition.getPredefinedDataTypeDefinitions().add(datetimeDataTypeDefinition);
		((XMIResource)rf).setID(datetimeDataTypeDefinition, PrimitiveType.TIMESTAMP_LITERAL+"_2");
		
		// BIT
		PredefinedDataTypeDefinition bitDataTypeDefinition = DatabaseDefinitionFactory.eINSTANCE.createPredefinedDataTypeDefinition();
		bitDataTypeDefinition.setPrimitiveType(PrimitiveType.BINARY_LITERAL);
		bitDataTypeDefinition.getName().add("BIT");
		bitDataTypeDefinition.setLengthSupported(true);
		bitDataTypeDefinition.setKeyConstraintSupported(true);
		bitDataTypeDefinition.setLengthUnit(LengthUnit.BYTE_LITERAL);
		bitDataTypeDefinition.setJdbcEnumType(-2);
		bitDataTypeDefinition.setJavaClassName("byte[]");
		databaseVendorDefinition.getPredefinedDataTypeDefinitions().add(bitDataTypeDefinition);
		((XMIResource)rf).setID(bitDataTypeDefinition, PrimitiveType.BINARY_LITERAL+"_1");
		
		// BIT VARTING
		PredefinedDataTypeDefinition bitVaryingDataTypeDefinition = DatabaseDefinitionFactory.eINSTANCE.createPredefinedDataTypeDefinition();
		bitVaryingDataTypeDefinition.setPrimitiveType(PrimitiveType.BINARY_VARYING_LITERAL);
		bitVaryingDataTypeDefinition.getName().add("BIT VARYING");
		bitVaryingDataTypeDefinition.setLengthSupported(true);
		bitVaryingDataTypeDefinition.setKeyConstraintSupported(true);
		bitVaryingDataTypeDefinition.setLengthUnit(LengthUnit.BYTE_LITERAL);
		bitVaryingDataTypeDefinition.setJdbcEnumType(-3);
		bitVaryingDataTypeDefinition.setJavaClassName("byte[]");
		databaseVendorDefinition.getPredefinedDataTypeDefinitions().add(bitVaryingDataTypeDefinition);
		((XMIResource)rf).setID(bitVaryingDataTypeDefinition, PrimitiveType.BINARY_VARYING_LITERAL+"_1");
		
		// BLOB
		PredefinedDataTypeDefinition blobDataTypeDefinition = DatabaseDefinitionFactory.eINSTANCE.createPredefinedDataTypeDefinition();
		blobDataTypeDefinition.setPrimitiveType(PrimitiveType.BINARY_LARGE_OBJECT_LITERAL);
		blobDataTypeDefinition.getName().add("BLOB");
		blobDataTypeDefinition.setDefaultSupported(false);
		blobDataTypeDefinition.getDefaultValueTypes().add("NULL");
		blobDataTypeDefinition.setJdbcEnumType(2004);
		blobDataTypeDefinition.setJavaClassName("java.sql.Blob");
		databaseVendorDefinition.getPredefinedDataTypeDefinitions().add(blobDataTypeDefinition);
		((XMIResource)rf).setID(blobDataTypeDefinition, PrimitiveType.BINARY_LARGE_OBJECT_LITERAL+"_1");

		// CLOB
		PredefinedDataTypeDefinition clobDataTypeDefinition = DatabaseDefinitionFactory.eINSTANCE.createPredefinedDataTypeDefinition();
		clobDataTypeDefinition.setPrimitiveType(PrimitiveType.CHARACTER_LARGE_OBJECT_LITERAL);
		clobDataTypeDefinition.getName().add("CLOB");
		clobDataTypeDefinition.setKeyConstraintSupported(true);
		clobDataTypeDefinition.setDefaultSupported(false);
		clobDataTypeDefinition.setMaximumLength(1073741823);
		clobDataTypeDefinition.setJdbcEnumType(2005);
		clobDataTypeDefinition.setJavaClassName("java.lang.String");
		databaseVendorDefinition.getPredefinedDataTypeDefinitions().add(clobDataTypeDefinition);
		((XMIResource)rf).setID(clobDataTypeDefinition, PrimitiveType.CHARACTER_LARGE_OBJECT_LITERAL+"_1");

		if (rf != null) {
			EList resourceContents = rf.getContents();
			resourceContents.add(databaseVendorDefinition);
			try {
				Map options = new HashMap();
				options.put(XMIResource.OPTION_DECLARE_XML, Boolean.TRUE);
				rf.save(options);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
