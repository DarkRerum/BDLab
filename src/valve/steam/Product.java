package valve.steam;

import oracle.jdbc.OracleTypes;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.*;

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


}
