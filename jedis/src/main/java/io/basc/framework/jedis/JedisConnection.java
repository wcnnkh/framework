package io.basc.framework.jedis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.basc.framework.data.domain.Range;
import io.basc.framework.data.geo.Circle;
import io.basc.framework.data.geo.Distance;
import io.basc.framework.data.geo.Metric;
import io.basc.framework.data.geo.Point;
import io.basc.framework.lang.Nullable;
import io.basc.framework.redis.ClaimArgs;
import io.basc.framework.redis.DataType;
import io.basc.framework.redis.ExpireOption;
import io.basc.framework.redis.FailoverParams;
import io.basc.framework.redis.FlushMode;
import io.basc.framework.redis.GeoRadiusArgs;
import io.basc.framework.redis.GeoRadiusWith;
import io.basc.framework.redis.GeoWithin;
import io.basc.framework.redis.GeoaddOption;
import io.basc.framework.redis.InterArgs;
import io.basc.framework.redis.MessageListener;
import io.basc.framework.redis.Module;
import io.basc.framework.redis.RedisConnection;
import io.basc.framework.redis.RedisPipeline;
import io.basc.framework.redis.RedisSubscribedConnectionException;
import io.basc.framework.redis.RedisSystemException;
import io.basc.framework.redis.RedisTransaction;
import io.basc.framework.redis.RedisValueEncoding;
import io.basc.framework.redis.RedisValueEncodings;
import io.basc.framework.redis.SaveMode;
import io.basc.framework.redis.ScanOptions;
import io.basc.framework.redis.ScoreOption;
import io.basc.framework.redis.SetOption;
import io.basc.framework.redis.Slowlog;
import io.basc.framework.redis.Subscription;
import io.basc.framework.redis.Tuple;
import io.basc.framework.redis.convert.RedisConverters;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Decorator;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.util.page.Pageable;
import io.basc.framework.util.page.SharedPageable;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.GeoRadiusResponse;
import redis.clients.jedis.resps.ScanResult;
import redis.clients.jedis.util.SafeEncoder;

@SuppressWarnings({ "unchecked" })
public class JedisConnection implements RedisConnection<byte[], byte[]>, Decorator {
	private final Jedis jedis;

	public JedisConnection(Jedis jedis) {
		this.jedis = jedis;
	}

	@Override
	public <T> T getDelegate(Class<T> targetType) {
		return XUtils.getDelegate(jedis, targetType);
	}

	@Override
	public void close() {
		jedis.close();
	}

	@Override
	public boolean isClosed() {
		return !jedis.isConnected();
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
	public Set<byte[]> keys(byte[] pattern) {
		return jedis.keys(pattern);
	}

	@Override
	public String migrate(String host, int port, byte[] key, int targetDB, int timeout) {
		return jedis.migrate(host, port, key, targetDB, timeout);
	}

	@Override
	public String migrate(String host, int port, int targetDB, int timeout,
			io.basc.framework.redis.MigrateParams option, byte[]... keys) {
		return jedis.migrate(host, port, targetDB, timeout, JedisUtils.toMigrateParams(option), keys);
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
	public String restore(byte[] key, long ttl, byte[] serializedValue, io.basc.framework.redis.RestoreParams params) {
		return jedis.restore(key, ttl, serializedValue, JedisUtils.toRestoreParams(params));
	}

	@Override
	public Pageable<Long, byte[]> scan(long cursorId, ScanOptions<byte[]> options) {
		ScanResult<byte[]> result = jedis.scan(SafeEncoder.encode(String.valueOf(cursorId)),
				JedisUtils.toScanParams(options));
		String next = result.getCursor();
		return new SharedPageable<Long, byte[]>(cursorId, result.getResult(),
				StringUtils.isEmpty(next) ? null : Long.parseLong(next));
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
	public Long bitop(io.basc.framework.redis.BitOP op, byte[] destkey, byte[]... srcKeys) {
		return jedis.bitop(JedisUtils.toBitOP(op), destkey, srcKeys);
	}

	@Override
	public Long bitpos(byte[] key, boolean bit, Long start, Long end) {
		if (start == null && end == null) {
			return jedis.bitpos(key, bit);
		}

		return jedis.bitpos(key, bit, JedisUtils.toBitPosParams(start, end));
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
	public byte[] getEx(byte[] key, ExpireOption option, Long time) {
		return jedis.getEx(key, JedisUtils.toGetExParams(option, time));
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

	@Override
	public Boolean mset(Map<byte[], byte[]> pairs) {
		return "OK".equalsIgnoreCase(jedis.mset(JedisUtils.toPairsArgs(pairs)));
	}

	@Override
	public Long msetnx(Map<byte[], byte[]> pairs) {
		return jedis.msetnx(JedisUtils.toPairsArgs(pairs));
	}

	@Override
	public Boolean psetex(byte[] key, long milliseconds, byte[] value) {
		return "OK".equalsIgnoreCase(jedis.psetex(key, milliseconds, value));
	}

	@Override
	public String set(byte[] key, byte[] value) {
		return jedis.set(key, value);
	}

	@Override
	public Boolean set(byte[] key, byte[] value, ExpireOption option, long time, SetOption setOption) {
		String response = jedis.set(key, value, JedisUtils.toSetParams(option, time, setOption));
		return JedisUtils.parseBoolean(response);
	}

	@Override
	public Boolean setbit(byte[] key, long offset, boolean value) {
		return jedis.setbit(key, offset, value);
	}

	@Override
	public Boolean setex(byte[] key, long seconds, byte[] value) {
		String response = jedis.setex(key, seconds, value);
		return JedisUtils.parseBoolean(response);
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
	public Pageable<Long, byte[]> sScan(long cursorId, byte[] key, ScanOptions<byte[]> options) {
		Assert.notNull(key, "Key must not be null!");
		ScanResult<byte[]> result = jedis.scan(SafeEncoder.encode(String.valueOf(cursorId)),
				JedisUtils.toScanParams(options));
		return new SharedPageable<Long, byte[]>(cursorId, result.getResult(), Long.parseLong(result.getCursor()));
	}

	@Override
	public byte[] blmove(byte[] sourceKey, byte[] destinationKey, io.basc.framework.redis.MovePosition from,
			io.basc.framework.redis.MovePosition to, long timout) {
		return jedis.blmove(sourceKey, destinationKey, JedisUtils.toListDirection(from), JedisUtils.toListDirection(to),
				timout);
	}

	@Override
	public List<byte[]> blpop(double timeout, byte[]... keys) {
		return jedis.blpop(timeout, keys);
	}

	@Override
	public List<byte[]> brpop(double timeout, byte[]... keys) {
		return jedis.brpop(timeout, keys);
	}

	@Override
	public byte[] brpoplpush(byte[] sourceKey, byte[] destinationKey, double timout) {
		return jedis.brpoplpush(sourceKey, destinationKey, (int) timout);
	}

	@Override
	public byte[] lindex(byte[] key, long index) {
		return jedis.lindex(key, index);
	}

	@Override
	public Long linsert(byte[] key, io.basc.framework.redis.InsertPosition position, byte[] pivot, byte[] value) {
		return jedis.linsert(key, JedisUtils.toListPosition(position), pivot, value);
	}

	@Override
	public Long llen(byte[] key) {
		return jedis.llen(key);
	}

	@Override
	public byte[] lmove(byte[] sourceKey, byte[] destinationKey, io.basc.framework.redis.MovePosition from,
			io.basc.framework.redis.MovePosition to) {
		return jedis.lmove(sourceKey, destinationKey, JedisUtils.toListDirection(from), JedisUtils.toListDirection(to));
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
		return JedisUtils.parseBoolean(response);
	}

	@Override
	public Boolean ltrim(byte[] key, long start, long stop) {
		String response = jedis.ltrim(key, start, stop);
		return JedisUtils.parseBoolean(response);
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
	public String pfmerge(byte[] destKey, byte[]... sourceKeys) {
		return jedis.pfmerge(destKey, sourceKeys);
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
	public Long geoadd(byte[] key, GeoaddOption option, Map<byte[], Point> members) {
		return jedis.geoadd(key, JedisUtils.toGeoAddParams(option), JedisUtils.toMemberCoordinateMap(members));
	}

	@Override
	public Double geodist(byte[] key, byte[] member1, byte[] member2, Metric metric) {
		return jedis.geodist(key, member1, member2, JedisUtils.toGeoUnit(metric));
	}

	@Override
	public List<String> geohash(byte[] key, byte[]... members) {
		List<byte[]> list = jedis.geohash(key, members);
		return JedisCodec.INSTANCE.toDecodeProcessor().processAll(list, new ArrayList<String>());
	}

	@Override
	public List<Point> geopos(byte[] key, byte[]... members) {
		List<GeoCoordinate> list = jedis.geopos(key, members);
		return JedisUtils.toPoints(list);
	}

	@Override
	public List<byte[]> georadius(byte[] key, Circle within, GeoRadiusArgs<byte[]> args) {
		List<GeoRadiusResponse> list = jedis.georadius(key, within.getPoint().getX(), within.getPoint().getY(),
				within.getRadius().getValue(), JedisUtils.toGeoUnit(within.getRadius().getMetric()),
				JedisUtils.toGeoRadiusParam(null, args));
		return JedisUtils.toGeoMembers(list);
	}

	@Override
	public List<GeoWithin<byte[]>> georadius(byte[] key, Circle within, GeoRadiusWith with,
			GeoRadiusArgs<byte[]> args) {
		List<GeoRadiusResponse> list = jedis.georadius(key, within.getPoint().getX(), within.getPoint().getY(),
				within.getRadius().getValue(), JedisUtils.toGeoUnit(within.getRadius().getMetric()),
				JedisUtils.toGeoRadiusParam(null, args));
		return JedisUtils.toGeoWithins(list);
	}

	@Override
	public List<byte[]> georadiusbymember(byte[] key, byte[] member, Distance distance, GeoRadiusArgs<byte[]> args) {
		List<GeoRadiusResponse> list = jedis.georadiusByMember(key, member, distance.getValue(),
				JedisUtils.toGeoUnit(distance.getMetric()), JedisUtils.toGeoRadiusParam(null, args));
		return JedisUtils.toGeoMembers(list);
	}

	@Override
	public List<GeoWithin<byte[]>> georadiusbymember(byte[] key, byte[] member, Distance distance, GeoRadiusWith with,
			GeoRadiusArgs<byte[]> args) {
		List<GeoRadiusResponse> list = jedis.georadiusByMember(key, member, distance.getValue(),
				JedisUtils.toGeoUnit(distance.getMetric()), JedisUtils.toGeoRadiusParam(with, args));
		return JedisUtils.toGeoWithins(list);
	}

	@Override
	public List<byte[]> bzpopmin(double timeout, byte[]... keys) {
		return jedis.bzpopmin(timeout, keys);
	}

	@Override
	public Long zadd(byte[] key, SetOption setOption, ScoreOption scoreOption, boolean changed,
			Map<byte[], Double> memberScores) {
		return jedis.zadd(key, memberScores, JedisUtils.toZAddParams(setOption, scoreOption, changed));
	}

	@Override
	public Double zaddIncr(byte[] key, SetOption setOption, ScoreOption scoreOption, boolean changed, double score,
			byte[] member) {
		return jedis.zaddIncr(key, score, member, JedisUtils.toZAddParams(setOption, scoreOption, changed));
	}

	@Override
	public Long zcard(byte[] key) {
		return jedis.zcard(key);
	}

	@Override
	public Long zcount(byte[] key, Range<? extends Number> range) {
		return jedis.zcount(key, range.getLowerBound().getValue().get().doubleValue(),
				range.getUpperBound().getValue().get().doubleValue());
	}

	@Override
	public Long zdiffstore(byte[] destinationKey, byte[]... keys) {
		return jedis.zdiffStore(destinationKey, keys);
	}

	@Override
	public Double zincrby(byte[] key, double increment, byte[] member) {
		return jedis.zincrby(key, increment, member);
	}

	@Override
	public Collection<byte[]> zinter(InterArgs args, byte[]... keys) {
		return jedis.zinter(JedisUtils.toZParams(args), keys);
	}

	@Override
	public Collection<Tuple<byte[]>> zinterWithScores(InterArgs args, byte[]... keys) {
		Set<redis.clients.jedis.resps.Tuple> tuples = jedis.zinterWithScores(JedisUtils.toZParams(args), keys);
		return JedisUtils.toTuples(tuples);
	}

	@Override
	public Long zinterstore(byte[] destinationKey, InterArgs interArgs, byte[]... keys) {
		return jedis.zinterstore(destinationKey, JedisUtils.toZParams(interArgs), keys);
	}

	@Override
	public Long zlexcount(byte[] key, Range<byte[]> range) {
		return jedis.zlexcount(key, RedisConverters.convertLowerBound(range.getLowerBound(), JedisCodec.INSTANCE),
				RedisConverters.convertUpperBound(range.getUpperBound(), JedisCodec.INSTANCE));
	}

	@Override
	public List<Double> zmscore(byte[] key, byte[]... members) {
		return jedis.zmscore(key, members);
	}

	@Override
	public Collection<Tuple<byte[]>> zpopmax(byte[] key, int count) {
		List<redis.clients.jedis.resps.Tuple> tuples = jedis.zpopmax(key, count);
		return JedisUtils.toTuples(tuples);
	}

	@Override
	public Collection<Tuple<byte[]>> zpopmin(byte[] key, int count) {
		List<redis.clients.jedis.resps.Tuple> tuples = jedis.zpopmin(key, count);
		return JedisUtils.toTuples(tuples);
	}

	@Override
	public Collection<byte[]> zrandmember(byte[] key, int count) {
		return jedis.zrandmember(key, count);
	}

	@Override
	public Collection<Tuple<byte[]>> zrandmemberWithScores(byte[] key, int count) {
		List<redis.clients.jedis.resps.Tuple> tuples = jedis.zrandmemberWithScores(key, count);
		return JedisUtils.toTuples(tuples);
	}

	@Override
	public Collection<byte[]> zrange(byte[] key, long start, long stop) {
		return jedis.zrange(key, start, stop);
	}

	@Override
	public Collection<byte[]> zrangeByLex(byte[] key, Range<byte[]> range, int offset, int limit) {
		return jedis.zrangeByLex(key, RedisConverters.convertLowerBound(range.getLowerBound(), JedisCodec.INSTANCE),
				RedisConverters.convertUpperBound(range.getUpperBound(), JedisCodec.INSTANCE), offset, limit);
	}

	@Override
	public Collection<byte[]> zrangeByScore(byte[] key, Range<byte[]> range, int offset, int limit) {
		return jedis.zrangeByScore(key, RedisConverters.convertLowerBound(range.getLowerBound(), JedisCodec.INSTANCE),
				RedisConverters.convertUpperBound(range.getUpperBound(), JedisCodec.INSTANCE), offset, limit);
	}

	@Override
	public Collection<Tuple<byte[]>> zrangeByScoreWithScores(byte[] key, Range<byte[]> range, int offset, int limit) {
		List<redis.clients.jedis.resps.Tuple> tuples = jedis.zrangeByScoreWithScores(key,
				RedisConverters.convertLowerBound(range.getLowerBound(), JedisCodec.INSTANCE),
				RedisConverters.convertUpperBound(range.getUpperBound(), JedisCodec.INSTANCE), offset, limit);
		return JedisUtils.toTuples(tuples);
	}

	@Override
	public Long zrank(byte[] key, byte[] member) {
		return jedis.zrank(key, member);
	}

	@Override
	public Long zrem(byte[] key, byte[]... members) {
		return jedis.zrem(key, members);
	}

	@Override
	public Long zremrangebylex(byte[] key, Range<byte[]> range) {
		return jedis.zremrangeByLex(key, RedisConverters.convertLowerBound(range.getLowerBound(), JedisCodec.INSTANCE),
				RedisConverters.convertUpperBound(range.getUpperBound(), JedisCodec.INSTANCE));
	}

	@Override
	public Long zremrangebyrank(byte[] key, long start, long stop) {
		return jedis.zremrangeByRank(key, start, stop);
	}

	@Override
	public Long zremrangebyscore(byte[] key, Range<byte[]> range) {
		return jedis.zremrangeByScore(key,
				RedisConverters.convertLowerBound(range.getLowerBound(), JedisCodec.INSTANCE),
				RedisConverters.convertUpperBound(range.getUpperBound(), JedisCodec.INSTANCE));
	}

	@Override
	public Collection<byte[]> zrevrange(byte[] key, long start, long stop) {
		return jedis.zrevrange(key, start, stop);
	}

	@Override
	public Collection<byte[]> zrevrangebylex(byte[] key, Range<byte[]> range, int offset, int count) {
		return jedis.zrevrangeByLex(key, RedisConverters.convertLowerBound(range.getLowerBound(), JedisCodec.INSTANCE),
				RedisConverters.convertUpperBound(range.getUpperBound(), JedisCodec.INSTANCE), offset, count);
	}

	@Override
	public Collection<byte[]> zrevrangebyscore(byte[] key, Range<byte[]> range, int offset, int count) {
		return jedis.zrevrangeByScore(key,
				RedisConverters.convertLowerBound(range.getLowerBound(), JedisCodec.INSTANCE),
				RedisConverters.convertUpperBound(range.getUpperBound(), JedisCodec.INSTANCE), offset, count);
	}

	@Override
	public Collection<io.basc.framework.redis.Tuple<byte[]>> zrevrangebyscoreWithScores(byte[] key, Range<byte[]> range,
			int offset, int count) {
		List<redis.clients.jedis.resps.Tuple> tuples = jedis.zrevrangeByScoreWithScores(key,
				RedisConverters.convertLowerBound(range.getLowerBound(), JedisCodec.INSTANCE),
				RedisConverters.convertUpperBound(range.getUpperBound(), JedisCodec.INSTANCE), offset, count);
		return JedisUtils.toTuples(tuples);
	}

	@Override
	public Long zrevrank(byte[] key, byte[] member) {
		return jedis.zrevrank(key, member);
	}

	@Override
	public Double zscore(byte[] key, byte[] member) {
		return jedis.zscore(key, member);
	}

	@Override
	public Collection<byte[]> zunion(io.basc.framework.redis.InterArgs interArgs, byte[]... keys) {
		return jedis.zunion(JedisUtils.toZParams(interArgs), keys);
	}

	@Override
	public Collection<io.basc.framework.redis.Tuple<byte[]>> zunionWithScores(
			io.basc.framework.redis.InterArgs interArgs, byte[]... keys) {
		Set<redis.clients.jedis.resps.Tuple> tuples = jedis.zunionWithScores(JedisUtils.toZParams(interArgs), keys);
		return JedisUtils.toTuples(tuples);
	}

	@Override
	public Long zunionstore(byte[] destinationKey, io.basc.framework.redis.InterArgs interArgs, byte[]... keys) {
		return jedis.zunionstore(destinationKey, JedisUtils.toZParams(interArgs), keys);
	}

	@Override
	public Long xack(byte[] key, byte[] group, byte[]... ids) {
		return jedis.xack(key, group, ids);
	}

	@Override
	public List<byte[]> xclaim(byte[] key, byte[] group, byte[] consumer, long minIdleTime, ClaimArgs args,
			byte[]... ids) {
		if (args != null && args.isJustId()) {
			return jedis.xclaimJustId(key, group, consumer, minIdleTime, JedisUtils.toXClaimParams(args), ids);
		} else {
			return jedis.xclaim(key, group, consumer, minIdleTime, JedisUtils.toXClaimParams(args), ids);
		}
	}

	@Override
	public Long xdel(byte[] key, byte[]... ids) {
		return jedis.xdel(key, ids);
	}

	@Override
	public <T> T eval(byte[] script, List<byte[]> keys, List<byte[]> args) {
		Assert.notNull(script, "Script must not be null!");
		Object value = jedis.eval(script, keys, args);
		if (value == null) {
			return null;
		}
		return (T) value;
	}

	@Override
	public <T> T evalsha(byte[] sha1, List<byte[]> keys, List<byte[]> args) {
		Assert.notNull(sha1, "sha1 must not be null!");
		Object value = jedis.evalsha(sha1, keys, args);
		if (value == null) {
			return null;
		}
		return (T) value;
	}

	@Override
	public List<Boolean> scriptexists(byte[]... sha1) {
		return jedis.scriptExists(sha1);
	}

	@Override
	public String scriptFlush() {
		return jedis.scriptFlush();
	}

	@Override
	public String scriptFlush(io.basc.framework.redis.FlushMode flushMode) {
		return jedis.scriptFlush(flushMode == FlushMode.ASYNC ? redis.clients.jedis.args.FlushMode.ASYNC
				: redis.clients.jedis.args.FlushMode.SYNC);
	}

	@Override
	public String scriptKill() {
		return jedis.scriptKill();
	}

	@Override
	public byte[] scriptLoad(byte[] script) {
		return jedis.scriptLoad(script);
	}

	@Nullable
	private volatile RedisTransaction<byte[], byte[]> transaction;
	@Nullable
	private volatile RedisPipeline<byte[], byte[]> pipeline;
	@Nullable
	private volatile JedisSubscription subscription;

	@Override
	public RedisTransaction<byte[], byte[]> multi() {
		if (!isQueueing()) {
			synchronized (jedis) {
				if (!isQueueing()) {
					if (isPipelined()) {
						throw new RedisSystemException("Pipes cannot be used in transactions");
					}

					if (isSubscribed()) {
						throw new RedisSystemException("Subscribed connections cannot use transactions");
					}

					transaction = new JedisTransaction(jedis.multi());
				}
			}
		}
		return transaction;
	}

	@Override
	public String discard() {
		if (isQueueing()) {
			synchronized (this) {
				if (isQueueing()) {
					return transaction.discard();
				}
			}
		}
		throw new IllegalAccessError("No ongoing transaction. Did you forget to call multi?");
	}

	@Override
	public String watch(byte[]... keys) {
		return jedis.watch(keys);
	}

	@Override
	public String unwatch() {
		return jedis.unwatch();
	}

	@Override
	public List<Object> exec() {
		if (isQueueing()) {
			synchronized (jedis) {
				if (isQueueing()) {
					return transaction.exec();
				}
			}
		}

		if (isPipelined()) {
			synchronized (jedis) {
				if (isPipelined()) {
					return pipeline.exec();
				}
			}
		}
		throw new RedisSystemException("No transaction or pipeline exists");
	}

	public boolean isPipelined() {
		return (pipeline != null && !pipeline.isClosed());
	}

	@Override
	public RedisPipeline<byte[], byte[]> pipelined() {
		if (!isPipelined()) {
			synchronized (jedis) {
				if (!isPipelined()) {
					if (isQueueing()) {
						throw new RedisSystemException("Pipes cannot be used in transactions");
					}

					if (isSubscribed()) {
						throw new RedisSystemException("Pipes cannot be used in subscriptions");
					}

					pipeline = new JedisPipeline(jedis.pipelined());
				}
			}
		}
		return pipeline;
	}

	@Override
	public boolean isSubscribed() {
		return (subscription != null && subscription.isAlive());
	}

	@Override
	public boolean isQueueing() {
		return transaction != null && transaction.isAlive();
	}

	@Override
	public Subscription<byte[], byte[]> getSubscription() {
		return isSubscribed() ? subscription : null;
	}

	@Override
	public void pSubscribe(MessageListener<byte[], byte[]> listener, byte[]... patterns) {
		if (isSubscribed()) {
			throw new RedisSubscribedConnectionException(
					"Connection already subscribed; use the connection Subscription to cancel or add new channels");
		}

		if (isQueueing() || isPipelined()) {
			throw new UnsupportedOperationException();
		}

		synchronized (jedis) {
			if (isSubscribed()) {
				throw new RedisSubscribedConnectionException(
						"Connection already subscribed; use the connection Subscription to cancel or add new channels");
			}

			if (isQueueing() || isPipelined()) {
				throw new UnsupportedOperationException();
			}

			BinaryJedisPubSub jedisPubSub = new JedisMessageListener(listener);
			this.subscription = new JedisSubscription(listener, jedisPubSub, null, patterns);
			jedis.psubscribe(jedisPubSub, patterns);
		}
	}

	@Override
	public Long publish(byte[] channel, byte[] message) {
		return jedis.publish(channel, message);
	}

	@Override
	public void subscribe(MessageListener<byte[], byte[]> listener, byte[]... channels) {
		if (isSubscribed()) {
			throw new RedisSubscribedConnectionException(
					"Connection already subscribed; use the connection Subscription to cancel or add new channels");
		}

		if (isQueueing() || isPipelined()) {
			throw new UnsupportedOperationException();
		}

		synchronized (jedis) {
			if (isSubscribed()) {
				throw new RedisSubscribedConnectionException(
						"Connection already subscribed; use the connection Subscription to cancel or add new channels");
			}

			if (isQueueing() || isPipelined()) {
				throw new UnsupportedOperationException();
			}

			BinaryJedisPubSub jedisPubSub = new JedisMessageListener(listener);
			this.subscription = new JedisSubscription(listener, jedisPubSub, channels, null);
			jedis.subscribe(jedisPubSub, channels);
		}
	}

	@Override
	public Long hdel(byte[] key, byte[]... fields) {
		return jedis.hdel(key, fields);
	}

	@Override
	public Boolean hexists(byte[] key, byte[] field) {
		return jedis.hexists(key, field);
	}

	@Override
	public byte[] hget(byte[] key, byte[] field) {
		return jedis.hget(key, field);
	}

	@Override
	public Map<byte[], byte[]> hgetall(byte[] key) {
		return jedis.hgetAll(key);
	}

	@Override
	public Long hincrby(byte[] key, byte[] field, long increment) {
		return jedis.hincrBy(key, field, increment);
	}

	@Override
	public Double hincrbyfloat(byte[] key, byte[] field, double increment) {
		return jedis.hincrByFloat(key, field, increment);
	}

	@Override
	public Set<byte[]> hkeys(byte[] key) {
		return jedis.hkeys(key);
	}

	@Override
	public Long hlen(byte[] key) {
		return jedis.hlen(key);
	}

	@Override
	public List<byte[]> hmget(byte[] key, byte[]... fields) {
		return jedis.hmget(key, fields);
	}

	@Override
	public String hmset(byte[] key, Map<byte[], byte[]> values) {
		return jedis.hmset(key, values);
	}

	@Override
	public List<byte[]> hrandfield(byte[] key, Integer count) {
		return jedis.hrandfield(key, count);
	}

	@Override
	public Map<byte[], byte[]> hrandfieldWithValue(byte[] key, Integer count) {
		return jedis.hrandfieldWithValues(key, count);
	}

	@Override
	public Long hset(byte[] key, Map<byte[], byte[]> values) {
		return jedis.hset(key, values);
	}

	@Override
	public Long hsetnx(byte[] key, byte[] field, byte[] value) {
		return jedis.hsetnx(key, field, value);
	}

	@Override
	public Long hstrlen(byte[] key, byte[] field) {
		return jedis.hstrlen(key, field);
	}

	@Override
	public List<byte[]> hvals(byte[] key) {
		return jedis.hvals(key);
	}

	@Override
	public List<byte[]> aclCat(byte[] categoryname) {
		if (categoryname == null) {
			return jedis.aclCatBinary();
		}
		return jedis.aclCat(categoryname);
	}

	@Override
	public Long aclDelUser(byte[] username, byte[]... usernames) {
		return jedis.aclDelUser(username, usernames);
	}

	@Override
	public String aclGenPass(Integer bits) {
		if (bits == null) {
			return jedis.aclGenPass();
		}
		return jedis.aclGenPass(bits);
	}

	@Override
	public List<byte[]> aclList() {
		return jedis.aclListBinary();
	}

	@Override
	public String aclLoad() {
		return jedis.aclLoad();
	}

	@Override
	public List<byte[]> aclLog(Integer count) {
		if (count == null) {
			return jedis.aclLogBinary();
		} else {
			return jedis.aclLogBinary(count);
		}
	}

	@Override
	public String aclLogReset() {
		return jedis.aclLogReset();
	}

	@Override
	public String aclSave() {
		return jedis.aclSave();
	}

	@Override
	public String aclSetuser(byte[] username, byte[]... rules) {
		return jedis.aclSetUser(username, rules);
	}

	@Override
	public List<byte[]> aclUsers() {
		return jedis.aclUsersBinary();
	}

	@Override
	public byte[] aclWhoami() {
		return jedis.aclWhoAmIBinary();
	}

	@Override
	public String bgrewriteaof() {
		return jedis.bgrewriteaof();
	}

	@Override
	public String bgsave() {
		return jedis.bgsave();
	}

	@Override
	public List<byte[]> configGet(byte[] parameter) {
		return jedis.configGet(parameter);
	}

	@Override
	public String configResetstat() {
		return jedis.configResetStat();
	}

	@Override
	public String configRewrite() {
		return jedis.configRewrite();
	}

	@Override
	public String configSet(byte[] parameter, byte[] value) {
		return jedis.configSet(parameter, value);
	}

	@Override
	public Long dbsize() {
		return jedis.dbSize();
	}

	@Override
	public String failoverAbort() {
		return jedis.failoverAbort();
	}

	@Override
	public String failover(FailoverParams params) {
		if (params == null) {
			return jedis.failover();
		}

		return jedis.failover(JedisUtils.toFailoverParams(params));
	}

	@Override
	public String flushall(FlushMode flushMode) {
		if (flushMode == null) {
			return jedis.flushAll();
		}

		return jedis.flushAll(JedisUtils.toFlushMode(flushMode));
	}

	@Override
	public String flushdb(FlushMode flushMode) {
		return flushMode == null ? jedis.flushDB() : jedis.flushDB(JedisUtils.toFlushMode(flushMode));
	}

	@Override
	public String info(String section) {
		return section == null ? jedis.info() : jedis.info(section);
	}

	@Override
	public Long lastsave() {
		return jedis.lastsave();
	}

	@Override
	public String memoryDoctor() {
		return jedis.memoryDoctor();
	}

	@Override
	public Long memoryUsage(byte[] key, int samples) {
		return jedis.memoryUsage(key, samples);
	}

	@Override
	public List<Module> moduleList() {
		List<redis.clients.jedis.Module> modules = jedis.moduleList();
		return modules == null ? Collections.emptyList()
				: modules.stream().map((e) -> new Module(e.getName(), e.getVersion())).collect(Collectors.toList());
	}

	@Override
	public String moduleLoad(String path) {
		return jedis.moduleLoad(path);
	}

	@Override
	public String moduleUnload(String name) {
		return jedis.moduleUnload(name);
	}

	@Override
	public List<Object> role() {
		return jedis.role();
	}

	@Override
	public String save() {
		return jedis.save();
	}

	@Override
	public void shutdown(SaveMode saveMode) {
		if (saveMode == null) {
			jedis.shutdown();
		} else {
			jedis.shutdown(JedisUtils.toSaveMode(saveMode));
		}
	}

	@Override
	public String slaveof(String host, int port) {
		return jedis.slaveof(host, port);
	}

	@Override
	public List<Slowlog> slowlogGet(Long count) {
		List<redis.clients.jedis.resps.Slowlog> list = count == null ? jedis.slowlogGet() : jedis.slowlogGet(count);
		return JedisUtils.toSlowlogList(list);
	}

	@Override
	public Long slowlogLen() {
		return jedis.slowlogLen();
	}

	@Override
	public String slowlogReset() {
		return jedis.slowlogReset();
	}

	@Override
	public String swapdb(int index1, int index2) {
		return jedis.swapDB(index1, index2);
	}

	@Override
	public List<String> time() {
		return jedis.time();
	}
}