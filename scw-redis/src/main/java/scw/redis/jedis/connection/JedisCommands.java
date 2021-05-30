/*package scw.redis.jedis.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import redis.clients.jedis.BitPosParams;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ListPosition;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.args.ListDirection;
import redis.clients.jedis.params.GeoAddParams;
import redis.clients.jedis.params.GetExParams;
import redis.clients.jedis.params.MigrateParams;
import redis.clients.jedis.params.RestoreParams;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.util.SafeEncoder;
import scw.core.Assert;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.data.geo.Circle;
import scw.data.geo.Marker;
import scw.data.geo.Metric;
import scw.data.geo.Point;
import scw.redis.connection.Cursor;
import scw.redis.connection.DataType;
import scw.redis.connection.KeyBoundCursor;
import scw.redis.connection.RedisAuth;
import scw.redis.connection.RedisBinaryCommands;
import scw.redis.connection.RedisValueEncoding;
import scw.redis.connection.RedisValueEncodings;
import scw.redis.connection.ScanCursor;
import scw.redis.connection.ScanIteration;
import scw.redis.connection.ScanOptions;
import scw.redis.jedis.JedisCodec;
import scw.util.comparator.OrderBy;

public class JedisCommands implements RedisBinaryCommands {
	private final Jedis jedis;

	public JedisCommands(Jedis jedis) {
		this.jedis = jedis;
	}

	@Override
	public Boolean copy(byte[] source, byte[] destination,
			Integer destinationDB, boolean replace) {
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
	public Set<byte[]> keys(byte[] pattern) {
		return jedis.keys(pattern);
	}

	@Override
	public String migrate(String host, int port, byte[] key, int targetDB,
			int timeout) {
		return jedis.migrate(host, port, key, targetDB, timeout);
	}

	@Override
	public String migrate(String host, int port, int targetDB, int timeout,
			boolean copy, boolean replace, RedisAuth auth, byte[]... keys) {
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
		RedisValueEncoding encoding = RedisValueEncoding.of(SafeEncoder
				.encode(value));
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
	public String restore(byte[] key, long ttl, byte[] serializedValue,
			boolean replace, boolean absTtl, Long idleTime, Long frequency) {
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

	private ScanParams toScanParams(ScanOptions<byte[]> options) {
		ScanParams scanParams = new ScanParams();
		if (options != null) {
			scanParams.match(options.getPattern());
			if (options.getCount() != null) {
				scanParams.count(options.getCount().intValue());
			}
		}
		return scanParams;
	}

	@Override
	public Cursor<byte[]> scan(long cursorId, ScanOptions<byte[]> options) {
		return new ScanCursor<byte[], byte[]>(cursorId, options) {

			@Override
			protected ScanIteration<byte[]> doScan(long cursorId,
					ScanOptions<byte[]> options) {
				ScanParams scanParams = toScanParams(options);
				redis.clients.jedis.ScanResult<byte[]> result = jedis.scan(
						SafeEncoder.encode(String.valueOf(cursorId)),
						scanParams);
				return new ScanIteration<>(Long.parseLong(result.getCursor()),
						result.getResult());
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

	@Override
	public Long append(byte[] key, byte[] value) {
		return jedis.append(key, value);
	}

	@Override
	public Long bitcount(byte[] key, long start, long end) {
		return jedis.bitcount(key, start, end);
	}

	@Override
	public Long bitop(BitOP op, byte[] destkey, byte[]... srcKeys) {
		switch (op) {
		case AND:
			return jedis.bitop(redis.clients.jedis.BitOP.AND, destkey, srcKeys);
		case NOT:
			return jedis.bitop(redis.clients.jedis.BitOP.NOT, destkey, srcKeys);
		case OR:
			return jedis.bitop(redis.clients.jedis.BitOP.OR, destkey, srcKeys);
		case XOR:
			return jedis.bitop(redis.clients.jedis.BitOP.XOR, destkey, srcKeys);
		default:
			return null;
		}
	}

	@Override
	public Long bitpos(byte[] key, boolean bit, Long start, Long end) {
		if (start == null && end == null) {
			return jedis.bitpos(key, bit);
		}

		if (start == null) {
			if (end == null) {
				return jedis.bitpos(key, bit);
			}
			BitPosParams params = new BitPosParams(0, end);
			return jedis.bitpos(key, bit, params);
		} else {
			if (end == null) {
				BitPosParams params = new BitPosParams(start);
				return jedis.bitpos(key, bit, params);
			} else {
				BitPosParams params = new BitPosParams(start, end);
				return jedis.bitpos(key, bit, params);
			}
		}
	}

	@Override
	public Long decr(byte[] key) {
		return jedis.decr(key);
	}

	@Override
	public Long decrBy(byte[] key, long decrement) {
		return jedis.decrBy(key, decrement);
	}

	@Override
	public byte[] get(byte[] key) {
		return jedis.get(key);
	}

	@Override
	public Boolean getbit(byte[] key, Long offset) {
		return jedis.getbit(key, offset);
	}

	@Override
	public byte[] getdel(byte[] key) {
		return jedis.getDel(key);
	}

	@Override
	public byte[] getEx(byte[] key,
			scw.redis.connection.RedisStringCommands.ExpireOption option,
			Long time) {
		GetExParams params = new GetExParams();
		switch (option) {
		case EX:
			params.ex(time);
			break;
		case EXAT:
			params.exAt(time);
			break;
		case PX:
			params.px(time);
			break;
		case PXAT:
			params.pxAt(time);
		case PERSIST:
			params.persist();
			break;
		default:
			break;
		}
		return jedis.getEx(key, params);
	}

	@Override
	public byte[] getrange(byte[] key, long startOffset, long endOffset) {
		return jedis.getrange(key, startOffset, endOffset);
	}

	@Override
	public byte[] getset(byte[] key, byte[] value) {
		return jedis.getSet(key, value);
	}

	@Override
	public Long incr(byte[] key) {
		return jedis.incr(key);
	}

	@Override
	public Long incrBy(byte[] key, long increment) {
		return jedis.incrBy(key, increment);
	}

	@Override
	public Double incrByFloat(byte[] key, double increment) {
		return jedis.incrByFloat(key, increment);
	}

	@Override
	public List<byte[]> mget(byte[]... keys) {
		return jedis.mget(keys);
	}

	private byte[][] toPairsArgs(Map<byte[], byte[]> pairs) {
		if (CollectionUtils.isEmpty(pairs)) {
			return new byte[0][0];
		}
		List<byte[]> args = new ArrayList<byte[]>();
		for (Entry<byte[], byte[]> entry : pairs.entrySet()) {
			args.add(entry.getKey());
			args.add(entry.getValue());
		}
		return args.toArray(new byte[0][0]);
	}

	@Override
	public Boolean mset(Map<byte[], byte[]> pairs) {
		byte[][] bytes = toPairsArgs(pairs);
		return "OK".equalsIgnoreCase(jedis.mset(bytes));
	}

	@Override
	public Long msetnx(Map<byte[], byte[]> pairs) {
		byte[][] bytes = toPairsArgs(pairs);
		return jedis.msetnx(bytes);
	}

	@Override
	public Boolean psetex(byte[] key, long milliseconds, byte[] value) {
		return "OK".equalsIgnoreCase(jedis.psetex(key, milliseconds, value));
	}

	@Override
	public Boolean set(byte[] key, byte[] value,
			scw.redis.connection.RedisStringCommands.ExpireOption option,
			long time,
			scw.redis.connection.RedisStringCommands.SetOption setOption,
			boolean get) {
		SetParams params = new SetParams();
		if (option != null) {
			switch (option) {
			case EX:
				params.ex(time);
				break;
			case EXAT:
				params.exAt(time);
				break;
			case PX:
				params.px(time);
				break;
			case PXAT:
				params.pxAt(time);
			case PERSIST:
			default:
				break;
			}
		}

		if (setOption != null) {
			switch (setOption) {
			case NX:
				params.nx();
				break;
			case XX:
				params.xx();
				break;
			default:
				break;
			}
		}

		String response = jedis.set(key, value, params);
		return parseBoolean(response);
	}

	@Override
	public Boolean setbit(byte[] key, long offset, boolean value) {
		return jedis.setbit(key, offset, value);
	}

	private Boolean parseBoolean(String response) {
		return response == null ? null : "OK".equalsIgnoreCase(response);
	}

	@Override
	public Boolean setex(byte[] key, long seconds, byte[] value) {
		String response = jedis.setex(key, seconds, value);
		return parseBoolean(response);
	}

	@Override
	public Boolean setNX(byte[] key, byte[] value) {
		Long response = jedis.setnx(key, value);
		return response == null ? null : (response == 1);
	}

	@Override
	public Long setrange(byte[] key, Long offset, byte[] value) {
		return jedis.setrange(key, offset, value);
	}

	@Override
	public Long strlen(byte[] key) {
		return jedis.strlen(key);
	}

	@Override
	public Long sadd(byte[] key, byte[]... members) {
		return jedis.sadd(key, members);
	}

	@Override
	public Long scard(byte[] key) {
		return jedis.scard(key);
	}

	@Override
	public Set<byte[]> sdiff(byte[]... keys) {
		return jedis.sdiff(keys);
	}

	@Override
	public Long sdiffstore(byte[] destinationKey, byte[]... keys) {
		return jedis.sdiffstore(destinationKey, keys);
	}

	@Override
	public Set<byte[]> sinter(byte[]... keys) {
		return jedis.sinter(keys);
	}

	@Override
	public Long sinterstore(byte[] destinationKey, byte[]... keys) {
		return jedis.sinterstore(destinationKey, keys);
	}

	@Override
	public Boolean sismember(byte[] key, byte[] member) {
		return jedis.sismember(key, member);
	}

	@Override
	public Set<byte[]> smembers(byte[] key) {
		return jedis.smembers(key);
	}

	@Override
	public List<Boolean> smismember(byte[] key, byte[]... members) {
		return jedis.smismember(key, members);
	}

	@Override
	public Boolean sMove(byte[] sourceKey, byte[] destinationKey, byte[] member) {
		Long value = jedis.smove(sourceKey, destinationKey, member);
		return value == null ? null : (value == 1);
	}

	@Override
	public Set<byte[]> spop(byte[] key, int count) {
		return jedis.spop(key, count);
	}

	@Override
	public List<byte[]> srandmember(byte[] key, int count) {
		return jedis.srandmember(key, count);
	}

	@Override
	public Long srem(byte[] key, byte[]... members) {
		return jedis.srem(key, members);
	}

	@Override
	public Set<byte[]> sunion(byte[]... keys) {
		return jedis.sunion(keys);
	}

	@Override
	public Long sunionstore(byte[] destinationKey, byte[]... keys) {
		return jedis.sunionstore(destinationKey, keys);
	}

	@Override
	public Cursor<byte[]> sScan(long cursorId, byte[] key,
			ScanOptions<byte[]> options) {
		Assert.notNull(key, "Key must not be null!");
		return new KeyBoundCursor<byte[], byte[]>(key, cursorId, options) {

			@Override
			protected ScanIteration<byte[]> doScan(byte[] key, long cursorId,
					ScanOptions<byte[]> options) {
				ScanParams params = toScanParams(options);

				redis.clients.jedis.ScanResult<byte[]> result = jedis.sscan(
						key, SafeEncoder.encode(String.valueOf(cursorId)),
						params);
				return new ScanIteration<>(Long.valueOf(result.getCursor()),
						result.getResult());
			}

			protected void doClose() {
				jedis.close();
			};
		}.open();
	}

	@Override
	public byte[] blmove(byte[] sourceKey, byte[] destinationKey,
			scw.redis.connection.RedisListsCommands.MovePosition from,
			scw.redis.connection.RedisListsCommands.MovePosition to, long timout) {
		return jedis.blmove(sourceKey, destinationKey, toListDirection(from),
				toListDirection(to), timout);
	}

	@Override
	public List<byte[]> blpop(byte[]... keys) {
		return jedis.blpop(keys);
	}

	@Override
	public List<byte[]> blpop(double timeout, byte[]... keys) {
		return jedis.blpop(timeout, keys);
	}

	@Override
	public List<byte[]> brpop(byte[]... keys) {
		return jedis.brpop(keys);
	}

	@Override
	public List<byte[]> brpop(double timeout, byte[]... keys) {
		return jedis.brpop(timeout, keys);
	}

	@Override
	public byte[] brpoplpush(byte[] sourceKey, byte[] destinationKey,
			double timout) {
		return jedis.brpoplpush(sourceKey, destinationKey, (int) timout);
	}

	@Override
	public byte[] lindex(byte[] key, long index) {
		return jedis.lindex(key, index);
	}

	@Override
	public Long linsert(byte[] key,
			scw.redis.connection.RedisListsCommands.InsertPosition position,
			byte[] pivot, byte[] value) {
		return jedis.linsert(key,
				position == InsertPosition.AFTER ? ListPosition.AFTER
						: ListPosition.BEFORE, pivot, value);
	}

	@Override
	public Long llen(byte[] key) {
		return jedis.llen(key);
	}

	private ListDirection toListDirection(MovePosition position) {
		return position == MovePosition.LEFT ? ListDirection.LEFT
				: ListDirection.RIGHT;
	}

	@Override
	public byte[] lmove(byte[] sourceKey, byte[] destinationKey,
			scw.redis.connection.RedisListsCommands.MovePosition from,
			scw.redis.connection.RedisListsCommands.MovePosition to) {
		return jedis.lmove(sourceKey, destinationKey, toListDirection(from),
				toListDirection(to));
	}
	
	@Override
	public List<byte[]> lpop(byte[] key, int count) {
		return jedis.lpop(key, count);
	}
	
	@Override
	public Long lpush(byte[] key, byte[]... elements) {
		return jedis.lpush(key, elements);
	}
	
	@Override
	public Long lpushx(byte[] key, byte[]... elements) {
		return jedis.lpushx(key, elements);
	}
	
	@Override
	public List<byte[]> lrange(byte[] key, long start, long stop) {
		return jedis.lrange(key, start, stop);
	}
	
	@Override
	public Long lrem(byte[] key, int count, byte[] element) {
		return jedis.lrem(key, count, element);
	}
	
	@Override
	public Boolean lset(byte[] key, long index, byte[] element) {
		String response = jedis.lset(key, index, element);
		return parseBoolean(response);
	}
	
	@Override
	public Boolean ltrim(byte[] key, long start, long stop) {
		String response = jedis.ltrim(key, start, stop);
		return parseBoolean(response);
	}
	
	@Override
	public List<byte[]> rpop(byte[] key, int count) {
		return jedis.rpop(key, count);
	}
	
	@Override
	public byte[] rpoplpush(byte[] sourceKey, byte[] destinationKey) {
		return jedis.rpoplpush(sourceKey, destinationKey);
	}
	
	@Override
	public Long rpush(byte[] key, byte[]... elements) {
		return jedis.rpush(key, elements);
	}
	
	@Override
	public Long rpushx(byte[] key, byte[]... elements) {
		return jedis.rpushx(key, elements);
	}
	
	@Override
	public Long pfadd(byte[] key, byte[]... elements) {
		return jedis.pfadd(key, elements);
	}
	
	@Override
	public Long pfcount(byte[]... keys) {
		return jedis.pfcount(keys);
	}
	
	@Override
	public Boolean pfmerge(byte[] destKey, byte[]... sourceKeys) {
		String response = jedis.pfmerge(destkey, sourcekeys);
		return parseBoolean(response);
	}
	
	@Override
	public String select(int index) {
		return jedis.select(index);
	}
	
	@Override
	public byte[] ping(byte[] message) {
		return jedis.ping(message);
	}
	
	@Override
	public Long geoadd(byte[] key,
			scw.redis.connection.RedisGeoCommands.GeoaddOption option,
			Marker<byte[]>... elements) {
		GeoAddParams params = new GeoAddParams();
		switch (option) {
		case CH:
			params.ch();
			break;
		case NX:
			params.nx();
			break;
		case XX:
			params.xx();
			break;
		default:
			break;
		}
		
		Map<byte[], GeoCoordinate> memberCoordinateMap = new HashMap<byte[], GeoCoordinate>(elements.length);
		for(Marker<byte[]> marker : elements){
			GeoCoordinate coordinate = new GeoCoordinate(marker.getX(), marker.getY());
			memberCoordinateMap.put(marker.getName(), coordinate);
		}
		return jedis.geoadd(key, params, memberCoordinateMap);
	}
	
	@Override
	public Double geodist(byte[] key, byte[] member1, byte[] member2,
			scw.redis.connection.GeoUnit unit) {
		GeoUnit geoUnit = null;
		switch (unit) {
		case M:
			geoUnit = GeoUnit.M;
			break;
		case FT:
			geoUnit = GeoUnit.FT;
			break;
		case KM:
			geoUnit = GeoUnit.KM;
		case MI:
			geoUnit = GeoUnit.MI;
		default:
			break;
		}
		return jedis.geodist(key, member1, member2, geoUnit);
	}
	
	@Override
	public List<String> geohash(byte[] key, byte[]... members) {
		List<byte[]> list = jedis.geohash(key, members);
		return new JedisCodec().toDecodeConverter().convert(list, new ArrayList<String>());
	}
	
	@Override
	public List<Point> geopos(byte[] key, byte[]... members) {
		List<GeoCoordinate> list = jedis.geopos(key, members);
		List<Point> points = new ArrayList<Point>();
		for(GeoCoordinate geo : list){
			points.add(new Point(geo.getLatitude(), geo.getLongitude()));
		}
		return points;
	}
	
	private GeoUnit toGeoUnit(Metric metric){
		if(metric == null){
			return GeoUnit.M;
		}

		String name = metric.getAbbreviation();
		if(StringUtils.isEmpty(name)){
			return GeoUnit.M;
		}
		return GeoUnit.valueOf(name.toUpperCase());
	}
	
	@Override
	public List<scw.redis.connection.RedisGeoCommands.GeoResults> georadius(
			byte[] key, Circle within, Integer count, OrderBy orderBy,
			scw.redis.connection.RedisGeoCommands.GeoradiusStroage store,
			scw.redis.connection.RedisGeoCommands.GeoResultWith... withs) {
		List<GeoRadiusResponse> list = jedis.georadius(key, within.getPoint().getX(), within.getPoint().getY(), within.getRadius().getValue(), toGeoUnit(within.getRadius().getMetric()))
		
		// TODO Auto-generated method stub
		return null;
	}
}
*/