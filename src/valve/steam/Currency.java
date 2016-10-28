package valve.steam;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Nikita on 28.10.2016.
 */
public class Currency {
    private long m_id;
    private String m_name;
    private float m_usdMult;

    private Currency(long id, String name, float usdMult) {
        m_id = id;
        m_name = name;
        m_usdMult = usdMult;
    };

    public static Currency getFromName(String name) throws SQLException {
        String query = "SELECT * FROM currencies WHERE name = ?";

        PreparedStatement ps = Steam.getInstance().getConnection().prepareStatement(query);
        ps.setString(1, name);
        ResultSet queryResult = ps.executeQuery();
        queryResult.next();

        return new Currency(queryResult.getLong(1), queryResult.getString(2), queryResult.getFloat(3));

    }

    public String toString() {
        return m_name;
    }
}
