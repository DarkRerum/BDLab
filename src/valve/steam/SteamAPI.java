package valve.steam;

import java.sql.*;
import java.io.*;
import oracle.jdbc.driver.OracleDriver;

public class SteamAPI {
	private Connection m_connection = null;
	private Statement statement = null;
	private String dbUser = null;
	private String dbPass = null;	
	
	public SteamAPI(String dbAddress, String username, String password) throws IOException,
			ClassNotFoundException, SQLException  {
		
		Class.forName("oracle.jdbc.driver.OracleDriver");
		
		m_connection = DriverManager.getConnection(dbAddress, username, password);
		if (m_connection == null) {
			throw new SQLException("Failed to make connection!");
		}
		statement = m_connection.createStatement();
		if (statement == null) {
			throw new SQLException("Failed to make statement!");
		}
	}	
	
	public long createAccount(String name, String username, String email) {
		String sql = "INSERT INTO accounts(name, username, email, lang_id) " +
                   "VALUES ('" + name + "','" + username + "','" + email + "',1)";
		try { 
			statement.executeUpdate(sql);
		}
		catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			return 0;
		}
		return 0;
	}
		
}