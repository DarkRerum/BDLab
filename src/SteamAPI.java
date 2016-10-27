import java.sql.*;
import java.sql.ResultSet;
import java.io.*;
import oracle.jdbc.driver.OracleDriver;

public class SteamAPI {
	private Connection connection = null;
	private Statement statement = null;
	private String dbUser = null;
	private String dbPass = null;
	
	
	public SteamAPI(String dbAddress, String loginFilePath) throws IOException,
			ClassNotFoundException, SQLException  {
				
		initializeLoginInfo(loginFilePath);
		if (dbUser == null || dbPass == null) {
			throw new IOException("File reading failed!");
		}
		
		Class.forName("oracle.jdbc.driver.OracleDriver");
		
		connection = DriverManager.getConnection(dbAddress, dbUser, dbPass);
		if (connection == null) {
			throw new SQLException("Failed to make connection!");
		}
		statement = connection.createStatement();
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
	
	
	private void initializeLoginInfo (String loginFilePath) {
		File loginFile = null;
		BufferedReader br = null;
		try {
			loginFile = new File(loginFilePath);
			br = new BufferedReader(new FileReader(loginFile));
			try {
				String line;
				if ((line = br.readLine()) != null) {
					dbUser = line;
				}
				if ((line = br.readLine()) != null) {
					dbPass = line;
				}
			}
			finally {
				br.close();
			}
		}
		catch (IOException e) {
			return;
		}
	}
}
	