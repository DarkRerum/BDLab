package valve.steam;

/**
 * Created by Nikita on 28.10.2016.
 */
public class Price {
	private Currency m_currency;
	private float m_value;

	public Price(Currency currency, float value) {
		m_currency = currency;
		m_value = value;
	}
    public float getValue() {
        return m_value;
    }
	public String toString() {
		return m_currency.toString() + " " + m_value;
	}
}
