package valve.steam;

import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import oracle.jdbc.OracleTypes;

public class Account {
	private long m_id;
	private String m_accountName;
	private String m_userName;
	private String m_email;
	private Blob m_avatar;
	private long m_phoneNumber;
	private Language m_language;
	
	public Account(String accountName, String userName, String email, Language language) throws SQLException {
		String query = "BEGIN INSERT INTO accounts (name, username, email, lang_id) VALUES(?,?,?,?) RETURNING ID INTO ?; END;";
		//PreparedStatement preparedStatement = Steam.getInstance().getConnection().prepareStatement(query);
					
		CallableStatement cs = Steam.getInstance().getConnection().prepareCall(query);
		cs.setString(1, accountName);
		cs.setString(2, userName);
		cs.setString(3, email);
		cs.setLong(4, language.getId());
		cs.registerOutParameter(5, OracleTypes.NUMBER);
		cs.execute();
		//System.out.println(cs.getInt(2));
		m_id = cs.getLong(5);		
		//Steam.getInstance().getConnection().commit();
		
		m_accountName = accountName;
		m_userName = userName;
		m_email = email;
		m_phoneNumber = 0;
		m_language = language;
	}
	
	private Account(long id, String accountName, String userName, String email, Blob avatar, long phoneNumber, Language language) {
		m_id = id;
		m_accountName = accountName;
		m_userName = userName;
		m_email = email;
		m_avatar = avatar;
		m_phoneNumber = phoneNumber;
		m_language = language;
	}

	private Account (long id) throws SQLException {
		m_id = id;
		loadDataFromDB();
	}
	
	public long getId() throws SQLException {
		loadDataFromDB();
		return m_id;
	}
	
	public String getName()throws SQLException {
		loadDataFromDB();
		return m_accountName;
	}
	
	public String getUserName() throws SQLException {
		loadDataFromDB();
		return m_userName;
	}

	public void setUserName(String newName) throws SQLException {
		String query = "UPDATE accounts SET username = ? WHERE id = ?";

		PreparedStatement ps = Steam.getInstance().getConnection().prepareStatement(query);
		ps.setString(1, newName);
		ps.setLong(2, m_id);
		ps.execute();
		//Steam.getInstance().getConnection().commit();
	}
	
	public String getEmail() throws SQLException{
		loadDataFromDB();
		return m_email;
	}
	
	public long getPhoneNumber()throws SQLException {
		loadDataFromDB();
		return m_phoneNumber;
	}
	
	public Language getLanguage() throws SQLException {
		loadDataFromDB();
		return m_language;
	}
	
	public static Account getFromName(String name) throws SQLException {
		String query = "SELECT id FROM accounts WHERE name = ?";
		
		PreparedStatement ps = Steam.getInstance().getConnection().prepareStatement(query);
		ps.setString(1, name);
		ResultSet queryResult = ps.executeQuery();
		queryResult.next();
		long id = queryResult.getLong(1);
		
		return new Account(id);
	}

	private void loadDataFromDB() throws SQLException {
		String query = "SELECT * FROM accounts WHERE id=?";

		PreparedStatement ps = Steam.getInstance().getConnection().prepareStatement(query);
		ps.setLong(1, m_id);
		ResultSet queryResult = ps.executeQuery();
		queryResult.next();
		//long id = queryResult.getLong(1);
		m_accountName = queryResult.getString(2);
		m_userName = queryResult.getString(3);
		m_email = queryResult.getString(4);
		m_avatar = queryResult.getBlob(5);
		m_phoneNumber = queryResult.getLong(6);
		long langId = queryResult.getLong(7);

		m_language = Language.getFromId(langId);
	}

	public List<Product> getOwnedProducts() throws SQLException {
		String query = "SELECT product_id FROM owned_products WHERE account_id = ?";

		PreparedStatement ps = Steam.getInstance().getConnection().prepareStatement(query);
		ps.setLong(1, m_id);
		ResultSet queryResult = ps.executeQuery();
		List<Product> productList = new ArrayList<>();

		while (queryResult.next()) {
			productList.add(Product.getFromId(queryResult.getLong(1)));
		}

		return productList;
	}
}