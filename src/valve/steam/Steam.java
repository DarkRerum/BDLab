package valve.steam;

import java.sql.*;
import java.io.*;
import oracle.jdbc.driver.OracleDriver;

public class Steam {

	private static volatile Steam m_instance;

	private Connection m_connection = null;
	
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
		//System.out.println("AutoCommit: " + m_connection.getAutoCommit());
	}	
	
	public Connection getConnection() {
		return m_connection;
	}	
}