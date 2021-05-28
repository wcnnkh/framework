package scw.redis.jedis.connection;

import java.util.Collection;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.params.MigrateParams;
import redis.clients.jedis.params.RestoreParams;
import redis.clients.jedis.util.SafeEncoder;
import scw.redis.connection.Cursor;
import scw.redis.connection.DataType;
import scw.redis.connection.RedisAuth;
import scw.redis.connection.RedisKeysCommands;
import scw.redis.connection.RedisValueEncoding;
import scw.redis.connection.RedisValueEncodings;
import scw.redis.connection.ScanCursor;
import scw.redis.connection.ScanIteration;
import scw.redis.connection.ScanOptions;

public class JedisKeysCommands implements RedisKeysCommands<byte[], byte[]> {
	private final Jedis jedis;

	public JedisKeysCommands(Jedis jedis) {
		this.jedis = jedis;
	}

	@Override
	public Boolean copy(byte[] source, byte[] destination, Integer destinationDB, boolean replace) {
		if (destinationDB == null) {
			return jedis.copy(source, destination, replace);
		} else {
			return jedis.copy(source, destination, destinationDB, replace);
		}
	}

	@Override
	public Long del(byte[]... keys) {
		return jedis.del(keys);
	}

	@Override
	public byte[] dump(byte[] key) {
		return jedis.dump(key);
	}

	@Override
	public Long exists(byte[]... keys) {
		return jedis.exists(keys);
	}

	@Override
	public Long expire(byte[] key, long seconds) {
		return jedis.expire(key, seconds);
	}

	@Override
	public Long expireAt(byte[] key, long timestamp) {
		return jedis.expireAt(key, timestamp);
	}

	@Override
	public Collection<byte[]> keys(byte[] pattern) {
		return jedis.keys(pattern);
	}

	@Override
	public String migrate(String host, int port, byte[] key, int targetDB, int timeout) {
		return jedis.migrate(host, port, key, targetDB, timeout);
	}

	@Override
	public String migrate(String host, int port, int targetDB, int timeout, boolean copy, boolean replace,
			RedisAuth auth, byte[]... keys) {
		MigrateParams params = new MigrateParams();
		if (copy) {
			params.copy();
		}

		if (replace) {
			params.replace();
		}

		if (auth != null) {
			if (auth.getUsername() != null) {
				params.auth2(auth.getUsername(), auth.getPassword());
			} else {
				params.auth(auth.getPassword());
			}
		}
		return jedis.migrate(host, port, targetDB, timeout, params, keys);
	}

	@Override
	public Long move(byte[] key, int targetDB) {
		return jedis.move(key, targetDB);
	}

	@Override
	public Long objectRefCount(byte[] key) {
		return jedis.objectRefcount(key);
	}

	@Override
	public RedisValueEncoding objectEncoding(byte[] key) {
		byte[] value = jedis.objectEncoding(key);
		RedisValueEncoding encoding = RedisValueEncoding.of(SafeEncoder.encode(value));
		return encoding == null ? RedisValueEncodings.VACANT : encoding;
	}

	@Override
	public Long objectIdletime(byte[] key) {
		return jedis.objectIdletime(key);
	}

	@Override
	public Long objectFreq(byte[] key) {
		return jedis.objectFreq(key);
	}

	@Override
	public Long persist(byte[] key) {
		return jedis.persist(key);
	}

	@Override
	public Long pexpire(byte[] key, long milliseconds) {
		return jedis.pexpire(key, milliseconds);
	}

	@Override
	public Long pexpireAt(byte[] key, long timestamp) {
		return jedis.pexpireAt(key, timestamp);
	}

	@Override
	public Long pttl(byte[] key) {
		return jedis.pttl(key);
	}

	@Override
	public byte[] randomkey() {
		return jedis.randomBinaryKey();
	}

	@Override
	public String rename(byte[] key, byte[] newKey) {
		return jedis.rename(key, newKey);
	}

	@Override
	public Boolean renamenx(byte[] key, byte[] newKey) {
		return jedis.renamenx(key, newKey) == 1;
	}

	@Override
	public String restore(byte[] key, long ttl, byte[] serializedValue, boolean replace, boolean absTtl, Long idleTime,
			Long frequency) {
		RestoreParams params = new RestoreParams();
		if (replace) {
			params.replace();
		}

		if (absTtl) {
			params.absTtl();
		}

		if (idleTime != null) {
			params.idleTime(idleTime);
		}

		if (frequency != null) {
			params.frequency(frequency);
		}
		return jedis.restore(key, ttl, serializedValue, params);
	}

	@SuppressWarnings("resource")
	@Override
	public Cursor<byte[]> scan(long cursorId, ScanOptions<byte[]> options) {
		return new ScanCursor<byte[], byte[]>(cursorId, options) {

			@Override
			protected ScanIteration<byte[]> doScan(long cursorId, ScanOptions<byte[]> options) {
				ScanParams scanParams = new ScanParams();
				if (options != null) {
					scanParams.match(options.getPattern());
					if (options.getCount() != null) {
						scanParams.count(options.getCount().intValue());
					}
				}
				redis.clients.jedis.ScanResult<byte[]> result = jedis.scan(SafeEncoder.encode(String.valueOf(cursorId)),
						scanParams);
				return new ScanIteration<>(Long.parseLong(result.getCursor()), result.getResult());
			}

			protected void doClose() {
				jedis.close();
			}
		}.open();
	}

	@Override
	public Long touch(byte[]... keys) {
		return jedis.touch(keys);
	}

	@Override
	public Long ttl(byte[] key) {
		return jedis.ttl(key);
	}

	@Override
	public DataType type(byte[] key) {
		String type = jedis.type(key);
		return type == null ? null : DataType.fromCode(type);
	}

	@Override
	public Long unlink(byte[]... keys) {
		return jedis.unlink(keys);
	}

	@Override
	public Long wait(int numreplicas, long timeout) {
		return jedis.waitReplicas(numreplicas, timeout);
	}
}
