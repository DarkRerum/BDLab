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
		long parentID;
		long id;
		Blob blob = null;

        if (JedisInst.getInstance().getJedis().exists("ProductByName_" + name + "_id")) {
			id = Long.parseLong(JedisInst.getInstance().getJedis().get("ProductByName_" + name + "_id"));
			parentID = Long.parseLong(JedisInst.getInstance().getJedis().get("ProductByName_" + name + "_parent"));
			Debug.log("Product " + name + " pulled from cache");
		}
		else {
			Debug.log("Cache miss on product " + name);

			String query = "SELECT * FROM products WHERE name = ?";

			PreparedStatement ps = Steam.getInstance().getConnection().prepareStatement(query);
			ps.setString(1, name);
			ResultSet queryResult = ps.executeQuery();
			queryResult.next();
			id = queryResult.getLong(1);
			blob = queryResult.getBlob(3);
			parentID = queryResult.getLong(4);

			JedisInst.getInstance().getJedis().set("ProductByName_" + name + "_id", "" + id);
			JedisInst.getInstance().getJedis().set("ProductByName_" + name + "_parent", "" + parentID);

			Debug.log("Product by name " + name + " cached");
		}


        if (parentID == 0) {
            return new Product(id, name, blob, null);
        }
        else {
            throw new NotImplementedException();
        }
    }

    public static Product getFromId(Long id) throws  SQLException, NotImplementedException {
		long parentID;
		String name;
		Blob blob = null;

		if (JedisInst.getInstance().getJedis().exists("ProductById_" + id + "_name")) {
			name = JedisInst.getInstance().getJedis().get("ProductById_" + id + "_name");
			parentID = Long.parseLong(JedisInst.getInstance().getJedis().get("ProductById_" + id + "_parent"));
			Debug.log("Product " + id + " pulled from cache");
		}
		else {
			Debug.log("Cache miss on product " + id);

			String query = "SELECT * FROM products WHERE id = ?";

			PreparedStatement ps = Steam.getInstance().getConnection().prepareStatement(query);
			ps.setLong(1, id);
			ResultSet queryResult = ps.executeQuery();
			queryResult.next();

			name = queryResult.getString(2);
			blob = queryResult.getBlob(3);
			parentID = queryResult.getLong(4);

			JedisInst.getInstance().getJedis().set("ProductById_" + id + "_name", "" + name);
			JedisInst.getInstance().getJedis().set("ProductById_" + id + "_parent", "" + parentID);

			Debug.log("Product by id " + id + " cached");
		}

        if (parentID == 0) {
            return new Product(id, name, blob, null);
        }
        else {
            throw new NotImplementedException();
        }
    }
	

    public Price getPrice(Currency currency) throws SQLException {
		float price;
		if (JedisInst.getInstance().getJedis().exists("Product_"
				+ m_id + "_price_in_" + currency.toString())) {
			price = Float.parseFloat(JedisInst.getInstance().getJedis()
					.get("Product_" + m_id + "_price_in_" + currency.toString()));

			Debug.log("Product " + m_name + " price in " + currency.toString() + " pulled from cache");
		}
		else {
			Debug.log("Cache miss on product " + m_name + " price in " + currency.toString());
			//String query = "? := PKG1.GET_PRICE(?, ?)";
			String query = "{ ? = call PKG1.GET_PRICE(?, ?) }";
			CallableStatement cs = Steam.getInstance().getConnection().prepareCall(query);

			cs.registerOutParameter(1, OracleTypes.NUMBER);
			cs.setString(2, m_name);
			cs.setString(3, currency.toString());

			cs.execute();
			//System.out.println(cs.getInt(2));
			price = cs.getFloat(1);

			JedisInst.getInstance().getJedis().set("Product_"
					+ m_id + "_price_in_" + currency.toString(), "" + price);
			Debug.log("Product " + m_name + " price in " + currency.toString() + " cached");
			//Steam.getInstance().getConnection().commit();
		}

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

		JedisInst.getInstance().getJedis().del("Product_"
				+ m_id + "_price_in_" + price.getCurrency().toString());
		Debug.log("Product " + m_name + " price in " + price.getCurrency().toString() + " cache dropped");
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

		JedisInst.getInstance().getJedis().del("Product_"
				+ m_id + "_price_in_" + currency.toString());
		Debug.log("Product " + m_name + " price in " + currency.toString() + " cache dropped");
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