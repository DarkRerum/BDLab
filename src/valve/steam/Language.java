package valve.steam;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import oracle.jdbc.OracleTypes;

public class Language {
	private long m_id;
	private String m_languageName;
	
	public Language(String name) throws SQLException {
		String query = "BEGIN INSERT INTO languages (name) VALUES(?) RETURNING ID INTO ?; END;";
		//PreparedStatement preparedStatement = Steam.getInstance().getConnection().prepareStatement(query);
					
		CallableStatement cs = Steam.getInstance().getConnection().prepareCall(query);
		cs.setString(1, name);
		cs.registerOutParameter(2, OracleTypes.NUMBER);
		cs.execute();
		//System.out.println(cs.getInt(2));
		m_id = cs.getLong(2);
		m_languageName = name;
		Steam.getInstance().getConnection().commit();
	}
	
	private Language(long id, String languageName) {
		m_id = id;
		m_languageName = languageName;
	}
	
	public long getId() {
		return m_id;
	}
	
	public String getName() {
		return m_languageName;
	}
	
	public static Language getFromName(String name) throws SQLException {
		String query = "SELECT id FROM languages WHERE name=?";	
		
		PreparedStatement ps = Steam.getInstance().getConnection().prepareStatement(query);
		ps.setString(1, name);
		ResultSet queryResult = ps.executeQuery();
		queryResult.next();
		long id = queryResult.getLong(1);
		
		Language l = new Language(id, name);
		return l;
	}
	
	public static Language getFromId(long id) throws SQLException {
		String query = "SELECT name FROM languages WHERE id=?";	
		
		PreparedStatement ps = Steam.getInstance().getConnection().prepareStatement(query);
		ps.setLong(1, id);
		ResultSet queryResult = ps.executeQuery();
		queryResult.next();
		String name = queryResult.getString(1);
		
		Language l = new Language(id, name);
		return l;
	}
	
}