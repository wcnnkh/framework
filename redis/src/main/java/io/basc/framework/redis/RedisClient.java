package io.basc.framework.redis;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.basc.framework.codec.Codec;
import io.basc.framework.data.domain.Range;
import io.basc.framework.data.geo.Circle;
import io.basc.framework.data.geo.Distance;
import io.basc.framework.data.geo.Metric;
import io.basc.framework.data.geo.Point;
import io.basc.framework.redis.convert.DefaultConvertibleRedisClient;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.page.Pageables;
import io.basc.framework.util.page.StreamPageables;

@SuppressWarnings("unchecked")
public interface RedisClient<K, V> extends RedisConnectionCommands<K, V>, RedisGeoCommands<K, V>,
		RedisHashesCommands<K, V>, RedisHyperloglogCommands<K, V>, RedisKeysCommands<K, V>, RedisListsCommands<K, V>,
		RedisScriptingCommands<K, V>, RedisSortedSetsCommands<K, V>, RedisStreamsCommands<K, V>,
		RedisStringCommands<K, V>, RedisSetsCommands<K, V>, RedisServerCommands<K, V> {

	RedisConnection<K, V> getConnection();

	default <TK, TV> RedisClient<TK, TV> to(Codec<TK, K> keyCodec, Codec<TV, V> valueCodec) {
		return new DefaultConvertibleRedisClient<>(this, keyCodec, valueCodec);
	}

	default <T> T execute(RedisCallback<K, V, T> callback) throws RedisSystemException {
		RedisConnection<K, V> connection = getConnection();
		try {
			return callback.doInRedis(connection);
		} finally {
			connection.close();
		}
	}

	@Override
	default V ping(K message) {
		return execute((commands) -> {
			return commands.ping(message);
		});
	}

	@Override
	default String select(int index) {
		return execute((commands) -> {
			return commands.select(index);
		});
	}

	@Override
	default Long geoadd(K key, GeoaddOption option, Map<V, Point> members) {
		return execute((commands) -> {
			return commands.geoadd(key, option, members);
		});
	}

	@Override
	default Double geodist(K key, V member1, V member2, Metric metric) {
		return execute((commands) -> {
			return commands.geodist(key, member1, member2, metric);
		});
	}

	@Override
	default List<String> geohash(K key, V... members) {
		return execute((commands) -> {
			return commands.geohash(key, members);
		});
	}

	@Override
	default List<Point> geopos(K key, V... members) {
		return execute((commands) -> {
			return commands.geopos(key, members);
		});
	}

	@Override
	default Collection<V> georadius(K key, Circle within, GeoRadiusArgs<K> args) {
		return execute((commands) -> {
			return commands.georadius(key, within, args);
		});
	}

	@Override
	default List<GeoWithin<V>> georadius(K key, Circle within, GeoRadiusWith with, GeoRadiusArgs<K> args) {
		return execute((commands) -> {
			return commands.georadius(key, within, with, args);
		});
	}

	@Override
	default List<V> georadiusbymember(K key, V member, Distance distance, GeoRadiusArgs<K> args) {
		return execute((commands) -> {
			return commands.georadiusbymember(key, member, distance, args);
		});
	}

	@Override
	default List<GeoWithin<V>> georadiusbymember(K key, V member, Distance distance, GeoRadiusWith with,
			GeoRadiusArgs<K> args) {
		return execute((commands) -> {
			return commands.georadiusbymember(key, member, distance, with, args);
		});
	}

	@Override
	default Long hdel(K key, K... fields) {
		return execute((commands) -> {
			return commands.hdel(key, fields);
		});
	}

	@Override
	default Boolean hexists(K key, K field) {
		return execute((commands) -> {
			return commands.hexists(key, field);
		});
	}

	@Override
	default V hget(K key, K field) {
		return execute((commands) -> {
			return commands.hget(key, field);
		});
	}

	@Override
	default Map<K, V> hgetall(K key) {
		return execute((commands) -> {
			return commands.hgetall(key);
		});
	}

	@Override
	default Long hincrby(K key, K field, long increment) {
		return execute((commands) -> {
			return commands.hincrby(key, field, increment);
		});
	}

	@Override
	default Double hincrbyfloat(K key, K field, double increment) {
		return execute((commands) -> {
			return commands.hincrbyfloat(key, field, increment);
		});
	}

	@Override
	default Set<K> hkeys(K key) {
		return execute((commands) -> {
			return commands.hkeys(key);
		});
	}

	@Override
	default Long hlen(K key) {
		return execute((commands) -> {
			return commands.hlen(key);
		});
	}

	@Override
	default List<V> hmget(K key, K... fields) {
		return execute((commands) -> {
			return commands.hmget(key, fields);
		});
	}

	@Override
	default String hmset(K key, Map<K, V> values) {
		return execute((commands) -> {
			return commands.hmset(key, values);
		});
	}

	@Override
	default List<K> hrandfield(K key, Integer count) {
		return execute((commands) -> {
			return commands.hrandfield(key, count);
		});
	}

	@Override
	default Map<K, V> hrandfieldWithValue(K key, Integer count) {
		return execute((commands) -> {
			return commands.hrandfieldWithValue(key, count);
		});
	}

	@Override
	default Long hset(K key, Map<K, V> values) {
		return execute((commands) -> {
			return commands.hset(key, values);
		});
	}

	@Override
	default Long hsetnx(K key, K field, V value) {
		return execute((commands) -> {
			return commands.hsetnx(key, field, value);
		});
	}

	@Override
	default Long hstrlen(K key, K field) {
		return execute((commands) -> {
			return commands.hstrlen(key, field);
		});
	}

	@Override
	default List<V> hvals(K key) {
		return execute((commands) -> {
			return commands.hvals(key);
		});
	}

	@Override
	default Long pfadd(K key, V... elements) {
		return execute((commands) -> {
			return commands.pfadd(key, elements);
		});
	}

	@Override
	default Long pfcount(K... keys) {
		return execute((commands) -> {
			return commands.pfcount(keys);
		});
	}

	@Override
	default String pfmerge(K destKey, K... sourceKeys) {
		return execute((commands) -> {
			return commands.pfmerge(destKey, sourceKeys);
		});
	}

	@Override
	default Boolean copy(K source, K destination, Integer destinationDB, boolean replace) {
		return execute((commands) -> {
			return commands.copy(source, destination, destinationDB, replace);
		});
	}

	@Override
	default Long del(K... keys) {
		return execute((commands) -> {
			return commands.del(keys);
		});
	}

	@Override
	default V dump(K key) {
		return execute((commands) -> {
			return commands.dump(key);
		});
	}

	@Override
	default Long exists(K... keys) {
		return execute((commands) -> {
			return commands.exists(keys);
		});
	}

	@Override
	default Long expire(K key, long seconds) {
		return execute((commands) -> {
			return commands.expire(key, seconds);
		});
	}

	default Long expire(Collection<K> keys, long time, TimeUnit timeUnit) {
		if (CollectionUtils.isEmpty(keys)) {
			return 0L;
		}

		return execute((commands) -> {
			Long value = null;
			for (K key : keys) {
				Long v = commands.pexpire(key, timeUnit.toMillis(time));
				if (v != null) {
					value = value == null ? 0L : (value + v);
				}
			}
			return value;
		});
	}

	@Override
	default Long expireAt(K key, long timestamp) {
		return execute((commands) -> {
			return commands.expireAt(key, timestamp);
		});
	}

	@Override
	default Set<K> keys(K pattern) {
		return execute((commands) -> {
			return commands.keys(pattern);
		});
	}

	@Override
	default String migrate(String host, int port, K key, int targetDB, int timeout) {
		return execute((commands) -> {
			return commands.migrate(host, port, key, targetDB, timeout);
		});
	}

	@Override
	default String migrate(String host, int port, int targetDB, int timeout, MigrateParams option, K... keys) {
		return execute((commands) -> {
			return commands.migrate(host, port, targetDB, timeout, option, keys);
		});
	}

	@Override
	default Long move(K key, int targetDB) {
		return execute((commands) -> {
			return commands.move(key, targetDB);
		});
	}

	@Override
	default Long objectRefCount(K key) {
		return execute((commands) -> {
			return commands.objectRefCount(key);
		});
	}

	@Override
	default RedisValueEncoding objectEncoding(K key) {
		return execute((commands) -> {
			return commands.objectEncoding(key);
		});
	}

	@Override
	default Long objectIdletime(K key) {
		return execute((commands) -> {
			return commands.objectIdletime(key);
		});
	}

	@Override
	default Long objectFreq(K key) {
		return execute((commands) -> {
			return commands.objectFreq(key);
		});
	}

	@Override
	default Long persist(K key) {
		return execute((commands) -> {
			return commands.persist(key);
		});
	}

	@Override
	default Long pexpire(K key, long milliseconds) {
		return execute((commands) -> {
			return commands.pexpire(key, milliseconds);
		});
	}

	@Override
	default Long pexpireAt(K key, long timestamp) {
		return execute((commands) -> {
			return commands.pexpireAt(key, timestamp);
		});
	}

	@Override
	default Long pttl(K key) {
		return execute((commands) -> {
			return commands.pttl(key);
		});
	}

	@Override
	default K randomkey() {
		return execute((commands) -> {
			return commands.randomkey();
		});
	}

	@Override
	default String rename(K key, K newKey) {
		return execute((commands) -> {
			return commands.rename(key, newKey);
		});
	}

	@Override
	default Boolean renamenx(K key, K newKey) {
		return execute((commands) -> {
			return commands.renamenx(key, newKey);
		});
	}

	@Override
	default String restore(K key, long ttl, byte[] serializedValue, RestoreParams params) {
		return execute((commands) -> {
			return commands.restore(key, ttl, serializedValue, params);
		});
	}

	@Override
	default Pageables<Long, K> scan(long cursorId, ScanOptions<K> options) {
		return new StreamPageables<Long, K>(cursorId,
				(cursor) -> execute((commands) -> commands.scan(cursor, options)));
	}

	@Override
	default Long touch(K... keys) {
		return execute((commands) -> {
			return commands.touch(keys);
		});
	}

	@Override
	default Long ttl(K key) {
		return execute((commands) -> {
			return commands.ttl(key);
		});
	}

	@Override
	default DataType type(K key) {
		return execute((commands) -> {
			return commands.type(key);
		});
	}

	@Override
	default Long unlink(K... keys) {
		return execute((commands) -> {
			return commands.unlink(keys);
		});
	}

	@Override
	default Long wait(int numreplicas, long timeout) {
		return execute((commands) -> {
			return commands.wait(numreplicas, timeout);
		});
	}

	@Override
	default V blmove(K sourceKey, K destinationKey, MovePosition from, MovePosition to, long timout) {
		return execute((commands) -> {
			return commands.blmove(sourceKey, destinationKey, from, to, timout);
		});
	}

	@Override
	default List<V> blpop(K... keys) {
		return execute((commands) -> {
			return commands.blpop(keys);
		});
	}

	@Override
	default List<V> blpop(double timeout, K... keys) {
		return execute((commands) -> {
			return commands.blpop(timeout, keys);
		});
	}

	@Override
	default List<V> brpop(K... keys) {
		return execute((commands) -> {
			return commands.brpop(keys);
		});
	}

	@Override
	default List<V> brpop(double timeout, K... keys) {
		return execute((commands) -> {
			return commands.brpop(timeout, keys);
		});
	}

	@Override
	default V brpoplpush(K sourceKey, K destinationKey, double timout) {
		return execute((commands) -> {
			return commands.brpoplpush(sourceKey, destinationKey, timout);
		});
	}

	@Override
	default V lindex(K key, long index) {
		return execute((commands) -> {
			return commands.lindex(key, index);
		});
	}

	@Override
	default Long linsert(K key, InsertPosition position, V pivot, V value) {
		return execute((commands) -> {
			return commands.linsert(key, position, pivot, value);
		});
	}

	@Override
	default Long llen(K key) {
		return execute((commands) -> {
			return commands.llen(key);
		});
	}

	@Override
	default V lmove(K sourceKey, K destinationKey, MovePosition from, MovePosition to) {
		return execute((commands) -> {
			return commands.lmove(sourceKey, destinationKey, from, to);
		});
	}

	@Override
	default List<V> lpop(K key, int count) {
		return execute((commands) -> {
			return commands.lpop(key, count);
		});
	}

	@Override
	default Long lpush(K key, V... elements) {
		return execute((commands) -> {
			return commands.lpush(key, elements);
		});
	}

	@Override
	default Long lpushx(K key, V... elements) {
		return execute((commands) -> {
			return commands.lpushx(key, elements);
		});
	}

	@Override
	default List<V> lrange(K key, long start, long stop) {
		return execute((commands) -> {
			return commands.lrange(key, start, stop);
		});
	}

	@Override
	default Long lrem(K key, int count, V element) {
		return execute((commands) -> {
			return commands.lrem(key, count, element);
		});
	}

	@Override
	default Boolean lset(K key, long index, V element) {
		return execute((commands) -> {
			return commands.lset(key, index, element);
		});
	}

	@Override
	default Boolean ltrim(K key, long start, long stop) {
		return execute((commands) -> {
			return commands.ltrim(key, start, stop);
		});
	}

	@Override
	default List<V> rpop(K key, int count) {
		return execute((commands) -> {
			return commands.rpop(key, count);
		});
	}

	@Override
	default V rpoplpush(K sourceKey, K destinationKey) {
		return execute((commands) -> {
			return commands.rpoplpush(sourceKey, destinationKey);
		});
	}

	@Override
	default Long rpush(K key, V... elements) {
		return execute((commands) -> {
			return commands.rpush(key, elements);
		});
	}

	@Override
	default Long rpushx(K key, V... elements) {
		return execute((commands) -> {
			return commands.rpushx(key, elements);
		});
	}

	default Long publish(K channel, V message) {
		return execute((commands) -> {
			return commands.publish(channel, message);
		});
	}

	@Override
	default <T> T eval(K script, List<K> keys, List<V> args) {
		return execute((commands) -> {
			return commands.eval(script, keys, args);
		});
	}

	@Override
	default <T> T evalsha(K sha1, List<K> keys, List<V> args) {
		return execute((commands) -> {
			return commands.evalsha(sha1, keys, args);
		});
	}

	@Override
	default List<Boolean> scriptexists(K... sha1) {
		return execute((commands) -> {
			return commands.scriptexists(sha1);
		});
	}

	@Override
	default String scriptFlush() {
		return execute((commands) -> {
			return commands.scriptFlush();
		});
	}

	@Override
	default String scriptFlush(FlushMode flushMode) {
		return execute((commands) -> {
			return commands.scriptFlush(flushMode);
		});
	}

	@Override
	default String scriptKill() {
		return execute((commands) -> {
			return commands.scriptKill();
		});
	}

	@Override
	default K scriptLoad(K script) {
		return execute((commands) -> {
			return commands.scriptLoad(script);
		});
	}

	@Override
	default List<V> bzpopmin(double timeout, K... keys) {
		return execute((commands) -> {
			return commands.bzpopmin(timeout, keys);
		});
	}

	@Override
	default Long zadd(K key, SetOption setOption, ScoreOption scoreOption, boolean changed,
			Map<V, Double> memberScores) {
		return execute((commands) -> {
			return commands.zadd(key, setOption, scoreOption, changed, memberScores);
		});
	}

	@Override
	default Double zaddIncr(K key, SetOption setOption, ScoreOption scoreOption, boolean changed, double score,
			V member) {
		return execute((commands) -> {
			return commands.zaddIncr(key, setOption, scoreOption, changed, score, member);
		});
	}

	@Override
	default Long zcard(K key) {
		return execute((commands) -> {
			return commands.zcard(key);
		});
	}

	@Override
	default Long zcount(K key, Range<? extends Number> range) {
		return execute((commands) -> {
			return commands.zcount(key, range);
		});
	}

	@Override
	default Long zdiffstore(K destinationKey, K... keys) {
		return execute((commands) -> {
			return commands.zdiffstore(destinationKey, keys);
		});
	}

	@Override
	default Double zincrby(K key, double increment, V member) {
		return execute((commands) -> {
			return commands.zincrby(key, increment, member);
		});
	}

	@Override
	default Collection<V> zinter(InterArgs args, K... keys) {
		return execute((commands) -> {
			return commands.zinter(args, keys);
		});
	}

	@Override
	default Collection<Tuple<V>> zinterWithScores(InterArgs args, K... keys) {
		return execute((commands) -> {
			return commands.zinterWithScores(args, keys);
		});
	}

	@Override
	default Long zinterstore(K destinationKey, InterArgs interArgs, K... keys) {
		return execute((commands) -> {
			return commands.zinterstore(destinationKey, interArgs, keys);
		});
	}

	@Override
	default Long zlexcount(K key, Range<V> range) {
		return execute((commands) -> {
			return commands.zlexcount(key, range);
		});
	}

	@Override
	default List<Double> zmscore(K key, V... members) {
		return execute((commands) -> {
			return commands.zmscore(key, members);
		});
	}

	@Override
	default Collection<Tuple<V>> zpopmax(K key, int count) {
		return execute((commands) -> {
			return commands.zpopmax(key, count);
		});
	}

	@Override
	default Collection<Tuple<V>> zpopmin(K key, int count) {
		return execute((commands) -> {
			return commands.zpopmin(key, count);
		});
	}

	@Override
	default Collection<V> zrandmember(K key, int count) {
		return execute((commands) -> {
			return commands.zrandmember(key, count);
		});
	}

	@Override
	default Collection<Tuple<V>> zrandmemberWithScores(K key, int count) {
		return execute((commands) -> {
			return commands.zrandmemberWithScores(key, count);
		});
	}

	@Override
	default Collection<V> zrange(K key, long start, long stop) {
		return execute((commands) -> {
			return commands.zrange(key, start, stop);
		});
	}

	@Override
	default Collection<V> zrangeByLex(K key, Range<V> range, int offset, int limit) {
		return execute((commands) -> {
			return commands.zrangeByLex(key, range, offset, limit);
		});
	}

	@Override
	default Collection<V> zrangeByScore(K key, Range<V> range, int offset, int limit) {
		return execute((commands) -> {
			return commands.zrangeByScore(key, range, offset, limit);
		});
	}

	@Override
	default Collection<Tuple<V>> zrangeByScoreWithScores(K key, Range<V> range, int offset, int limit) {
		return execute((commands) -> {
			return commands.zrangeByScoreWithScores(key, range, offset, limit);
		});
	}

	@Override
	default Long zrank(K key, V member) {
		return execute((commands) -> {
			return commands.zrank(key, member);
		});
	}

	@Override
	default Long zrem(K key, V... members) {
		return execute((commands) -> {
			return commands.zrem(key, members);
		});
	}

	@Override
	default Long zremrangebylex(K key, Range<V> range) {
		return execute((commands) -> {
			return commands.zremrangebylex(key, range);
		});
	}

	@Override
	default Long zremrangebyrank(K key, long start, long stop) {
		return execute((commands) -> {
			return commands.zremrangebyrank(key, start, stop);
		});
	}

	@Override
	default Long zremrangebyscore(K key, Range<V> range) {
		return execute((commands) -> {
			return commands.zremrangebyscore(key, range);
		});
	}

	@Override
	default Collection<V> zrevrange(K key, long start, long stop) {
		return execute((commands) -> {
			return commands.zrevrange(key, start, stop);
		});
	}

	@Override
	default Collection<V> zrevrangebylex(K key, Range<V> range, int offset, int count) {
		return execute((commands) -> {
			return commands.zrevrangebylex(key, range, offset, count);
		});
	}

	@Override
	default Collection<V> zrevrangebyscore(K key, Range<V> range, int offset, int count) {
		return execute((commands) -> {
			return commands.zrevrangebyscore(key, range, offset, count);
		});
	}

	@Override
	default Collection<Tuple<V>> zrevrangebyscoreWithScores(K key, Range<V> range, int offset, int count) {
		return execute((commands) -> {
			return commands.zrevrangebyscoreWithScores(key, range, offset, count);
		});
	}

	@Override
	default Long zrevrank(K key, V member) {
		return execute((commands) -> {
			return commands.zrevrank(key, member);
		});
	}

	@Override
	default Double zscore(K key, V member) {
		return execute((commands) -> {
			return commands.zscore(key, member);
		});
	}

	@Override
	default Collection<V> zunion(InterArgs interArgs, K... keys) {
		return execute((commands) -> {
			return commands.zunion(interArgs, keys);
		});
	}

	@Override
	default Collection<Tuple<V>> zunionWithScores(InterArgs interArgs, K... keys) {
		return execute((commands) -> {
			return commands.zunionWithScores(interArgs, keys);
		});
	}

	@Override
	default Long zunionstore(K destinationKey, InterArgs interArgs, K... keys) {
		return execute((commands) -> {
			return commands.zunionstore(destinationKey, interArgs, keys);
		});
	}

	@Override
	default Long xack(K key, K group, K... ids) {
		return execute((commands) -> {
			return commands.xack(key, group, ids);
		});
	}

	@Override
	default List<V> xclaim(K key, K group, K consumer, long minIdleTime, ClaimArgs args, K... ids) {
		return execute((commands) -> {
			return commands.xclaim(key, group, consumer, minIdleTime, args, ids);
		});
	}

	@Override
	default Long xdel(K key, K... ids) {
		return execute((commands) -> {
			return commands.xdel(key, ids);
		});
	}

	@Override
	default Long append(K key, V value) {
		return execute((commands) -> {
			return commands.append(key, value);
		});
	}

	@Override
	default Long bitcount(K key, long start, long end) {
		return execute((commands) -> {
			return commands.bitcount(key, start, end);
		});
	}

	@Override
	default Long bitop(BitOP op, K destkey, K... srcKeys) {
		return execute((commands) -> {
			return commands.bitop(op, destkey, srcKeys);
		});
	}

	@Override
	default Long bitpos(K key, boolean bit, Long start, Long end) {
		return execute((commands) -> {
			return commands.bitpos(key, bit, start, end);
		});
	}

	@Override
	default Long decr(K key) {
		return execute((commands) -> {
			return commands.decr(key);
		});
	}

	@Override
	default Long decrBy(K key, long decrement) {
		return execute((commands) -> {
			return commands.decrBy(key, decrement);
		});
	}

	@Override
	default V get(K key) {
		return execute((commands) -> {
			return commands.get(key);
		});
	}

	@Override
	default Boolean getbit(K key, Long offset) {
		return execute((commands) -> {
			return commands.getbit(key, offset);
		});
	}

	@Override
	default V getdel(K key) {
		return execute((commands) -> {
			return commands.getdel(key);
		});
	}

	@Override
	default V getEx(K key, ExpireOption option, Long time) {
		return execute((commands) -> {
			return commands.getEx(key, option, time);
		});
	}

	@Override
	default V getrange(K key, long startOffset, long endOffset) {
		return execute((commands) -> {
			return commands.getrange(key, startOffset, endOffset);
		});
	}

	@Override
	default V getset(K key, V value) {
		return execute((commands) -> {
			return commands.getset(key, value);
		});
	}

	@Override
	default Long incr(K key) {
		return execute((commands) -> {
			return commands.incr(key);
		});
	}

	@Override
	default Long incrBy(K key, long increment) {
		return execute((commands) -> {
			return commands.incrBy(key, increment);
		});
	}

	@Override
	default Double incrByFloat(K key, double increment) {
		return execute((commands) -> {
			return commands.incrByFloat(key, increment);
		});
	}

	@Override
	default List<V> mget(K... keys) {
		return execute((commands) -> {
			return commands.mget(keys);
		});
	}

	@Override
	default Boolean mset(Map<K, V> pairs) {
		return execute((commands) -> {
			return commands.mset(pairs);
		});
	}

	@Override
	default Long msetnx(Map<K, V> pairs) {
		return execute((commands) -> {
			return commands.msetnx(pairs);
		});
	}

	@Override
	default Boolean psetex(K key, long milliseconds, V value) {
		return execute((commands) -> {
			return commands.psetex(key, milliseconds, value);
		});
	}

	@Override
	default String set(K key, V value) {
		return execute((commands) -> {
			return commands.set(key, value);
		});
	}

	@Override
	default Boolean set(K key, V value, ExpireOption option, long time, SetOption setOption) {
		return execute((commands) -> {
			return commands.set(key, value, option, time, setOption);
		});
	}

	@Override
	default Boolean setbit(K key, long offset, boolean value) {
		return execute((commands) -> {
			return commands.setbit(key, offset, value);
		});
	}

	@Override
	default Boolean setex(K key, long seconds, V value) {
		return execute((commands) -> {
			return commands.setex(key, seconds, value);
		});
	}

	@Override
	default Boolean setNX(K key, V value) {
		return execute((commands) -> {
			return commands.setNX(key, value);
		});
	}

	@Override
	default Long setrange(K key, Long offset, V value) {
		return execute((commands) -> {
			return commands.setrange(key, offset, value);
		});
	}

	@Override
	default Long strlen(K key) {
		return execute((commands) -> {
			return commands.strlen(key);
		});
	}

	@Override
	default Long sadd(K key, V... members) {
		return execute((commands) -> {
			return commands.sadd(key, members);
		});
	}

	@Override
	default Long scard(K key) {
		return execute((commands) -> {
			return commands.scard(key);
		});
	}

	@Override
	default Set<V> sdiff(K... keys) {
		return execute((commands) -> {
			return commands.sdiff(keys);
		});
	}

	@Override
	default Long sdiffstore(K destinationKey, K... keys) {
		return execute((commands) -> {
			return commands.sdiffstore(destinationKey, keys);
		});
	}

	@Override
	default Set<V> sinter(K... keys) {
		return execute((commands) -> {
			return commands.sinter(keys);
		});
	}

	@Override
	default Long sinterstore(K destinationKey, K... keys) {
		return execute((commands) -> {
			return commands.sinterstore(destinationKey, keys);
		});
	}

	@Override
	default Boolean sismember(K key, V member) {
		return execute((commands) -> {
			return commands.sismember(key, member);
		});
	}

	@Override
	default Set<V> smembers(K key) {
		return execute((commands) -> {
			return commands.smembers(key);
		});
	}

	@Override
	default List<Boolean> smismember(K key, V... members) {
		return execute((commands) -> {
			return commands.smismember(key, members);
		});
	}

	@Override
	default Boolean sMove(K sourceKey, K destinationKey, V member) {
		return execute((commands) -> {
			return commands.sMove(sourceKey, destinationKey, member);
		});
	}

	@Override
	default Set<V> spop(K key, int count) {
		return execute((commands) -> {
			return commands.spop(key, count);
		});
	}

	@Override
	default List<V> srandmember(K key, int count) {
		return execute((commands) -> {
			return commands.srandmember(key, count);
		});
	}

	@Override
	default Long srem(K key, V... members) {
		return execute((commands) -> {
			return commands.srem(key, members);
		});
	}

	@Override
	default Set<V> sunion(K... keys) {
		return execute((commands) -> commands.sunion(keys));
	}

	@Override
	default Long sunionstore(K destinationKey, K... keys) {
		return execute((commands) -> commands.sunionstore(destinationKey, keys));
	}

	@Override
	default Pageables<Long, K> sScan(long cursorId, K key, ScanOptions<K> options) {
		return new StreamPageables<Long, K>(cursorId,
				(cursor) -> execute((commands) -> commands.sScan(cursor, key, options)));
	}

	@Override
	default List<K> aclCat(K categoryname) {
		return execute((e) -> e.aclCat(categoryname));
	}

	@Override
	default Long aclDelUser(K username, K... usernames) {
		return execute((e) -> e.aclDelUser(username, usernames));
	}

	@Override
	default String aclGenPass(Integer bits) {
		return execute((e) -> e.aclGenPass(bits));
	}

	@Override
	default List<K> aclList() {
		return execute((e) -> e.aclList());
	}

	@Override
	default String aclLoad() {
		return execute((e) -> e.aclLoad());
	}

	@Override
	default List<K> aclLog(Integer count) {
		return execute((e) -> e.aclLog(count));
	}

	@Override
	default String aclLogReset() {
		return execute((e) -> e.aclLogReset());
	}

	@Override
	default String aclSave() {
		return execute((e) -> e.aclSave());
	}

	@Override
	default String aclSetuser(K username, K... rules) {
		return execute((e) -> e.aclSetuser(username, rules));
	}

	@Override
	default List<K> aclUsers() {
		return execute((e) -> e.aclUsers());
	}

	@Override
	default K aclWhoami() {
		return execute((e) -> e.aclWhoami());
	}

	@Override
	default String bgrewriteaof() {
		return execute((e) -> e.bgrewriteaof());
	}

	@Override
	default String bgsave() {
		return execute((e) -> e.bgsave());
	}

	@Override
	default List<V> configGet(K parameter) {
		return execute((e) -> e.configGet(parameter));
	}

	@Override
	default String configResetstat() {
		return execute((e) -> e.configResetstat());
	}

	@Override
	default String configRewrite() {
		return execute((e) -> e.configRewrite());
	}

	@Override
	default String configSet(K parameter, V value) {
		return execute((e) -> e.configSet(parameter, value));
	}

	@Override
	default Long dbsize() {
		return execute((e) -> e.dbsize());
	}

	@Override
	default String failover(FailoverParams params) {
		return execute((e) -> e.failover(params));
	}

	@Override
	default String failoverAbort() {
		return execute((e) -> e.failover());
	}

	@Override
	default String flushall(FlushMode flushMode) {
		return execute((e) -> e.flushall(flushMode));
	}

	@Override
	default String flushdb(FlushMode flushMode) {
		return execute((e) -> e.flushdb(flushMode));
	}

	@Override
	default String info(String section) {
		return execute((e) -> e.info(section));
	}

	@Override
	default Long lastsave() {
		return execute((e) -> e.lastsave());
	}

	@Override
	default String memoryDoctor() {
		return execute((e) -> e.memoryDoctor());
	}

	@Override
	default Long memoryUsage(K key, int samples) {
		return execute((e) -> e.memoryUsage(key, samples));
	}

	@Override
	default List<Module> moduleList() {
		return execute((e) -> e.moduleList());
	}

	@Override
	default String moduleLoad(String path) {
		return execute((e) -> e.moduleLoad(path));
	}

	@Override
	default String moduleUnload(String name) {
		return execute((e) -> e.moduleUnload(name));
	}

	@Override
	default List<Object> role() {
		return execute((e) -> e.role());
	}

	@Override
	default String save() {
		return execute((e) -> e.save());
	}

	@Override
	default void shutdown(SaveMode saveMode) {
		execute((e) -> {
			e.shutdown(saveMode);
			return null;
		});
	}

	@Override
	default String slaveof(String host, int port) {
		return execute((e) -> e.slaveof(host, port));
	}

	@Override
	default List<Slowlog> slowlogGet(Long count) {
		return execute((e) -> e.slowlogGet(count));
	}

	@Override
	default Long slowlogLen() {
		return execute((e) -> e.slowlogLen());
	}

	@Override
	default String slowlogReset() {
		return execute((e) -> e.slowlogReset());
	}

	@Override
	default String swapdb(int index1, int index2) {
		return execute((e) -> e.swapdb(index1, index2));
	}

	@Override
	default List<String> time() {
		return execute((e) -> e.time());
	}
}
