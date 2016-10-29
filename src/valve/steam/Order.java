package valve.steam;

import oracle.jdbc.OracleTypes;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Order {
	private long m_id;
	private Date m_purchaseDate;
	private Account m_account;

	public Order(Account account) throws SQLException {
		String query = "BEGIN INSERT INTO purchase_orders (purchase_date, account_id) VALUES(?,?) RETURNING ID INTO ?; END;";
		CallableStatement cs = Steam.getInstance().getConnection().prepareCall(query);
		cs.setString(1, null);
		cs.setLong(2, account.getId());
		cs.registerOutParameter(3, OracleTypes.NUMBER);
		cs.execute();

		m_id = cs.getLong(3);
		m_account = account;
		m_purchaseDate = null;
	}



	public Date getPurchaseDate() {
		return m_purchaseDate;
	}

	public Account getAccount() {
		return m_account;
	}

    public Map<Long, Product> getProductsInOrder() throws SQLException{
        String query = "SELECT iio.id, iio.item.product_id FROM items_in_orders iio WHERE order_id = ?";

        PreparedStatement ps = Steam.getInstance().getConnection().prepareStatement(query);
        ps.setLong(1, m_id);
        ResultSet queryResult = ps.executeQuery();
        Map<Long, Product> productMap = new TreeMap<Long, Product>();

        while (queryResult.next()) {
            productMap.put(queryResult.getLong(1),Product.getFromId(queryResult.getLong(2)));
        }
        return productMap;
    }

	public long addItemInOrder(Product product, Currency currency) throws SQLException {
		if (m_purchaseDate != null) {
			throw new SQLException("Order is closed");
		}
		if (m_account.getOwnedProducts().contains(product)) {
			throw new SQLException("User already has this product");
		}
        if (getProductsInOrder().values().contains(product)) {
            throw new SQLException("Order already contains this product");
        }

		Price price = product.getPrice(currency);

        String query = "BEGIN INSERT INTO items_in_orders (order_id, item) VALUES(?,purchase_item(?,?,?)) RETURNING ID INTO ?; END;";
        CallableStatement cs = Steam.getInstance().getConnection().prepareCall(query);
        cs.setLong(1, m_id);
        cs.setLong(2, product.getId());
        cs.setLong(3, currency.getId());
        cs.setFloat(4, price.getValue());

        cs.registerOutParameter(5, OracleTypes.NUMBER);
        cs.execute();
        return  cs.getLong(5);
	}

    public void removeItemFromOrder(Product product) throws SQLException {
        if (m_purchaseDate != null) {
            throw new SQLException("Order is closed");
        }
        Map<Long, Product> productsMap = getProductsInOrder();
        long deleteId = 0;
        boolean isProductInOrder = false;
        for (Map.Entry<Long, Product> entry : productsMap.entrySet()) {
            if (entry.getValue().equals(product)) {
                isProductInOrder = true;
                deleteId = entry.getKey();
                break;
            }
        }
        if (!isProductInOrder) {
            throw new SQLException("Order doesn't contain this product");
        }
        String query = "DELETE FROM items_in_orders WHERE id = ?";
        CallableStatement cs = Steam.getInstance().getConnection().prepareCall(query);
        cs.setLong(1, deleteId);
        cs.execute();
    }

    public long closeOrder() throws SQLException{
        String query = "{ ? = call STEAM.CLOSE_ORDER(?) }";
        CallableStatement cs = Steam.getInstance().getConnection().prepareCall(query);
        cs.registerOutParameter(1, OracleTypes.NUMBER);
        cs.setLong(2, m_id);

        cs.execute();
        return cs.getLong(1);
    }

	public static Order getFromId(long id) throws SQLException{
		String query = "SELECT * FROM purchase_orders WHERE id = ?";
		PreparedStatement ps = Steam.getInstance().getConnection().prepareStatement(query);
		ps.setLong(1, id);
		ResultSet queryResult = ps.executeQuery();
		queryResult.next();
		return new Order(id);
	}

	private Order(long id) throws SQLException {
		m_id = id;
		loadDataFromDB();
	}

	private void loadDataFromDB() throws SQLException{
		String query = "SELECT id, account_id, purchase_date FROM purchase_orders WHERE id=?";

		PreparedStatement ps = Steam.getInstance().getConnection().prepareStatement(query);
		ps.setLong(1, m_id);
		ResultSet queryResult = ps.executeQuery();
		queryResult.next();

		m_purchaseDate = queryResult.getDate(2);
		long accountId = queryResult.getLong(3);

		m_account = Account.getFromId(accountId);
	}
}
