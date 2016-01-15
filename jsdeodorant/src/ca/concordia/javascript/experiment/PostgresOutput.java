package ca.concordia.javascript.experiment;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import ca.concordia.javascript.analysis.abstraction.FunctionInvocation;
import ca.concordia.javascript.analysis.abstraction.Module;
import ca.concordia.javascript.analysis.abstraction.ObjectCreation;
import ca.concordia.javascript.analysis.module.LibraryType;

public class PostgresOutput {
	private Connection connection;
	private int iterationId = 1;

	public PostgresOutput(String folderPath) {
		connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/jsdeodorant");
			setExperimentIterationId(folderPath);
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}
	}

	private void setExperimentIterationId(String folderPath) throws SQLException {
		ResultSet result = executeStatement("SELECT id FROM experiment_iteration ORDER BY time desc limit 1");
		if (result.next())
			iterationId = result.getInt(1) + 1;
		PreparedStatement query = getPreparedStatement("INSERT INTO experiment_iteration VALUES (?, ?, ?)");
		query.setInt(1, iterationId);
		query.setTimestamp(2, new Timestamp(new Date().getTime()));
		query.setString(3, folderPath);
		executePreparedStatement(query);
	}

	public void logModuleInfo(Module module) {
		PreparedStatement query = getPreparedStatement("INSERT INTO module VALUES (?, ?, ?, ?, ?)");
		try {
			query.setInt(1, iterationId);
			query.setString(2, new File(module.getSourceFile().getOriginalPath()).getCanonicalPath());
			query.setString(3, module.getLibraryType().toString());
			query.setInt(4, module.getDependencies().size());
			query.setInt(5, module.getExports().size());
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
		executePreparedStatement(query);
	}

	public void logFunctionsAndClasses(Module module) {
		for (ObjectCreation creation : module.getProgram().getObjectCreationList()) {
			if (creation.isClassDeclarationPredefined()) {
				insertIntoModuleFunctions(creation.getOperandOfNewName(), creation.getOperandOfNewName(), LibraryType.JS_PREDEFINED.toString(), "", true, creation.getArguments().size(), 0, "", module.getCanonicalPath(), "");
			}
			if (creation.getClassDeclaration() != null) {
				insertIntoModuleFunctions(creation.getOperandOfNewName(), creation.getClassDeclarationQualifiedName(), creation.getClassDeclarationModule().getLibraryType().toString(), creation.getClassDeclaration().getKind().toString(), true, creation.getArguments().size(), creation.getClassDeclaration().getParameters().size(), LogUtil.getParametersName(creation.getClassDeclaration().getParameters()), module.getCanonicalPath(), creation.getClassDeclarationModule().getCanonicalPath());
			}
		}
		for (FunctionInvocation functionInvocation : module.getProgram().getFunctionInvocationList()) {
			String functionDefinitionName = "";
			String definitionModuleType = "";
			String definitionFunctionType = "";
			int parameterSize = -1;
			String parameterName = "";
			String definitionModulePath = "";
			if (functionInvocation.isPredefined()) {
				functionDefinitionName = functionInvocation.getPredefinedName();
				definitionModuleType = LibraryType.JS_PREDEFINED.toString();
			} else {
				if (functionInvocation.getFunctionDeclaration() != null) {
					functionDefinitionName = functionInvocation.getFunctionDeclaration().getName();
					definitionModuleType = functionInvocation.getFunctionDeclarationModule().getLibraryType().toString();
					definitionFunctionType = functionInvocation.getFunctionDeclaration().getKind().toString();
					parameterSize = functionInvocation.getFunctionDeclaration().getParameters().size();
					parameterName = LogUtil.getParametersName(functionInvocation.getFunctionDeclaration().getParameters());
					definitionModulePath = functionInvocation.getFunctionDeclarationModule().getCanonicalPath();
				}
			}
			insertIntoModuleFunctions(functionInvocation.getIdentifier().toString(), functionDefinitionName, definitionModuleType, definitionFunctionType, false, functionInvocation.getArguments().size(), parameterSize, parameterName, module.getCanonicalPath(), definitionModulePath);
		}
	}

	private void insertIntoModuleFunctions(String invocationName, String definitionName, String definitionModuleType, String definitionFunctionType, boolean isClass, int numberOfArguments, int numberOfParameters, String parameterNames, String invocationLocation, String definitionLocation) {
		PreparedStatement query = getPreparedStatement("INSERT INTO module_functions VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		try {
			query.setInt(1, iterationId);
			query.setString(2, invocationName);
			query.setString(3, definitionName);
			query.setString(4, definitionModuleType);
			query.setString(5, definitionFunctionType);
			query.setBoolean(6, isClass);
			query.setInt(7, numberOfArguments);
			query.setInt(8, numberOfParameters);
			query.setString(9, parameterNames);
			query.setString(10, invocationLocation);
			query.setString(11, definitionLocation);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		executePreparedStatement(query);
	}

	private void executePreparedStatement(PreparedStatement query) {
		try {
			query.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private ResultSet executeStatement(String query) {
		try {
			return getStatement().executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private PreparedStatement getPreparedStatement(String query) {
		try {
			return connection.prepareStatement(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Statement getStatement() {
		try {
			return connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}