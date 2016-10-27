package valve.steam;

import java.sql.*;
import java.io.*;
import oracle.jdbc.driver.OracleDriver;

public class Steam {

	private static volatile Steam m_instance;

	private Connection m_connection = null;
	private Statement statement = null;
	private String dbUser = null;
	private String dbPass = null;	
	
	// Singletron implementation via Double Checked Locking & volatile method
	public static Steam getInstance() {
		Steam localInstance = m_instance;
		if (localInstance == null) {
			synchronized (Steam.class) {
				localInstance = m_instance;
				if (localInstance == null) {
					m_instance = localInstance = new Steam();
				}
			}
		}
		return localInstance;
	}
	
	private Steam() {}
	
	public synchronized void resetConnection(String dbAddress, String username, String password) throws IOException,
			ClassNotFoundException, SQLException {
		
		
		//Class.forName("oracle.jdbc.driver.OracleDriver");
		
		if (m_connection != null && !m_connection.isClosed()) {
			m_connection.close();
		}
		
		m_connection = DriverManager.getConnection(dbAddress, username, password);
		if (m_connection == null) {
			throw new SQLException("Failed to make connection!");
		}
		statement = m_connection.createStatement();
		if (statement == null) {
			throw new SQLException("Failed to make statement!");
		}
	}	
	
	public Connection getConnection() {
		return m_connection;
	}
	/*public long createAccount(String name, String username, String email) {
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
	}*/
		
}