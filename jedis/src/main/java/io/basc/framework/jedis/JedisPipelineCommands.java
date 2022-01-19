package io.basc.framework.jedis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.basc.framework.convert.lang.NumberToBooleanConverter;
import io.basc.framework.data.geo.Circle;
import io.basc.framework.data.geo.Distance;
import io.basc.framework.data.geo.Metric;
import io.basc.framework.data.geo.Point;
import io.basc.framework.redis.BitOP;
import io.basc.framework.redis.ClaimArgs;
import io.basc.framework.redis.DataType;
import io.basc.framework.redis.DefaultRedisResponse;
import io.basc.framework.redis.ExpireOption;
import io.basc.framework.redis.GeoRadiusArgs;
import io.basc.framework.redis.GeoRadiusWith;
import io.basc.framework.redis.GeoWithin;
import io.basc.framework.redis.GeoaddOption;
import io.basc.framework.redis.InsertPosition;
import io.basc.framework.redis.MovePosition;
import io.basc.framework.redis.RedisPipelineCommands;
import io.basc.framework.redis.RedisResponse;
import io.basc.framework.redis.RedisValueEncoding;
import io.basc.framework.redis.RedisValueEncodings;
import io.basc.framework.redis.ScanOptions;
import io.basc.framework.redis.SetOption;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.page.Pageable;
import io.basc.framework.util.page.SharedPageable;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.Response;
import redis.clients.jedis.commands.PipelineBinaryCommands;
import redis.clients.jedis.resps.GeoRadiusResponse;
import redis.clients.jedis.resps.ScanResult;
import redis.clients.jedis.util.SafeEncoder;

public class JedisPipelineCommands implements RedisPipelineCommands<byte[], byte[]> {
	private final PipelineBinaryCommands commands;

	public JedisPipelineCommands(PipelineBinaryCommands commands) {
		this.commands = commands;
	}

	@Override
	public RedisResponse<Long> geoadd(byte[] key, GeoaddOption option, Map<byte[], Point> members) {
		Response<Long> response = commands.geoadd(key, JedisUtils.toGeoAddParams(option),
				JedisUtils.toMemberCoordinateMap(members));
		return new DefaultRedisResponse<>(() -> response.get());
	}

	@Override
	public RedisResponse<Double> geodist(byte[] key, byte[] member1, byte[] member2, Metric metric) {
		Response<Double> response = commands.geodist(key, member1, member2, JedisUtils.toGeoUnit(metric));
		return new DefaultRedisResponse<>(() -> response.get());
	}

	@Override
	public RedisResponse<List<String>> geohash(byte[] key, byte[]... members) {
		Response<List<byte[]>> response = commands.geohash(key, members);
		return new DefaultRedisResponse<>(
				() -> JedisCodec.INSTANCE.toDecodeConverter().convert(response.get(), new ArrayList<String>()));
	}

	@Override
	public RedisResponse<List<Point>> geopos(byte[] key, byte[]... members) {
		Response<List<GeoCoordinate>> response = commands.geopos(key, members);
		return new DefaultRedisResponse<List<Point>>(() -> JedisUtils.toPoints(response.get()));
	}

	@Override
	public RedisResponse<Collection<byte[]>> georadius(byte[] key, Circle within, GeoRadiusArgs<byte[]> args) {
		Response<List<GeoRadiusResponse>> response = commands.georadius(key, within.getPoint().getX(),
				within.getPoint().getY(), within.getRadius().getValue(),
				JedisUtils.toGeoUnit(within.getRadius().getMetric()), JedisUtils.toGeoRadiusParam(null, args));
		return new DefaultRedisResponse<Collection<byte[]>>(() -> JedisUtils.toGeoMembers(response.get()));
	}

	@Override
	public RedisResponse<List<GeoWithin<byte[]>>> georadius(byte[] key, Circle within, GeoRadiusWith with,
			GeoRadiusArgs<byte[]> args) {
		Response<List<GeoRadiusResponse>> response = commands.georadius(key, within.getPoint().getX(),
				within.getPoint().getY(), within.getRadius().getValue(),
				JedisUtils.toGeoUnit(within.getRadius().getMetric()), JedisUtils.toGeoRadiusParam(null, args));
		return new DefaultRedisResponse<List<GeoWithin<byte[]>>>(() -> JedisUtils.toGeoWithins(response.get()));
	}

	@Override
	public RedisResponse<List<byte[]>> georadiusbymember(byte[] key, byte[] member, Distance distance,
			GeoRadiusArgs<byte[]> args) {
		Response<List<GeoRadiusResponse>> response = commands.georadiusByMember(key, member, distance.getValue(),
				JedisUtils.toGeoUnit(distance.getMetric()), JedisUtils.toGeoRadiusParam(null, args));
		return new DefaultRedisResponse<List<byte[]>>(() -> JedisUtils.toGeoMembers(response.get()));
	}

	@Override
	public RedisResponse<List<GeoWithin<byte[]>>> georadiusbymember(byte[] key, byte[] member, Distance distance,
			GeoRadiusWith with, GeoRadiusArgs<byte[]> args) {
		Response<List<GeoRadiusResponse>> response = commands.georadiusByMember(key, member, distance.getValue(),
				JedisUtils.toGeoUnit(distance.getMetric()), JedisUtils.toGeoRadiusParam(with, args));
		return new DefaultRedisResponse<List<GeoWithin<byte[]>>>(() -> JedisUtils.toGeoWithins(response.get()));
	}

	@Override
	public RedisResponse<Long> hdel(byte[] key, byte[]... fields) {
		Response<Long> response = commands.hdel(key, fields);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Boolean> hexists(byte[] key, byte[] field) {
		Response<Boolean> response = commands.hexists(key, field);
		return new DefaultRedisResponse<Boolean>(() -> response.get());
	}

	@Override
	public RedisResponse<byte[]> hget(byte[] key, byte[] field) {
		Response<byte[]> response = commands.hget(key, field);
		return new DefaultRedisResponse<byte[]>(() -> response.get());
	}

	@Override
	public RedisResponse<Map<byte[], byte[]>> hgetall(byte[] key) {
		Response<Map<byte[], byte[]>> response = commands.hgetAll(key);
		return new DefaultRedisResponse<Map<byte[], byte[]>>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> hincrby(byte[] key, byte[] field, long increment) {
		Response<Long> response = commands.hincrBy(key, field, increment);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Double> hincrbyfloat(byte[] key, byte[] field, double increment) {
		Response<Double> response = commands.hincrByFloat(key, field, increment);
		return new DefaultRedisResponse<Double>(() -> response.get());
	}

	@Override
	public RedisResponse<Set<byte[]>> hkeys(byte[] key) {
		Response<Set<byte[]>> response = commands.hkeys(key);
		return new DefaultRedisResponse<Set<byte[]>>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> hlen(byte[] key) {
		Response<Long> response = commands.hlen(key);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<List<byte[]>> hmget(byte[] key, byte[]... fields) {
		Response<List<byte[]>> response = commands.hmget(key, fields);
		return new DefaultRedisResponse<List<byte[]>>(() -> response.get());
	}

	@Override
	public RedisResponse<String> hmset(byte[] key, Map<byte[], byte[]> values) {
		Response<String> response = commands.hmset(key, values);
		return new DefaultRedisResponse<String>(() -> response.get());
	}

	@Override
	public RedisResponse<List<byte[]>> hrandfield(byte[] key, Integer count) {
		Response<List<byte[]>> response = commands.hrandfield(key, count);
		return new DefaultRedisResponse<List<byte[]>>(() -> response.get());
	}

	@Override
	public RedisResponse<Map<byte[], byte[]>> hrandfieldWithValue(byte[] key, Integer count) {
		Response<Map<byte[], byte[]>> response = commands.hrandfieldWithValues(key, count);
		return new DefaultRedisResponse<Map<byte[], byte[]>>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> hset(byte[] key, Map<byte[], byte[]> values) {
		Response<Long> response = commands.hset(key, values);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Boolean> hsetnx(byte[] key, byte[] field, byte[] value) {
		Response<Long> response = commands.hsetnx(key, field, value);
		return new DefaultRedisResponse<Boolean>(() -> NumberToBooleanConverter.DEFAULT.convert(response.get()));
	}

	@Override
	public RedisResponse<Long> hstrlen(byte[] key, byte[] field) {
		Response<Long> response = commands.hstrlen(key, field);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<List<byte[]>> hvals(byte[] key) {
		Response<List<byte[]>> response = commands.hvals(key);
		return new DefaultRedisResponse<List<byte[]>>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> pfadd(byte[] key, byte[]... elements) {
		Response<Long> response = commands.pfadd(key, elements);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> pfcount(byte[]... keys) {
		Response<Long> response = commands.pfcount(keys);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<String> pfmerge(byte[] destKey, byte[]... sourceKeys) {
		Response<String> response = commands.pfmerge(destKey, sourceKeys);
		return new DefaultRedisResponse<String>(() -> response.get());
	}

	@Override
	public RedisResponse<Boolean> copy(byte[] source, byte[] destination, boolean replace) {
		Response<Boolean> response = commands.copy(source, destination, replace);
		return new DefaultRedisResponse<Boolean>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> del(byte[]... keys) {
		Response<Long> response = commands.del(keys);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<byte[]> dump(byte[] key) {
		Response<byte[]> response = commands.dump(key);
		return new DefaultRedisResponse<byte[]>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> exists(byte[]... keys) {
		Response<Long> response = commands.exists(keys);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> expire(byte[] key, long seconds) {
		Response<Long> response = commands.expire(key, seconds);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> expireAt(byte[] key, long timestamp) {
		Response<Long> response = commands.expireAt(key, timestamp);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Set<byte[]>> keys(byte[] pattern) {
		Response<Set<byte[]>> response = commands.hkeys(pattern);
		return new DefaultRedisResponse<Set<byte[]>>(() -> response.get());
	}

	@Override
	public RedisResponse<String> migrate(String host, int port, byte[] key, int timeout) {
		Response<String> response = commands.migrate(host, port, key, timeout);
		return new DefaultRedisResponse<String>(() -> response.get());
	}

	@Override
	public RedisResponse<String> migrate(String host, int port, int timeout,
			io.basc.framework.redis.MigrateParams option, byte[]... keys) {
		Response<String> response = commands.migrate(host, port, timeout, JedisUtils.toMigrateParams(option), keys);
		return new DefaultRedisResponse<String>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> objectRefCount(byte[] key) {
		Response<Long> response = commands.objectRefcount(key);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<RedisValueEncoding> objectEncoding(byte[] key) {
		Response<byte[]> response = commands.objectEncoding(key);
		return new DefaultRedisResponse<RedisValueEncoding>(() -> {
			RedisValueEncoding encoding = RedisValueEncoding.of(SafeEncoder.encode(response.get()));
			return encoding == null ? RedisValueEncodings.VACANT : encoding;
		});
	}

	@Override
	public RedisResponse<Long> objectIdletime(byte[] key) {
		Response<Long> response = commands.objectIdletime(key);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> objectFreq(byte[] key) {
		Response<Long> response = commands.objectFreq(key);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> persist(byte[] key) {
		Response<Long> response = commands.persist(key);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> pexpire(byte[] key, long milliseconds) {
		Response<Long> response = commands.pexpire(key, milliseconds);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> pexpireAt(byte[] key, long timestamp) {
		Response<Long> response = commands.pexpireAt(key, timestamp);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> pttl(byte[] key) {
		Response<Long> response = commands.pttl(key);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<byte[]> randomkey() {
		Response<byte[]> response = commands.randomBinaryKey();
		return new DefaultRedisResponse<byte[]>(() -> response.get());
	}

	@Override
	public RedisResponse<String> rename(byte[] key, byte[] newKey) {
		Response<String> response = commands.rename(key, newKey);
		return new DefaultRedisResponse<String>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> renamenx(byte[] key, byte[] newKey) {
		Response<Long> response = commands.renamenx(key, newKey);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<String> restore(byte[] key, long ttl, byte[] serializedValue,
			io.basc.framework.redis.RestoreParams params) {
		Response<String> response = commands.restore(key, ttl, serializedValue, JedisUtils.toRestoreParams(params));
		return new DefaultRedisResponse<String>(() -> response.get());
	}

	@Override
	public RedisResponse<Pageable<Long, byte[]>> scan(long cursorId, ScanOptions<byte[]> options) {
		Response<ScanResult<byte[]>> response = commands.scan(SafeEncoder.encode(String.valueOf(cursorId)),
				JedisUtils.toScanParams(options));
		return new DefaultRedisResponse<Pageable<Long, byte[]>>(() -> {
			ScanResult<byte[]> result = response.get();
			String next = result.getCursor();
			return new SharedPageable<Long, byte[]>(cursorId, result.getResult(),
					StringUtils.isEmpty(next) ? null : Long.parseLong(next));
		});
	}

	@Override
	public RedisResponse<Long> touch(byte[]... keys) {
		Response<Long> response = commands.touch(keys);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> ttl(byte[] key) {
		Response<Long> response = commands.ttl(key);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<DataType> type(byte[] key) {
		Response<String> response = commands.type(key);
		return new DefaultRedisResponse<DataType>(() -> {
			String type = response.get();
			return type == null ? null : DataType.fromCode(type);
		});
	}

	@Override
	public RedisResponse<Long> unlink(byte[]... keys) {
		Response<Long> response = commands.unlink(keys);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<byte[]> blmove(byte[] sourceKey, byte[] destinationKey, MovePosition from, MovePosition to,
			long timout) {
		Response<byte[]> response = commands.blmove(sourceKey, destinationKey, JedisUtils.toListDirection(from),
				JedisUtils.toListDirection(to), timout);
		return new DefaultRedisResponse<byte[]>(() -> response.get());
	}

	@Override
	public RedisResponse<List<byte[]>> blpop(double timeout, byte[]... keys) {
		Response<List<byte[]>> response = commands.blpop(timeout, keys);
		return new DefaultRedisResponse<List<byte[]>>(() -> response.get());
	}

	@Override
	public RedisResponse<List<byte[]>> brpop(double timeout, byte[]... keys) {
		Response<List<byte[]>> response = commands.brpop(timeout, keys);
		return new DefaultRedisResponse<List<byte[]>>(() -> response.get());
	}

	@Override
	public RedisResponse<byte[]> brpoplpush(byte[] sourceKey, byte[] destinationKey, double timeout) {
		Assert.isTrue(timeout <= Integer.MAX_VALUE && timeout >= 0);
		Response<byte[]> response = commands.brpoplpush(sourceKey, destinationKey, (int) timeout);
		return new DefaultRedisResponse<byte[]>(() -> response.get());
	}

	@Override
	public RedisResponse<byte[]> lindex(byte[] key, long index) {
		Response<byte[]> response = commands.lindex(key, index);
		return new DefaultRedisResponse<byte[]>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> linsert(byte[] key, InsertPosition position, byte[] pivot, byte[] value) {
		Response<Long> response = commands.linsert(key, JedisUtils.toListPosition(position), pivot, value);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> llen(byte[] key) {
		Response<Long> response = commands.llen(key);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<byte[]> lmove(byte[] sourceKey, byte[] destinationKey, MovePosition from, MovePosition to) {
		Response<byte[]> response = commands.lmove(sourceKey, destinationKey, JedisUtils.toListDirection(from),
				JedisUtils.toListDirection(to));
		return new DefaultRedisResponse<byte[]>(() -> response.get());
	}

	@Override
	public RedisResponse<List<byte[]>> lpop(byte[] key, int count) {
		Response<List<byte[]>> response = commands.lpop(key, count);
		return new DefaultRedisResponse<List<byte[]>>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> lpush(byte[] key, byte[]... elements) {
		Response<Long> response = commands.lpush(key, elements);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> lpushx(byte[] key, byte[]... elements) {
		Response<Long> response = commands.lpushx(key, elements);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<List<byte[]>> lrange(byte[] key, long start, long stop) {
		Response<List<byte[]>> response = commands.lrange(key, start, stop);
		return new DefaultRedisResponse<List<byte[]>>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> lrem(byte[] key, int count, byte[] element) {
		Response<Long> response = commands.lrem(key, count, element);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<String> lset(byte[] key, long index, byte[] element) {
		Response<String> response = commands.lset(key, index, element);
		return new DefaultRedisResponse<>(() -> response.get());
	}

	@Override
	public RedisResponse<String> ltrim(byte[] key, long start, long stop) {
		Response<String> response = commands.ltrim(key, start, stop);
		return new DefaultRedisResponse<String>(() -> response.get());
	}

	@Override
	public RedisResponse<List<byte[]>> rpop(byte[] key, int count) {
		Response<List<byte[]>> response = commands.rpop(key, count);
		return new DefaultRedisResponse<List<byte[]>>(() -> response.get());
	}

	@Override
	public RedisResponse<byte[]> rpoplpush(byte[] sourceKey, byte[] destinationKey) {
		Response<byte[]> response = commands.rpoplpush(sourceKey, destinationKey);
		return new DefaultRedisResponse<byte[]>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> rpush(byte[] key, byte[]... elements) {
		Response<Long> response = commands.rpush(key, elements);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> rpushx(byte[] key, byte[]... elements) {
		Response<Long> response = commands.rpushx(key, elements);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> RedisResponse<T> eval(byte[] script, List<byte[]> keys, List<byte[]> args) {
		Response<Object> response = commands.eval(script, keys, args);
		return new DefaultRedisResponse<T>(() -> (T) response.get());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> RedisResponse<T> evalsha(byte[] sha1, List<byte[]> keys, List<byte[]> args) {
		Response<Object> response = commands.evalsha(sha1, keys, args);
		return new DefaultRedisResponse<>(() -> (T) response.get());
	}

	@Override
	public RedisResponse<Long> sadd(byte[] key, byte[]... members) {
		Response<Long> response = commands.sadd(key, members);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> scard(byte[] key) {
		Response<Long> response = commands.scard(key);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Set<byte[]>> sdiff(byte[]... keys) {
		Response<Set<byte[]>> response = commands.sdiff(keys);
		return new DefaultRedisResponse<Set<byte[]>>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> sdiffstore(byte[] destinationKey, byte[]... keys) {
		Response<Long> response = commands.sdiffstore(destinationKey, keys);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Set<byte[]>> sinter(byte[]... keys) {
		Response<Set<byte[]>> response = commands.sinter(keys);
		return new DefaultRedisResponse<Set<byte[]>>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> sinterstore(byte[] destinationKey, byte[]... keys) {
		Response<Long> response = commands.sinterstore(destinationKey, keys);
		return new DefaultRedisResponse<>(() -> response.get());
	}

	@Override
	public RedisResponse<Boolean> sismember(byte[] key, byte[] member) {
		Response<Boolean> response = commands.sismember(key, member);
		return new DefaultRedisResponse<Boolean>(() -> response.get());
	}

	@Override
	public RedisResponse<Set<byte[]>> smembers(byte[] key) {
		Response<Set<byte[]>> response = commands.smembers(key);
		return new DefaultRedisResponse<Set<byte[]>>(() -> response.get());
	}

	@Override
	public RedisResponse<List<Boolean>> smismember(byte[] key, byte[]... members) {
		Response<List<Boolean>> response = commands.smismember(key, members);
		return new DefaultRedisResponse<List<Boolean>>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> sMove(byte[] sourceKey, byte[] destinationKey, byte[] member) {
		Response<Long> response = commands.smove(sourceKey, destinationKey, member);
		return new DefaultRedisResponse<>(() -> response.get());
	}

	@Override
	public RedisResponse<Set<byte[]>> spop(byte[] key, int count) {
		Response<Set<byte[]>> response = commands.spop(key, count);
		return new DefaultRedisResponse<Set<byte[]>>(() -> response.get());
	}

	@Override
	public RedisResponse<List<byte[]>> srandmember(byte[] key, int count) {
		Response<List<byte[]>> response = commands.srandmember(key, count);
		return new DefaultRedisResponse<List<byte[]>>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> srem(byte[] key, byte[]... members) {
		Response<Long> response = commands.srem(key, members);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Set<byte[]>> sunion(byte[]... keys) {
		Response<Set<byte[]>> response = commands.sunion(keys);
		return new DefaultRedisResponse<Set<byte[]>>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> sunionstore(byte[] destinationKey, byte[]... keys) {
		Response<Long> response = commands.sunionstore(destinationKey, keys);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Pageable<Long, byte[]>> sScan(long cursorId, byte[] key, ScanOptions<byte[]> options) {
		Response<ScanResult<byte[]>> response = commands.scan(SafeEncoder.encode(String.valueOf(cursorId)),
				JedisUtils.toScanParams(options));
		return new DefaultRedisResponse<>(() -> {
			ScanResult<byte[]> result = response.get();
			return new SharedPageable<Long, byte[]>(cursorId, result.getResult(), Long.parseLong(result.getCursor()));
		});
	}

	@Override
	public RedisResponse<Long> xack(byte[] key, byte[] group, byte[]... ids) {
		Response<Long> response = commands.xack(key, group, ids);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<List<byte[]>> xclaim(byte[] key, byte[] group, byte[] consumer, long minIdleTime,
			ClaimArgs args, byte[]... ids) {
		Response<List<byte[]>> response = commands.xclaim(key, group, consumer, minIdleTime,
				JedisUtils.toXClaimParams(args), ids);
		return new DefaultRedisResponse<List<byte[]>>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> xdel(byte[] key, byte[]... ids) {
		Response<Long> response = commands.xdel(key, ids);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> append(byte[] key, byte[] value) {
		Response<Long> response = commands.append(key, value);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> bitcount(byte[] key, long start, long end) {
		Response<Long> response = commands.bitcount(key, start, end);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> bitop(BitOP op, byte[] destkey, byte[]... srcKeys) {
		Response<Long> response = commands.bitop(JedisUtils.toBitOP(op), destkey, srcKeys);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> bitpos(byte[] key, boolean bit, Long start, Long end) {
		Response<Long> response = commands.bitpos(key, bit, JedisUtils.toBitPosParams(start, end));
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> decr(byte[] key) {
		Response<Long> response = commands.decr(key);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> decrBy(byte[] key, long decrement) {
		Response<Long> response = commands.decrBy(key, decrement);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<byte[]> get(byte[] key) {
		Response<byte[]> response = commands.get(key);
		return new DefaultRedisResponse<byte[]>(() -> response.get());
	}

	@Override
	public RedisResponse<Boolean> getbit(byte[] key, Long offset) {
		Response<Boolean> response = commands.getbit(key, offset);
		return new DefaultRedisResponse<Boolean>(() -> response.get());
	}

	@Override
	public RedisResponse<byte[]> getdel(byte[] key) {
		Response<byte[]> response = commands.getDel(key);
		return new DefaultRedisResponse<byte[]>(() -> response.get());
	}

	@Override
	public RedisResponse<byte[]> getEx(byte[] key, ExpireOption option, Long time) {
		Response<byte[]> response = commands.getEx(key, JedisUtils.toGetExParams(option, time));
		return new DefaultRedisResponse<byte[]>(() -> response.get());
	}

	@Override
	public RedisResponse<byte[]> getrange(byte[] key, long startOffset, long endOffset) {
		Response<byte[]> response = commands.getrange(key, startOffset, endOffset);
		return new DefaultRedisResponse<byte[]>(() -> response.get());
	}

	@Override
	public RedisResponse<byte[]> getset(byte[] key, byte[] value) {
		Response<byte[]> response = commands.getSet(key, value);
		return new DefaultRedisResponse<byte[]>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> incr(byte[] key) {
		Response<Long> response = commands.incr(key);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> incrBy(byte[] key, long increment) {
		Response<Long> response = commands.incrBy(key, increment);
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<Double> incrByFloat(byte[] key, double increment) {
		Response<Double> response = commands.incrByFloat(key, increment);
		return new DefaultRedisResponse<Double>(() -> response.get());
	}

	@Override
	public RedisResponse<List<byte[]>> mget(byte[]... keys) {
		Response<List<byte[]>> response = commands.mget(keys);
		return new DefaultRedisResponse<List<byte[]>>(() -> response.get());
	}

	@Override
	public RedisResponse<String> mset(Map<byte[], byte[]> pairs) {
		Response<String> response = commands.mset(JedisUtils.toPairsArgs(pairs));
		return new DefaultRedisResponse<>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> msetnx(Map<byte[], byte[]> pairs) {
		Response<Long> response = commands.msetnx(JedisUtils.toPairsArgs(pairs));
		return new DefaultRedisResponse<Long>(() -> response.get());
	}

	@Override
	public RedisResponse<String> psetex(byte[] key, long milliseconds, byte[] value) {
		Response<String> response = commands.psetex(key, milliseconds, value);
		return new DefaultRedisResponse<>(() -> response.get());
	}

	@Override
	public RedisResponse<String> set(byte[] key, byte[] value) {
		Response<String> response = commands.set(key, value);
		return new DefaultRedisResponse<String>(() -> response.get());
	}

	@Override
	public RedisResponse<String> set(byte[] key, byte[] value, ExpireOption option, long time, SetOption setOption) {
		Response<String> response = commands.set(key, value, JedisUtils.toSetParams(option, time, setOption));
		return new DefaultRedisResponse<>(() -> response.get());
	}

	@Override
	public RedisResponse<Boolean> setbit(byte[] key, long offset, boolean value) {
		Response<Boolean> response = commands.setbit(key, offset, value);
		return new DefaultRedisResponse<Boolean>(() -> response.get());
	}

	@Override
	public RedisResponse<String> setex(byte[] key, long seconds, byte[] value) {
		Response<String> response = commands.setex(key, seconds, value);
		return new DefaultRedisResponse<>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> setNX(byte[] key, byte[] value) {
		Response<Long> response = commands.setnx(key, value);
		return new DefaultRedisResponse<>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> setrange(byte[] key, Long offset, byte[] value) {
		Response<Long> response = commands.setrange(key, offset, value);
		return new DefaultRedisResponse<>(() -> response.get());
	}

	@Override
	public RedisResponse<Long> strlen(byte[] key) {
		Response<Long> response = commands.strlen(key);
		return new DefaultRedisResponse<>(() -> response.get());
	}
}