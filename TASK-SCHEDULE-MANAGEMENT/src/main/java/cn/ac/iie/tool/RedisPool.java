package cn.ac.iie.tool;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.exceptions.JedisException;

import java.util.Set;

/**redis连接池**/
public class RedisPool {
	private JedisSentinelPool jedisSentinelPool = null;
	private Set<String> sentinels = null;
	private String masterName = null;

	private JedisPool jedisPool = null;
	private String hostName;
	private int port;

	private String password;
	private int timeOut = 30 * 1000;
	private RedisType redisType = RedisType.STANDALONE;

	enum RedisType{
		SENTINEL,CLUSTER,STANDALONE
	}

	public RedisPool(String hostName, int port) {
		this.hostName = hostName;
		this.port = port;
		this.redisType = RedisType.STANDALONE;
	}
	public RedisPool(String hostName, int port, String password) {
		this.hostName = hostName;
		this.port = port;
		this.password = password;
		this.redisType = RedisType.STANDALONE;
	}
	public RedisPool(Set<String> sentinels, String masterName) {
		this.sentinels = sentinels;
		this.masterName = masterName;
		this.redisType = RedisType.SENTINEL;
	}
	public RedisPool(Set<String> sentinels, String masterName, String password) {
		this.sentinels = sentinels;
		this.masterName = masterName;
		this.password = password;
		this.redisType = RedisType.SENTINEL;
	}

	public void quit() {
		if (jedisSentinelPool != null)
			jedisSentinelPool.destroy();
	}

	public static Jedis getRawInstance(String host, int port) {
		return new Jedis(host, port);
	}

	public Jedis getResource() throws JedisException {
		switch (redisType) {
		case STANDALONE:Jedis s = new Jedis(hostName, port);
			if (jedisPool != null)
				return jedisPool.getResource();
			else {
				synchronized (this){
					JedisPoolConfig c = new JedisPoolConfig();
					c.setMaxTotal(20);
					c.setMaxIdle(20);
					jedisPool = new JedisPool(c, hostName, port, timeOut);
					System.out.println("New standalone pool @ " + hostName +
							":" + port);
					return jedisPool.getResource();
				}
			}
		case SENTINEL: {
			if (jedisSentinelPool != null)
				return jedisSentinelPool.getResource();
			else {
				synchronized (this){
					if (jedisSentinelPool == null){
						JedisPoolConfig c = new JedisPoolConfig();
						jedisSentinelPool = new JedisSentinelPool(masterName, sentinels, c,
								timeOut);
						System.out.println("New sentinel pool @ " + masterName);
					}
					return jedisSentinelPool.getResource();
				}
			}
		}
		case CLUSTER: {
		}
		}
		return null;
	}

}
