package ca.concordia.javascript.experiment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresOutput {
	public PostgresOutput() {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/jsdeodorant", "", "");
		} catch (SQLException e) {

			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;

		}
	}
}
