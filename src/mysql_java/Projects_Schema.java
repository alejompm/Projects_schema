package mysql_java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Projects_Schema {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
String db="jdbc:mysql://localhost:3306/projects_schema";
		
		try {
			Connection conn = DriverManager.getConnection(db, "root", "890310aA");
			System.out.println("Connected");
		}
		catch(SQLException e ) {
			System.out.println("Not connected");
			e.printStackTrace();
		}
	}

}
