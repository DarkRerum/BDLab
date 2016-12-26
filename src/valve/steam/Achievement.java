package valve.steam;

import oracle.jdbc.OracleTypes;

import java.sql.*;
import java.util.List;

public class Achievement {
	private long m_id;
    private Product m_product;
	private Blob m_icon;
	private static int notFoundExceptionErrorCode = 17011;

	public Achievement(Product product) throws SQLException {
		String query = "BEGIN INSERT INTO achievements (product_id) VALUES(?) RETURNING ID INTO ?; END;";
		CallableStatement cs = Steam.getInstance().getConnection().prepareCall(query);
		cs.setLong(1, product.getId());
		cs.registerOutParameter(2, OracleTypes.NUMBER);
		cs.execute();

		m_id = cs.getLong(2);
		m_product = product;
	}

	public long getId() {
		return m_id;
	}

	public Product getProduct() {
		return m_product;
	}

	public String getName(Language language) throws SQLException{
		String name;

		if (JedisInst.getInstance().getJedis().exists("Achievement_" + m_id + "_inLang_" + language.getName())) {
			name = JedisInst.getInstance().getJedis().get("Achievement_" + m_id + "_inLang_" + language.getName());
			Debug.log("Achievement name " + m_id + " pulled from cache");
		}
		else {
			Debug.log("Cache miss on achievement name " + m_id );

			String query = "SELECT ach_name FROM achievement_names WHERE achievement_id = ? AND " +
					"lang_id = ?";
			PreparedStatement ps = Steam.getInstance().getConnection().prepareStatement(query);

			ps.setLong(1, m_id);
			ps.setLong(2, language.getId());
			ResultSet queryResult = ps.executeQuery();
			queryResult.next();

			try {
				name = queryResult.getString(1);
			}
			catch (SQLException sqlEx) {
				if (sqlEx.getErrorCode() == notFoundExceptionErrorCode) {
					return null;
				}
				else throw sqlEx;
			}

			JedisInst.getInstance().getJedis().set("Achievement_" + m_id
					+ "_inLang_" + language.getName(), "" + name);
			Debug.log("Achievement name " + name + " cached");
		}

		return name;
	}

	public String getDescription(Language language, boolean isLocked) throws SQLException{
		String query = "SELECT description FROM achievement_descriptions WHERE achievement_id = ? AND " +
				"lang_id = ? AND is_locked = ?";
		PreparedStatement ps = Steam.getInstance().getConnection().prepareStatement(query);
		ps.setLong(1, m_id);
		ps.setLong(2, language.getId());

		if (isLocked) {
			ps.setLong(3, 1);
		}
		else {
			ps.setLong(3, 0);
		}

		ResultSet queryResult = ps.executeQuery();
		queryResult.next();

		String description;
		try {
			description = queryResult.getString(1);
		}
		catch (SQLException sqlEx) {
			if (sqlEx.getErrorCode() == notFoundExceptionErrorCode) {
				return null;
			}
			else throw sqlEx;
		}

		return description;
	}

	public long addDescription(Language language, String description, boolean isLocked) throws SQLException {
		if (getDescription(language, isLocked) != null) {
			throw new SQLException("Description in this language already exists");
		}
		String query = "BEGIN INSERT INTO achievement_descriptions (description, lang_id, achievement_id, is_locked) VALUES(?,?,?,?) RETURNING ID INTO ?; END;";
		CallableStatement cs = Steam.getInstance().getConnection().prepareCall(query);

		cs.setString(1, description);
		cs.setLong(2, language.getId());
		cs.setLong(3, m_id);
		if (isLocked) {
			cs.setLong(4, 1);
		}
		else {
			cs.setLong(4, 0);
		}

		cs.registerOutParameter(5, OracleTypes.NUMBER);
		cs.execute();
		return cs.getLong(5);
	}

	public long addName(Language language, String name) throws SQLException {
		if (getName(language) != null) {
			throw new SQLException("Name in this language already exists");
		}

		String query = "BEGIN INSERT INTO achievement_names (ach_name, lang_id, achievement_id) VALUES(?,?,?) RETURNING ID INTO ?; END;";
		CallableStatement cs = Steam.getInstance().getConnection().prepareCall(query);

		cs.setString(1, name);
		cs.setLong(2, language.getId());
		cs.setLong(3, m_id);

		cs.registerOutParameter(4, OracleTypes.NUMBER);
		cs.execute();
		return cs.getLong(4);
	}

	public static Achievement getFromName(Product product, String name) throws SQLException {
		long id;
		if (JedisInst.getInstance().getJedis().exists("Achievement_From_Product_"
				+ product.getName() + "_ByName_" + name + "_id")) {
			id = Long.parseLong(JedisInst.getInstance().getJedis().get("Achievement_From_Product_"
					+ product.getName() + "_ByName_" + name + "_id"));
			Debug.log("Achievement " + name + " pulled from cache");
		}
		else {
			Debug.log("Cache miss on achievement " + name);

			String query = "SELECT id FROM achievements WHERE product_id = ? AND " +
					"id IN (SELECT achievement_id FROM achievement_names WHERE ach_name = ?)";
			PreparedStatement ps = Steam.getInstance().getConnection().prepareStatement(query);

			ps.setLong(1, product.getId());
			ps.setString(2, name);
			ResultSet queryResult = ps.executeQuery();
			queryResult.next();

			try {
				id = queryResult.getLong(1);
			} catch (SQLException sqlEx) {
				throw new SQLException("There is not such achievement");
			}

			JedisInst.getInstance().getJedis().set("Achievement_From_Product_"
					+ product.getName() + "_ByName_" + name + "_id", "" + id);
			Debug.log("Achievement by name " + name + " cached");
		}

		return new Achievement(id);
	}

	public static Achievement getFromId(long id) throws SQLException{
		long returnId = 0;

		String query = "SELECT * FROM achievements WHERE id = ?";
		PreparedStatement ps = Steam.getInstance().getConnection().prepareStatement(query);
		ps.setLong(1, id);
		ResultSet queryResult = ps.executeQuery();
		queryResult.next();


		try {
			returnId = queryResult.getLong(1);
		} catch (SQLException sqlEx) {
			throw new SQLException("There is not such achievement");
		}


		return new Achievement(returnId);
	}

	public Achievement(long id) throws SQLException{
		m_id = id;
		loadDataFromDB();
	}

	private void loadDataFromDB() throws SQLException{
		String query = "SELECT product_id FROM achievements WHERE id=?";
		PreparedStatement ps = Steam.getInstance().getConnection().prepareStatement(query);
		ps.setLong(1, m_id);
		ResultSet queryResult = ps.executeQuery();
		queryResult.next();
		m_product = Product.getFromId(queryResult.getLong(1));
	}
}