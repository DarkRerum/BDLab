package valve.steam;

import redis.clients.jedis.Jedis;

/**
 * Created by 123 on 26.12.2016.
 */
public class JedisInst {
	private static volatile JedisInst m_instance;
	private Jedis jedisInstance = null;

	public static JedisInst getInstance() {
		JedisInst localInstance = m_instance;
		if (localInstance == null) {
			synchronized (JedisInst.class) {
				localInstance = m_instance;
				if (localInstance == null) {
					m_instance = localInstance = new JedisInst();
				}
			}
		}
		return localInstance;
	}

	private JedisInst() {}

	public synchronized void setJedisAddress(String address) {
		if (jedisInstance != null) {
			jedisInstance.close();
		}
		jedisInstance = new Jedis(address);
	}

	public synchronized void setJedisAddress(String address, int port) {
		if (jedisInstance != null) {
			jedisInstance.close();
		}
		jedisInstance = new Jedis(address, port);
	}

	public Jedis getJedis() {
		return jedisInstance;
	}
}
