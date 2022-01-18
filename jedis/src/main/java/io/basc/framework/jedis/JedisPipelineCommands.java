package io.basc.framework.jedis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.basc.framework.data.geo.Circle;
import io.basc.framework.data.geo.Distance;
import io.basc.framework.data.geo.Metric;
import io.basc.framework.data.geo.Point;
import io.basc.framework.redis.BitOP;
import io.basc.framework.redis.ClaimArgs;
import io.basc.framework.redis.Cursor;
import io.basc.framework.redis.DataType;
import io.basc.framework.redis.DefaultRedisResponse;
import io.basc.framework.redis.ExpireOption;
import io.basc.framework.redis.FlushMode;
import io.basc.framework.redis.GeoRadiusArgs;
import io.basc.framework.redis.GeoRadiusWith;
import io.basc.framework.redis.GeoWithin;
import io.basc.framework.redis.GeoaddOption;
import io.basc.framework.redis.InsertPosition;
import io.basc.framework.redis.MovePosition;
import io.basc.framework.redis.RedisAuth;
import io.basc.framework.redis.RedisPipelineCommands;
import io.basc.framework.redis.RedisResponse;
import io.basc.framework.redis.RedisValueEncoding;
import io.basc.framework.redis.ScanOptions;
import io.basc.framework.redis.SetOption;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.Response;
import redis.clients.jedis.commands.PipelineBinaryCommands;
import redis.clients.jedis.resps.GeoRadiusResponse;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Map<byte[], byte[]>> hrandfieldWithValue(byte[] key, Integer count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> hset(byte[] key, Map<byte[], byte[]> values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Boolean> hsetnx(byte[] key, byte[] field, byte[] value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> hstrlen(byte[] key, byte[] field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<List<byte[]>> hvals(byte[] key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> pfadd(byte[] key, byte[]... elements) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> pfcount(byte[]... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Boolean> pfmerge(byte[] destKey, byte[]... sourceKeys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Boolean> copy(byte[] source, byte[] destination, Integer destinationDB, boolean replace) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> del(byte[]... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<byte[]> dump(byte[] key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> exists(byte[]... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> expire(byte[] key, long seconds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> expireAt(byte[] key, long timestamp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Set<byte[]>> keys(byte[] pattern) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<String> migrate(String host, int port, byte[] key, int targetDB, int timeout) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<String> migrate(String host, int port, int targetDB, int timeout, boolean copy,
			boolean replace, RedisAuth auth, byte[]... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> move(byte[] key, int targetDB) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> objectRefCount(byte[] key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<RedisValueEncoding> objectEncoding(byte[] key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> objectIdletime(byte[] key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> objectFreq(byte[] key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> persist(byte[] key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> pexpire(byte[] key, long milliseconds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> pexpireAt(byte[] key, long timestamp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> pttl(byte[] key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<byte[]> randomkey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<String> rename(byte[] key, byte[] newKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Boolean> renamenx(byte[] key, byte[] newKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<String> restore(byte[] key, long ttl, byte[] serializedValue, boolean replace, boolean absTtl,
			Long idleTime, Long frequency) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Cursor<byte[]>> scan(long cursorId, ScanOptions<byte[]> options) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> touch(byte[]... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> ttl(byte[] key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<DataType> type(byte[] key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> unlink(byte[]... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> wait(int numreplicas, long timeout) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<byte[]> blmove(byte[] sourceKey, byte[] destinationKey, MovePosition from, MovePosition to,
			long timout) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<List<byte[]>> blpop(double timeout, byte[]... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<List<byte[]>> brpop(double timeout, byte[]... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<byte[]> brpoplpush(byte[] sourceKey, byte[] destinationKey, double timout) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<byte[]> lindex(byte[] key, long index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> linsert(byte[] key, InsertPosition position, byte[] pivot, byte[] value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> llen(byte[] key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<byte[]> lmove(byte[] sourceKey, byte[] destinationKey, MovePosition from, MovePosition to) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<List<byte[]>> lpop(byte[] key, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> lpush(byte[] key, byte[]... elements) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> lpushx(byte[] key, byte[]... elements) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<List<byte[]>> lrange(byte[] key, long start, long stop) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> lrem(byte[] key, int count, byte[] element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Boolean> lset(byte[] key, long index, byte[] element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Boolean> ltrim(byte[] key, long start, long stop) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<List<byte[]>> rpop(byte[] key, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<byte[]> rpoplpush(byte[] sourceKey, byte[] destinationKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> rpush(byte[] key, byte[]... elements) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> rpushx(byte[] key, byte[]... elements) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> RedisResponse<T> eval(byte[] script, List<byte[]> keys, List<byte[]> args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> RedisResponse<T> evalsha(byte[] sha1, List<byte[]> keys, List<byte[]> args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<List<Boolean>> scriptexists(byte[]... sha1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<String> scriptFlush() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<String> scriptFlush(FlushMode flushMode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<String> scriptKill() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<byte[]> scriptLoad(byte[] script) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> sadd(byte[] key, byte[]... members) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> scard(byte[] key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Set<byte[]>> sdiff(byte[]... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> sdiffstore(byte[] destinationKey, byte[]... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Set<byte[]>> sinter(byte[]... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> sinterstore(byte[] destinationKey, byte[]... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Boolean> sismember(byte[] key, byte[] member) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Set<byte[]>> smembers(byte[] key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<List<Boolean>> smismember(byte[] key, byte[]... members) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Boolean> sMove(byte[] sourceKey, byte[] destinationKey, byte[] member) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Set<byte[]>> spop(byte[] key, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<List<byte[]>> srandmember(byte[] key, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> srem(byte[] key, byte[]... members) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Set<byte[]>> sunion(byte[]... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> sunionstore(byte[] destinationKey, byte[]... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Cursor<byte[]>> sScan(long cursorId, byte[] key, ScanOptions<byte[]> options) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> xack(byte[] key, byte[] group, byte[]... ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<List<byte[]>> xclaim(byte[] key, byte[] group, byte[] consumer, long minIdleTime,
			ClaimArgs args, byte[]... ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> xdel(byte[] key, byte[]... ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> append(byte[] key, byte[] value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> bitcount(byte[] key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> bitop(BitOP op, byte[] destkey, byte[]... srcKeys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> bitpos(byte[] key, boolean bit, Long start, Long end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> decr(byte[] key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> decrBy(byte[] key, long decrement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<byte[]> get(byte[] key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Boolean> getbit(byte[] key, Long offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<byte[]> getdel(byte[] key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<byte[]> getEx(byte[] key, ExpireOption option, Long time) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<byte[]> getrange(byte[] key, long startOffset, long endOffset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<byte[]> getset(byte[] key, byte[] value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> incr(byte[] key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> incrBy(byte[] key, long increment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Double> incrByFloat(byte[] key, double increment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<List<byte[]>> mget(byte[]... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Boolean> mset(Map<byte[], byte[]> pairs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Long> msetnx(Map<byte[], byte[]> pairs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Boolean> psetex(byte[] key, long milliseconds, byte[] value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<String> set(byte[] key, byte[] value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Boolean> set(byte[] key, byte[] value, ExpireOption option, long time, SetOption setOption) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Boolean> setbit(byte[] key, long offset, boolean value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Boolean> setex(byte[] key, long seconds, byte[] value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Boolean> setNX(byte[] key, byte[] value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Boolean> setrange(byte[] key, Long offset, byte[] value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisResponse<Boolean> strlen(byte[] key) {
		// TODO Auto-generated method stub
		return null;
	}
}
