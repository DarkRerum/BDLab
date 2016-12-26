package valve.steam;

import redis.clients.jedis.Jedis;

/**
 * Created by 123 on 26.12.2016.
 */
public class JedisInst {
	private static Jedis jedisInstance = new Jedis();

	public static Jedis getInstance() {
		return jedisInstance;
	}

	public synchronized void setAddress(String address) {
		jedisInstance = new Jedis(address);
	}

	private JedisInst() {}
}
