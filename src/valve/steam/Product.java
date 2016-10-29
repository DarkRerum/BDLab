package valve.steam;

import oracle.jdbc.OracleTypes;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nikita on 28.10.2016.
 */
public class Product {
    private long m_id;
    private String m_name;
    private Blob m_image;
    private Product m_parentProduct;

    public Product(String name) throws SQLException {
        String query = "BEGIN INSERT INTO products (name) VALUES(?) RETURNING ID INTO ?; END;";
        //PreparedStatement preparedStatement = Steam.getInstance().getConnection().prepareStatement(query);

        CallableStatement cs = Steam.getInstance().getConnection().prepareCall(query);
        cs.setString(1, name);
        cs.registerOutParameter(2, OracleTypes.NUMBER);
        cs.execute();
        m_id = cs.getLong(2);
    }

    public Product(String name, Product parentProduct) throws  SQLException {
        String query = "BEGIN INSERT INTO products (name, parent_product_id) VALUES(?,?) RETURNING ID INTO ?; END;";
        //PreparedStatement preparedStatement = Steam.getInstance().getConnection().prepareStatement(query);

        CallableStatement cs = Steam.getInstance().getConnection().prepareCall(query);
        cs.setString(1, name);
        cs.setLong(2, parentProduct.getId());
        cs.registerOutParameter(3, OracleTypes.NUMBER);
        cs.execute();
        m_id = cs.getLong(3);
        m_parentProduct = parentProduct;
    }

    private Product(long id, String name, Blob image, Product parentProduct) throws  SQLException {
        m_id = id;
        m_name = name;
        m_image = image;
        m_parentProduct = parentProduct;
    }

    public long getId() {
        return m_id;
    }

    public String getName() {
        return m_name;
    }
	

    public static Product getFromName(String name) throws  SQLException, NotImplementedException {
        String query = "SELECT * FROM products WHERE name = ?";

        PreparedStatement ps = Steam.getInstance().getConnection().prepareStatement(query);
		ps.setString(1, name);
		ResultSet queryResult = ps.executeQuery();
		queryResult.next();
        long parentID = queryResult.getLong(4);

        if (parentID == 0) {
            return new Product(queryResult.getLong(1), queryResult.getString(2),
                    queryResult.getBlob(3), null);
        }
        else {
            throw new NotImplementedException();
        }
    }

    public static Product getFromId(Long id) throws  SQLException, NotImplementedException {
        String query = "SELECT * FROM products WHERE id = ?";

        PreparedStatement ps = Steam.getInstance().getConnection().prepareStatement(query);
        ps.setLong(1, id);
        ResultSet queryResult = ps.executeQuery();
        queryResult.next();
        long parentID = queryResult.getLong(4);

        if (parentID == 0) {
            return new Product(queryResult.getLong(1), queryResult.getString(2),
                    queryResult.getBlob(3), null);
        }
        else {
            throw new NotImplementedException();
        }
    }
	

    public Price getPrice(Currency currency) throws SQLException {
        //String query = "? := PKG1.GET_PRICE(?, ?)";
        String query = "{ ? = call PKG1.GET_PRICE(?, ?) }";
        CallableStatement cs = Steam.getInstance().getConnection().prepareCall(query);

        cs.registerOutParameter(1, OracleTypes.NUMBER);
        cs.setString(2, m_name);
        cs.setString(3, currency.toString());

        cs.execute();
        //System.out.println(cs.getInt(2));
        float price = cs.getFloat(1);
        //Steam.getInstance().getConnection().commit();

        return new Price(currency, price);
    }

    public void addPrice(Price price) throws SQLException {
		String query = "{ ? = call STEAM.SET_PRICE(?, ?, ?, ?) }";
		CallableStatement cs = Steam.getInstance().getConnection().prepareCall(query);

		cs.registerOutParameter(1, OracleTypes.NUMBER);
		cs.setString(2, m_name);
		cs.setFloat(3, price.getValue());
		cs.setString(4, price.getCurrency().toString());
		cs.setNull(5, Types.NULL);

		cs.execute();
    }

    public void removePrice(Currency currency) throws SQLException {
		String query = "{ ? = call STEAM.REMOVE_PRICE(?, ?) }";
		CallableStatement cs = Steam.getInstance().getConnection().prepareCall(query);

		cs.registerOutParameter(1, OracleTypes.NUMBER);
		cs.setString(2, m_name);
		cs.setString(3, currency.toString());
		/*cs.setString(4, price.getCurrency().toString());
		cs.setNull(5, Types.NULL);*/

		cs.execute();
	}

	public List<Achievement> getAchievements() throws SQLException{
		List<Achievement> achievements = new ArrayList<Achievement>();
		String query = "SELECT id FROM achievements WHERE product_id = ?";
		PreparedStatement ps = Steam.getInstance().getConnection().prepareStatement(query);

		ps.setLong(1, m_id);
		ResultSet queryResult = ps.executeQuery();

		while (queryResult.next()) {
			achievements.add(Achievement.getFromId(queryResult.getLong(1)));
		}
		return  achievements;
	}

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Product))
            return false;
        if (obj == this)
            return true;

        Product rhs = (Product) obj;
        if (this.m_id != rhs.m_id) {
            return false;
        }
        /*if (!this.m_name.equals(rhs.m_name)) {
            return false;
        }*/
        return true;
    }
}