package valve.steam;

import java.sql.CallableStatement;
import oracle.jdbc.OracleTypes;
import java.sql.SQLException;

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
	
	public long getId() {
		return m_id;
	}
	
	public String getLanguageName() {
		return m_languageName;
	}
	
	/*public static Language getLanguageFromName(String name) {
		PreparedStatement preparedStatement = Steam.getInstance().getConnection().prepareStatement(query);
		
		
	}*/
}