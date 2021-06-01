package scw.redis.core.convert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import scw.codec.Codec;
import scw.convert.Converter;
import scw.convert.IdentityConverter;
import scw.core.utils.CollectionUtils;
import scw.data.domain.Range;
import scw.data.geo.Circle;
import scw.data.geo.Distance;
import scw.data.geo.Metric;
import scw.data.geo.Point;
import scw.redis.core.Cursor;
import scw.redis.core.DataType;
import scw.redis.core.GeoRadiusArgs;
import scw.redis.core.GeoRadiusWith;
import scw.redis.core.GeoWithin;
import scw.redis.core.MessageListener;
import scw.redis.core.RedisAuth;
import scw.redis.core.RedisCommands;
import scw.redis.core.RedisValueEncoding;
import scw.redis.core.ReturnType;
import scw.redis.core.ScanOptions;
import scw.redis.core.SetOption;
import scw.redis.core.Subscription;
import scw.util.CollectionFactory;

@SuppressWarnings("unchecked")
public class ConvertibleRedisCommands<K, V, TK, TV> implements RedisCommands<K, V> {
	private final Codec<K, TK> keyCodec;
	private final Codec<V, TV> valueCodec;
	private final RedisCommands<TK, TV> redisCommands;

	public ConvertibleRedisCommands(RedisCommands<TK, TV> redisCommands, Codec<K, TK> keyCodec,
			Codec<V, TV> valueCodec) {
		this.redisCommands = redisCommands;
		this.keyCodec = keyCodec;
		this.valueCodec = valueCodec;
	}

	@Override
	public V ping(K message) {
		TK k = keyCodec.encode(message);
		TV v = redisCommands.ping(k);
		return valueCodec.decode(v);
	}

	@Override
	public String select(int index) {
		return redisCommands.select(index);
	}

	@Override
	public Long geoadd(K key, GeoaddOption option, Map<V, Point> members) {
		TK k = keyCodec.encode(key);
		Map<TV, Point> tvMap = new LinkedHashMap<TV, Point>(members.size());
		for (Entry<V, Point> entry : members.entrySet()) {
			tvMap.put(valueCodec.encode(entry.getKey()), entry.getValue());
		}
		return redisCommands.geoadd(k, option, tvMap);
	}

	@Override
	public Double geodist(K key, V member1, V member2, Metric metric) {
		TK k = keyCodec.encode(key);
		TV v1 = valueCodec.encode(member1);
		TV v2 = valueCodec.encode(member2);
		return redisCommands.geodist(k, v1, v2, metric);
	}

	@Override
	public List<String> geohash(K key, V... members) {
		TK k = keyCodec.encode(key);
		TV[] tms = valueCodec.encode(members);
		return redisCommands.geohash(k, tms);
	}

	@Override
	public List<Point> geopos(K key, V... members) {
		TK k = keyCodec.encode(key);
		TV[] tms = valueCodec.encode(members);
		return redisCommands.geopos(k, tms);
	}

	@Override
	public Collection<V> georadius(K key, Circle within, GeoRadiusArgs<K> args) {
		TK k = keyCodec.encode(key);
		GeoRadiusArgs<TK> tArgs = args.convert(keyCodec.toEncodeConverter());
		Collection<TV> values = redisCommands.georadius(k, within, tArgs);
		return valueCodec.decode(values);
	}

	private final Converter<GeoWithin<TV>, GeoWithin<V>> geoWithinDecoder = new Converter<GeoWithin<TV>, GeoWithin<V>>() {

		@Override
		public GeoWithin<V> convert(GeoWithin<TV> o) {
			return o.convert(valueCodec.toDecodeConverter());
		}
	};

	@Override
	public List<GeoWithin<V>> georadius(K key, Circle within, GeoRadiusWith with, GeoRadiusArgs<K> args) {
		TK k = keyCodec.encode(key);
		GeoRadiusArgs<TK> tArgs = args.convert(keyCodec.toEncodeConverter());
		Collection<GeoWithin<TV>> values = redisCommands.georadius(k, within, with, tArgs);
		return geoWithinDecoder.convert(values, new ArrayList<GeoWithin<V>>(values.size()));
	}

	@Override
	public List<V> georadiusbymember(K key, V member, Distance distance, GeoRadiusArgs<K> args) {
		TK k = keyCodec.encode(key);
		TV tm = valueCodec.encode(member);
		GeoRadiusArgs<TK> tArgs = args.convert(keyCodec.toEncodeConverter());
		List<TV> values = redisCommands.georadiusbymember(k, tm, distance, tArgs);
		return valueCodec.decode(values);
	}

	@Override
	public List<GeoWithin<V>> georadiusbymember(K key, V member, Distance distance, GeoRadiusWith with,
			GeoRadiusArgs<K> args) {
		TK k = keyCodec.encode(key);
		TV tm = valueCodec.encode(member);
		GeoRadiusArgs<TK> tArgs = args.convert(keyCodec.toEncodeConverter());
		Collection<GeoWithin<TV>> values = redisCommands.georadiusbymember(k, tm, distance, with, tArgs);
		return geoWithinDecoder.convert(values, new ArrayList<GeoWithin<V>>(values.size()));
	}

	@Override
	public Long hdel(K key, K... fields) {
		TK k = keyCodec.encode(key);
		TK[] tfs = keyCodec.encode(fields);
		return redisCommands.hdel(k, tfs);
	}

	@Override
	public Boolean hexists(K key, K field) {
		TK k = keyCodec.encode(key);
		TK f = keyCodec.encode(field);
		return redisCommands.hexists(k, f);
	}

	@Override
	public V hget(K key, K field) {
		TK k = keyCodec.encode(key);
		TK f = keyCodec.encode(field);
		TV v = redisCommands.hget(k, f);
		return valueCodec.decode(v);
	}

	@Override
	public Map<K, V> hgetall(K key) {
		TK k = keyCodec.encode(key);
		Map<TK, TV> valueMap = redisCommands.hgetall(k);
		return CollectionFactory.convert(valueMap, keyCodec.toDecodeConverter(), valueCodec.toDecodeConverter());
	}

	@Override
	public Long hincrby(K key, K field, long increment) {
		TK k = keyCodec.encode(key);
		TK f = keyCodec.encode(field);
		return redisCommands.hincrby(k, f, increment);
	}

	@Override
	public Double hincrbyfloat(K key, K field, double increment) {
		TK k = keyCodec.encode(key);
		TK f = keyCodec.encode(field);
		return redisCommands.hincrbyfloat(k, f, increment);
	}

	@Override
	public Set<K> hkeys(K key) {
		TK k = keyCodec.encode(key);
		Set<TK> tks = redisCommands.hkeys(k);
		return keyCodec.toDecodeConverter().convert(tks, new LinkedHashSet<K>(tks.size()));
	}

	@Override
	public Long hlen(K key) {
		TK k = keyCodec.encode(key);
		return redisCommands.hlen(k);
	}

	@Override
	public List<V> hmget(K key, K... fields) {
		TK k = keyCodec.encode(key);
		TK[] tfs = keyCodec.encode(fields);
		List<TV> values = redisCommands.hmget(k, tfs);
		return valueCodec.decode(values);
	}

	@Override
	public void hmset(K key, Map<K, V> values) {
		TK k = keyCodec.encode(key);
		Map<TK, TV> tMap = CollectionFactory.convert(values, keyCodec.toEncodeConverter(),
				valueCodec.toEncodeConverter());
		redisCommands.hmset(k, tMap);
	}

	@Override
	public List<K> hrandfield(K key, Integer count) {
		TK k = keyCodec.encode(key);
		List<TK> tks = redisCommands.hrandfield(k, count);
		return keyCodec.decode(tks);
	}

	@Override
	public Map<K, V> hrandfieldWithValue(K key, Integer count) {
		TK k = keyCodec.encode(key);
		Map<TK, TV> tMap = redisCommands.hrandfieldWithValue(k, count);
		return CollectionFactory.convert(tMap, keyCodec.toDecodeConverter(), valueCodec.toDecodeConverter());
	}

	@Override
	public Long hset(K key, Map<K, V> values) {
		TK k = keyCodec.encode(key);
		Map<TK, TV> tMap = CollectionFactory.convert(values, keyCodec.toEncodeConverter(),
				valueCodec.toEncodeConverter());
		return redisCommands.hset(k, tMap);
	}

	@Override
	public Boolean hsetnx(K key, K field, V value) {
		TK k = keyCodec.encode(key);
		TK tf = keyCodec.encode(field);
		TV tv = valueCodec.encode(value);
		return redisCommands.hsetnx(k, tf, tv);
	}

	@Override
	public Long hstrlen(K key, K field) {
		TK k = keyCodec.encode(key);
		TK tf = keyCodec.encode(field);
		return redisCommands.hstrlen(k, tf);
	}

	@Override
	public List<V> hvals(K key) {
		TK k = keyCodec.encode(key);
		List<TV> values = redisCommands.hvals(k);
		return valueCodec.decode(values);
	}

	@Override
	public Long pfadd(K key, V... elements) {
		TK k = keyCodec.encode(key);
		TV[] tvs = valueCodec.encode(elements);
		return redisCommands.pfadd(k, tvs);
	}

	@Override
	public Long pfcount(K... keys) {
		TK[] ks = keyCodec.encode(keys);
		return redisCommands.pfcount(ks);
	}

	@Override
	public Boolean pfmerge(K destKey, K... sourceKeys) {
		TK dk = keyCodec.encode(destKey);
		TK[] sks = keyCodec.encode(sourceKeys);
		return redisCommands.pfmerge(dk, sks);
	}

	@Override
	public Boolean copy(K source, K destination, Integer destinationDB, boolean replace) {
		TK sk = keyCodec.encode(source);
		TK dk = keyCodec.encode(destination);
		return redisCommands.copy(sk, dk, destinationDB, replace);
	}

	@Override
	public Long del(K... keys) {
		TK[] ks = keyCodec.encode(keys);
		return redisCommands.del(ks);
	}

	@Override
	public V dump(K key) {
		TK k = keyCodec.encode(key);
		TV value = redisCommands.dump(k);
		return valueCodec.decode(value);
	}

	@Override
	public Long exists(K... keys) {
		TK[] ks = keyCodec.encode(keys);
		return redisCommands.exists(ks);
	}

	@Override
	public Long expire(K key, long seconds) {
		TK k = keyCodec.encode(key);
		return redisCommands.expire(k, seconds);
	}

	@Override
	public Long expireAt(K key, long timestamp) {
		TK k = keyCodec.encode(key);
		return redisCommands.expireAt(k, timestamp);
	}

	@Override
	public Set<K> keys(K pattern) {
		TK k = keyCodec.encode(pattern);
		Set<TK> tvs = redisCommands.keys(k);
		return keyCodec.toDecodeConverter().convert(tvs, new LinkedHashSet<K>(tvs.size()));
	}

	@Override
	public String migrate(String host, int port, K key, int targetDB, int timeout) {
		TK k = keyCodec.encode(key);
		return redisCommands.migrate(host, port, k, targetDB, timeout);
	}

	@Override
	public String migrate(String host, int port, int targetDB, int timeout, boolean copy, boolean replace,
			RedisAuth auth, K... keys) {
		TK[] ks = keyCodec.encode(keys);
		return redisCommands.migrate(host, port, targetDB, timeout, copy, replace, auth, ks);
	}

	@Override
	public Long move(K key, int targetDB) {
		TK k = keyCodec.encode(key);
		return redisCommands.move(k, targetDB);
	}

	@Override
	public Long objectRefCount(K key) {
		TK k = keyCodec.encode(key);
		return redisCommands.objectRefCount(k);
	}

	@Override
	public RedisValueEncoding objectEncoding(K key) {
		TK k = keyCodec.encode(key);
		return redisCommands.objectEncoding(k);
	}

	@Override
	public Long objectIdletime(K key) {
		TK k = keyCodec.encode(key);
		return redisCommands.objectIdletime(k);
	}

	@Override
	public Long objectFreq(K key) {
		TK k = keyCodec.encode(key);
		return redisCommands.objectFreq(k);
	}

	@Override
	public Long persist(K key) {
		TK k = keyCodec.encode(key);
		return redisCommands.persist(k);
	}

	@Override
	public Long pexpire(K key, long milliseconds) {
		TK k = keyCodec.encode(key);
		return redisCommands.pexpire(k, milliseconds);
	}

	@Override
	public Long pexpireAt(K key, long timestamp) {
		TK k = keyCodec.encode(key);
		return redisCommands.pexpireAt(k, timestamp);
	}

	@Override
	public Long pttl(K key) {
		TK k = keyCodec.encode(key);
		return redisCommands.pttl(k);
	}

	@Override
	public K randomkey() {
		TK k = redisCommands.randomkey();
		return keyCodec.decode(k);
	}

	@Override
	public void rename(K key, K newKey) {
		TK k = keyCodec.encode(key);
		TK tkNewKey = keyCodec.encode(newKey);
		redisCommands.rename(k, tkNewKey);
	}

	@Override
	public Boolean renamenx(K key, K newKey) {
		TK k = keyCodec.encode(key);
		TK tkNewKey = keyCodec.encode(newKey);
		return redisCommands.renamenx(k, tkNewKey);
	}

	@Override
	public void restore(K key, long ttl, byte[] serializedValue, boolean replace, boolean absTtl, Long idleTime,
			Long frequency) {
		TK k = keyCodec.encode(key);
		redisCommands.restore(k, ttl, serializedValue, replace, absTtl, idleTime, frequency);
	}

	@Override
	public Cursor<K> scan(long cursorId, ScanOptions<K> options) {
		ScanOptions<TK> to = options.convert(keyCodec.toEncodeConverter());
		Cursor<TK> cursor = redisCommands.scan(cursorId, to);
		return new ConvertibleCursor<TK, K>(cursor, keyCodec.toDecodeConverter());
	}

	@Override
	public Long touch(K... keys) {
		TK[] ks = keyCodec.encode(keys);
		return redisCommands.touch(ks);
	}

	@Override
	public Long ttl(K key) {
		TK k = keyCodec.encode(key);
		return redisCommands.ttl(k);
	}

	@Override
	public DataType type(K key) {
		TK k = keyCodec.encode(key);
		return redisCommands.type(k);
	}

	@Override
	public Long unlink(K... keys) {
		TK[] ks = keyCodec.encode(keys);
		return redisCommands.unlink(ks);
	}

	@Override
	public Long wait(int numreplicas, long timeout) {
		return redisCommands.wait(numreplicas, timeout);
	}

	@Override
	public V blmove(K sourceKey, K destinationKey, MovePosition from, MovePosition to, long timout) {
		TK sk = keyCodec.encode(sourceKey);
		TK dk = keyCodec.encode(destinationKey);
		TV v = redisCommands.blmove(sk, dk, from, to, timout);
		return valueCodec.decode(v);
	}

	@Override
	public List<V> blpop(K... keys) {
		TK[] ks = keyCodec.encode(keys);
		List<TV> values = redisCommands.blpop(ks);
		return valueCodec.decode(values);
	}

	@Override
	public List<V> blpop(double timeout, K... keys) {
		TK[] ks = keyCodec.encode(keys);
		List<TV> values = redisCommands.blpop(timeout, ks);
		return valueCodec.decode(values);
	}

	@Override
	public List<V> brpop(K... keys) {
		TK[] ks = keyCodec.encode(keys);
		List<TV> values = redisCommands.brpop(ks);
		return valueCodec.decode(values);
	}

	@Override
	public List<V> brpop(double timeout, K... keys) {
		TK[] ks = keyCodec.encode(keys);
		List<TV> values = redisCommands.brpop(timeout, ks);
		return valueCodec.decode(values);
	}

	@Override
	public V brpoplpush(K sourceKey, K destinationKey, double timout) {
		TK sk = keyCodec.encode(sourceKey);
		TK dk = keyCodec.encode(destinationKey);
		TV v = redisCommands.brpoplpush(sk, dk, timout);
		return valueCodec.decode(v);
	}

	@Override
	public V lindex(K key, long index) {
		TK k = keyCodec.encode(key);
		TV v = redisCommands.lindex(k, index);
		return valueCodec.decode(v);
	}

	@Override
	public Long linsert(K key, InsertPosition position, V pivot, V value) {
		TK k = keyCodec.encode(key);
		TV pv = valueCodec.encode(pivot);
		TV tv = valueCodec.encode(value);
		return redisCommands.linsert(k, position, pv, tv);
	}

	@Override
	public Long llen(K key) {
		TK k = keyCodec.encode(key);
		return redisCommands.llen(k);
	}

	@Override
	public V lmove(K sourceKey, K destinationKey, MovePosition from, MovePosition to) {
		TK sk = keyCodec.encode(sourceKey);
		TK dk = keyCodec.encode(destinationKey);
		TV v = redisCommands.lmove(sk, dk, from, to);
		return valueCodec.decode(v);
	}

	@Override
	public List<V> lpop(K key, int count) {
		TK k = keyCodec.encode(key);
		List<TV> values = redisCommands.lpop(k, count);
		return valueCodec.decode(values);
	}

	@Override
	public Long lpush(K key, V... elements) {
		TK k = keyCodec.encode(key);
		TV[] vs = valueCodec.encode(elements);
		return redisCommands.lpush(k, vs);
	}

	@Override
	public Long lpushx(K key, V... elements) {
		TK k = keyCodec.encode(key);
		TV[] vs = valueCodec.encode(elements);
		return redisCommands.lpushx(k, vs);
	}

	@Override
	public List<V> lrange(K key, long start, long stop) {
		TK k = keyCodec.encode(key);
		List<TV> values = redisCommands.lrange(k, start, stop);
		return valueCodec.decode(values);
	}

	@Override
	public Long lrem(K key, int count, V element) {
		TK k = keyCodec.encode(key);
		TV v = valueCodec.encode(element);
		return redisCommands.lrem(k, count, v);
	}

	@Override
	public Boolean lset(K key, long index, V element) {
		TK k = keyCodec.encode(key);
		TV v = valueCodec.encode(element);
		return redisCommands.lset(k, index, v);
	}

	@Override
	public Boolean ltrim(K key, long start, long stop) {
		TK k = keyCodec.encode(key);
		return redisCommands.ltrim(k, start, stop);
	}

	@Override
	public List<V> rpop(K key, int count) {
		TK k = keyCodec.encode(key);
		List<TV> values = redisCommands.rpop(k, count);
		return valueCodec.decode(values);
	}

	@Override
	public V rpoplpush(K sourceKey, K destinationKey) {
		TK sk = keyCodec.encode(sourceKey);
		TK dk = keyCodec.encode(destinationKey);
		TV v = redisCommands.rpoplpush(sk, dk);
		return valueCodec.decode(v);
	}

	@Override
	public Long rpush(K key, V... elements) {
		TK k = keyCodec.encode(key);
		TV[] vs = valueCodec.encode(elements);
		return redisCommands.rpush(k, vs);
	}

	@Override
	public Long rpushx(K key, V... elements) {
		TK k = keyCodec.encode(key);
		TV[] vs = valueCodec.encode(elements);
		return redisCommands.rpushx(k, vs);
	}

	@Override
	public boolean isSubscribed() {
		return redisCommands.isSubscribed();
	}

	@Override
	public Subscription<K, V> getSubscription() {
		return new ConvertibleSubscription<TK, TV, K, V>(redisCommands.getSubscription(), keyCodec,
				valueCodec.toEncodeConverter());
	}

	@Override
	public Long publish(K channel, V message) {
		TK k = keyCodec.encode(channel);
		TV m = valueCodec.encode(message);
		return redisCommands.publish(k, m);
	}

	@Override
	public void subscribe(MessageListener<K, V> listener, K... channels) {
		TK[] ks = keyCodec.encode(channels);
		MessageListener<TK, TV> messageListener = new ConvertibleMessageListener<K, V, TK, TV>(listener,
				keyCodec.toDecodeConverter(), valueCodec.toDecodeConverter());
		redisCommands.subscribe(messageListener, ks);
	}

	@Override
	public void pSubscribe(MessageListener<K, V> listener, K... patterns) {
		TK[] ks = keyCodec.encode(patterns);
		MessageListener<TK, TV> messageListener = new ConvertibleMessageListener<K, V, TK, TV>(listener,
				keyCodec.toDecodeConverter(), valueCodec.toDecodeConverter());
		redisCommands.pSubscribe(messageListener, ks);
	}

	@Override
	public <T> T eval(K script, ReturnType returnType, List<K> keys, List<V> args) {
		TK k = keyCodec.encode(script);
		List<TK> ks = keyCodec.encode(keys);
		List<TV> vs = valueCodec.encode(args);
		return redisCommands.eval(k, returnType, ks, vs);
	}

	@Override
	public <T> T evalsha(K sha1, ReturnType returnType, List<K> keys, List<V> args) {
		TK k = keyCodec.encode(sha1);
		List<TK> ks = keyCodec.encode(keys);
		List<TV> vs = valueCodec.encode(args);
		return redisCommands.evalsha(k, returnType, ks, vs);
	}

	@Override
	public List<Boolean> scriptexists(K... sha1) {
		TK[] ks = keyCodec.encode(sha1);
		return redisCommands.scriptexists(ks);
	}

	@Override
	public void scriptFlush() {
		redisCommands.scriptFlush();
	}

	@Override
	public void scriptFlush(FlushMode flushMode) {
		redisCommands.scriptFlush(flushMode);
	}

	@Override
	public void scriptKill() {
		redisCommands.scriptKill();
	}

	@Override
	public K scriptLoad(K script) {
		TK k = keyCodec.encode(script);
		TK v = redisCommands.scriptLoad(k);
		return keyCodec.decode(v);
	}

	@Override
	public List<V> bzpopmin(double timeout, K... keys) {
		TK[] ks = keyCodec.encode(keys);
		List<TV> vs = redisCommands.bzpopmin(timeout, ks);
		return valueCodec.decode(vs);
	}

	@Override
	public Long zadd(K key, SetOption setOption, ScoreOption scoreOption, boolean changed,
			Map<V, Double> memberScores) {
		TK k = keyCodec.encode(key);
		Map<TV, Double> ts = CollectionFactory.convert(memberScores, valueCodec.toEncodeConverter(),
				new IdentityConverter<Double>());
		return redisCommands.zadd(k, setOption, scoreOption, changed, ts);
	}

	@Override
	public Double zaddIncr(K key, SetOption setOption, ScoreOption scoreOption, boolean changed, double score,
			V member) {
		TK k = keyCodec.encode(key);
		TV v = valueCodec.encode(member);
		return redisCommands.zaddIncr(k, setOption, scoreOption, changed, score, v);
	}

	@Override
	public Long zcard(K key) {
		TK k = keyCodec.encode(key);
		return redisCommands.zcard(k);
	}

	@Override
	public Long zcount(K key, Range<? extends Number> range) {
		TK k = keyCodec.encode(key);
		return redisCommands.zcount(k, range);
	}

	@Override
	public Long zdiffstore(K destinationKey, K... keys) {
		TK dk = keyCodec.encode(destinationKey);
		TK[] ks = keyCodec.encode(keys);
		return redisCommands.zdiffstore(dk, ks);
	}

	@Override
	public Double zincrby(K key, double increment, V member) {
		TK k = keyCodec.encode(key);
		TV v = valueCodec.encode(member);
		return redisCommands.zincrby(k, increment, v);
	}

	@Override
	public Collection<V> zinter(InterArgs args, K... keys) {
		TK[] ks = keyCodec.encode(keys);
		Collection<TV> tvs = redisCommands.zinter(args, ks);
		if (CollectionUtils.isEmpty(tvs)) {
			return Collections.emptyList();
		}
		return valueCodec.toDecodeConverter().convert(tvs, new LinkedHashSet<V>(tvs.size()));
	}

	private final Converter<Tuple<TV>, Tuple<V>> tupleDecoder = new Converter<Tuple<TV>, Tuple<V>>() {

		@Override
		public Tuple<V> convert(Tuple<TV> o) {
			return new Tuple<V>(valueCodec.decode(o.getValue()), o.getScore());
		}

	};

	@Override
	public Collection<Tuple<V>> zinterWithScores(InterArgs args, K... keys) {
		TK[] ks = keyCodec.encode(keys);
		Collection<Tuple<TV>> values = redisCommands.zinterWithScores(args, ks);
		return tupleDecoder.convert(values);
	}

	@Override
	public Long zinterstore(K destinationKey, InterArgs interArgs, K... keys) {
		TK dk = keyCodec.encode(destinationKey);
		TK[] ks = keyCodec.encode(keys);
		return redisCommands.zinterstore(dk, interArgs, ks);
	}

	@Override
	public Long zlexcount(K key, Range<V> range) {
		TK k = keyCodec.encode(key);
		Range<TV> tr = range.convert(valueCodec.toEncodeConverter());
		return redisCommands.zlexcount(k, tr);
	}

	@Override
	public List<Double> zmscore(K key, V... members) {
		TK k = keyCodec.encode(key);
		TV[] ms = valueCodec.encode(members);
		return redisCommands.zmscore(k, ms);
	}

	@Override
	public Collection<Tuple<V>> zpopmax(K key, int count) {
		TK k = keyCodec.encode(key);
		Collection<Tuple<TV>> tuples = redisCommands.zpopmax(k, count);
		return tupleDecoder.convert(tuples);
	}

	@Override
	public Collection<Tuple<V>> zpopmin(K key, int count) {
		TK k = keyCodec.encode(key);
		Collection<Tuple<TV>> tuples = redisCommands.zpopmin(k, count);
		return tupleDecoder.convert(tuples);
	}

	@Override
	public Collection<V> zrandmember(K key, int count) {
		TK k = keyCodec.encode(key);
		Collection<TV> tuples = redisCommands.zrandmember(k, count);
		return valueCodec.decode(tuples);
	}

	@Override
	public Collection<Tuple<V>> zrandmemberWithScores(K key, int count) {
		TK k = keyCodec.encode(key);
		Collection<Tuple<TV>> tuples = redisCommands.zrandmemberWithScores(k, count);
		return tupleDecoder.convert(tuples);
	}

	@Override
	public Collection<V> zrange(K key, long start, long stop) {
		TK k = keyCodec.encode(key);
		Collection<TV> tuples = redisCommands.zrange(k, start, stop);
		return valueCodec.decode(tuples);
	}

	@Override
	public Collection<V> zrangeByLex(K key, Range<V> range, int offset, int limit) {
		TK k = keyCodec.encode(key);
		Collection<TV> tuples = redisCommands.zrangeByLex(k, range.convert(valueCodec.toEncodeConverter()), offset,
				limit);
		return valueCodec.decode(tuples);
	}

	@Override
	public Collection<V> zrangeByScore(K key, Range<V> range, int offset, int limit) {
		TK k = keyCodec.encode(key);
		Collection<TV> tuples = redisCommands.zrangeByScore(k, range.convert(valueCodec.toEncodeConverter()), offset,
				limit);
		return valueCodec.decode(tuples);
	}

	@Override
	public Collection<Tuple<V>> zrangeByScoreWithScores(K key, Range<V> range, int offset, int limit) {
		TK k = keyCodec.encode(key);
		Collection<Tuple<TV>> tuples = redisCommands.zrangeByScoreWithScores(k,
				range.convert(valueCodec.toEncodeConverter()), offset, limit);
		return tupleDecoder.convert(tuples);
	}

	@Override
	public Long zrank(K key, V member) {
		TK k = keyCodec.encode(key);
		TV v = valueCodec.encode(member);
		return redisCommands.zrank(k, v);
	}

	@Override
	public Long zrem(K key, V... members) {
		TK k = keyCodec.encode(key);
		TV[] ms = valueCodec.encode(members);
		return redisCommands.zrem(k, ms);
	}

	@Override
	public Long zremrangebylex(K key, Range<V> range) {
		return redisCommands.zremrangebylex(keyCodec.encode(key), range.convert(valueCodec.toEncodeConverter()));
	}

	@Override
	public Long zremrangebyrank(K key, long start, long stop) {
		return redisCommands.zremrangebyrank(keyCodec.encode(key), start, stop);
	}

	@Override
	public Long zremrangebyscore(K key, Range<V> range) {
		return redisCommands.zremrangebyscore(keyCodec.encode(key), range.convert(valueCodec.toEncodeConverter()));
	}

	@Override
	public Collection<V> zrevrange(K key, long start, long stop) {
		Collection<TV> values = redisCommands.zrevrange(keyCodec.encode(key), start, stop);
		return valueCodec.toDecodeConverter().convert(values);
	}

	@Override
	public Collection<V> zrevrangebylex(K key, Range<V> range, int offset, int count) {
		Collection<TV> values = redisCommands.zrevrangebylex(keyCodec.encode(key),
				range.convert(valueCodec.toEncodeConverter()), offset, count);
		return valueCodec.toDecodeConverter().convert(values);
	}

	@Override
	public Collection<V> zrevrangebyscore(K key, Range<V> range, int offset, int count) {
		Collection<TV> values = redisCommands.zrevrangebyscore(keyCodec.encode(key),
				range.convert(valueCodec.toEncodeConverter()), offset, count);
		return valueCodec.toDecodeConverter().convert(values);
	}

	@Override
	public Collection<Tuple<V>> zrevrangebyscoreWithScores(K key, Range<V> range, int offset, int count) {
		Collection<Tuple<TV>> tuples = redisCommands.zrevrangebyscoreWithScores(keyCodec.encode(key),
				range.convert(valueCodec.toEncodeConverter()), offset, count);
		return tupleDecoder.convert(tuples);
	}

	@Override
	public Long zrevrank(K key, V member) {
		return redisCommands.zrevrank(keyCodec.encode(key), valueCodec.encode(member));
	}

	@Override
	public Double zscore(K key, V member) {
		return redisCommands.zscore(keyCodec.encode(key), valueCodec.encode(member));
	}

	@Override
	public Collection<V> zunion(InterArgs interArgs, K... keys) {
		Collection<TV> values = redisCommands.zunion(interArgs, keyCodec.encode(keys));
		return valueCodec.toDecodeConverter().convert(values);
	}

	@Override
	public Collection<Tuple<V>> zunionWithScores(InterArgs interArgs, K... keys) {
		Collection<Tuple<TV>> tuples = redisCommands.zunionWithScores(interArgs, keyCodec.encode(keys));
		return tupleDecoder.convert(tuples);
	}

	@Override
	public Long zunionstore(K destinationKey, InterArgs interArgs, K... keys) {
		return redisCommands.zunionstore(keyCodec.encode(destinationKey), interArgs, keyCodec.encode(keys));
	}

	@Override
	public Long xack(K key, K group, K... ids) {
		return redisCommands.xack(keyCodec.encode(key), keyCodec.encode(group), keyCodec.encode(ids));
	}

	@Override
	public List<V> xclaim(K key, K group, K consumer, long minIdleTime, ClaimArgs args, K... ids) {
		List<TV> values = redisCommands.xclaim(keyCodec.encode(key), keyCodec.encode(group), keyCodec.encode(consumer),
				minIdleTime, args, keyCodec.encode(ids));
		return valueCodec.decode(values);
	}

	@Override
	public Long xdel(K key, K... ids) {
		return redisCommands.xdel(keyCodec.encode(key), keyCodec.encode(ids));
	}

	@Override
	public Long append(K key, V value) {
		return redisCommands.append(keyCodec.encode(key), valueCodec.encode(value));
	}

	@Override
	public Long bitcount(K key, long start, long end) {
		return redisCommands.bitcount(keyCodec.encode(key), start, end);
	}

	@Override
	public Long bitop(BitOP op, K destkey, K... srcKeys) {
		return redisCommands.bitop(op, keyCodec.encode(destkey), keyCodec.encode(srcKeys));
	}

	@Override
	public Long bitpos(K key, boolean bit, Long start, Long end) {
		return redisCommands.bitpos(keyCodec.encode(key), bit, start, end);
	}

	@Override
	public Long decr(K key) {
		return redisCommands.decr(keyCodec.encode(key));
	}

	@Override
	public Long decrBy(K key, long decrement) {
		return redisCommands.decrBy(keyCodec.encode(key), decrement);
	}

	@Override
	public V get(K key) {
		TV v = redisCommands.get(keyCodec.encode(key));
		return valueCodec.decode(v);
	}

	@Override
	public Boolean getbit(K key, Long offset) {
		return redisCommands.getbit(keyCodec.encode(key), offset);
	}

	@Override
	public V getdel(K key) {
		TV v = redisCommands.getdel(keyCodec.encode(key));
		return valueCodec.decode(v);
	}

	@Override
	public V getEx(K key, ExpireOption option, Long time) {
		TV v = redisCommands.getEx(keyCodec.encode(key), option, time);
		return valueCodec.decode(v);
	}

	@Override
	public V getrange(K key, long startOffset, long endOffset) {
		TV v = redisCommands.getrange(keyCodec.encode(key), startOffset, endOffset);
		return valueCodec.decode(v);
	}

	@Override
	public V getset(K key, V value) {
		TV v = redisCommands.getset(keyCodec.encode(key), valueCodec.encode(value));
		return valueCodec.decode(v);
	}

	@Override
	public Long incr(K key) {
		return redisCommands.incr(keyCodec.encode(key));
	}

	@Override
	public Long incrBy(K key, long increment) {
		return redisCommands.incrBy(keyCodec.encode(key), increment);
	}

	@Override
	public Double incrByFloat(K key, double increment) {
		return redisCommands.incrByFloat(keyCodec.encode(key), increment);
	}

	@Override
	public List<V> mget(K... keys) {
		List<TV> values = redisCommands.mget(keyCodec.encode(keys));
		return valueCodec.decode(values);
	}

	@Override
	public Boolean mset(Map<K, V> pairs) {
		return redisCommands
				.mset(CollectionFactory.convert(pairs, keyCodec.toEncodeConverter(), valueCodec.toEncodeConverter()));
	}

	@Override
	public Long msetnx(Map<K, V> pairs) {
		return redisCommands
				.msetnx(CollectionFactory.convert(pairs, keyCodec.toEncodeConverter(), valueCodec.toEncodeConverter()));
	}

	@Override
	public Boolean psetex(K key, long milliseconds, V value) {
		return redisCommands.psetex(keyCodec.encode(key), milliseconds, valueCodec.encode(value));
	}

	@Override
	public Boolean set(K key, V value, ExpireOption option, long time, SetOption setOption) {
		return redisCommands.set(keyCodec.encode(key), valueCodec.encode(value), option, time, setOption);
	}

	@Override
	public Boolean setbit(K key, long offset, boolean value) {
		return redisCommands.setbit(keyCodec.encode(key), offset, value);
	}

	@Override
	public Boolean setex(K key, long seconds, V value) {
		return redisCommands.setex(keyCodec.encode(key), seconds, valueCodec.encode(value));
	}

	@Override
	public Boolean setNX(K key, V value) {
		return redisCommands.setNX(keyCodec.encode(key), valueCodec.encode(value));
	}

	@Override
	public Long setrange(K key, Long offset, V value) {
		return redisCommands.setrange(keyCodec.encode(key), offset, valueCodec.encode(value));
	}

	@Override
	public Long strlen(K key) {
		return redisCommands.strlen(keyCodec.encode(key));
	}

	@Override
	public Long sadd(K key, V... members) {
		return redisCommands.sadd(keyCodec.encode(key), valueCodec.encode(members));
	}

	@Override
	public Long scard(K key) {
		return redisCommands.scard(keyCodec.encode(key));
	}

	@Override
	public Set<V> sdiff(K... keys) {
		Set<TV> vs = redisCommands.sdiff(keyCodec.encode(keys));
		return valueCodec.toDecodeConverter().convert(vs);
	}

	@Override
	public Long sdiffstore(K destinationKey, K... keys) {
		return redisCommands.sdiffstore(keyCodec.encode(destinationKey), keyCodec.encode(keys));
	}

	@Override
	public Set<V> sinter(K... keys) {
		Set<TV> vs = redisCommands.sinter(keyCodec.encode(keys));
		return valueCodec.toDecodeConverter().convert(vs);
	}

	@Override
	public Long sinterstore(K destinationKey, K... keys) {
		return redisCommands.sinterstore(keyCodec.encode(destinationKey), keyCodec.encode(keys));
	}

	@Override
	public Boolean sismember(K key, V member) {
		return redisCommands.sismember(keyCodec.encode(key), valueCodec.encode(member));
	}

	@Override
	public Set<V> smembers(K key) {
		Set<TV> vs = redisCommands.smembers(keyCodec.encode(key));
		return valueCodec.toDecodeConverter().convert(vs);
	}

	@Override
	public List<Boolean> smismember(K key, V... members) {
		return redisCommands.smismember(keyCodec.encode(key), valueCodec.encode(members));
	}

	@Override
	public Boolean sMove(K sourceKey, K destinationKey, V member) {
		return redisCommands.sMove(keyCodec.encode(sourceKey), keyCodec.encode(destinationKey),
				valueCodec.encode(member));
	}

	@Override
	public Set<V> spop(K key, int count) {
		Set<TV> vs = redisCommands.spop(keyCodec.encode(key), count);
		return valueCodec.toDecodeConverter().convert(vs);
	}

	@Override
	public List<V> srandmember(K key, int count) {
		List<TV> vs = redisCommands.srandmember(keyCodec.encode(key), count);
		return valueCodec.decode(vs);
	}

	@Override
	public Long srem(K key, V... members) {
		return redisCommands.srem(keyCodec.encode(key), valueCodec.encode(members));
	}

	@Override
	public Set<V> sunion(K... keys) {
		Set<TV> vs = redisCommands.sunion(keyCodec.encode(keys));
		return valueCodec.toDecodeConverter().convert(vs);
	}

	@Override
	public Long sunionstore(K destinationKey, K... keys) {
		return redisCommands.sunionstore(keyCodec.encode(destinationKey), keyCodec.encode(keys));
	}

	@Override
	public Cursor<K> sScan(long cursorId, K key, ScanOptions<K> options) {
		Cursor<TK> cursor = redisCommands.sScan(cursorId, keyCodec.encode(key),
				options.convert(keyCodec.toEncodeConverter()));
		return new ConvertibleCursor<TK, K>(cursor, keyCodec.toDecodeConverter());
	}

	@Override
	public void discard() {
		redisCommands.discard();
	}

	@Override
	public List<Object> exec() {
		return redisCommands.exec();
	}

	@Override
	public void multi() {
		redisCommands.multi();
	}

	@Override
	public void unwatch() {
		redisCommands.unwatch();
	}

	@Override
	public void watch(K... keys) {
		redisCommands.watch(keyCodec.encode(keys));
	}

}
