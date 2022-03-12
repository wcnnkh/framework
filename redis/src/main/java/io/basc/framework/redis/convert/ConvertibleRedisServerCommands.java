package io.basc.framework.redis.convert;

import java.util.List;

import io.basc.framework.redis.FailoverParams;
import io.basc.framework.redis.FlushMode;
import io.basc.framework.redis.Module;
import io.basc.framework.redis.RedisServerCommands;
import io.basc.framework.redis.SaveMode;
import io.basc.framework.redis.Slowlog;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisServerCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisServerCommands<K, V> {

	RedisServerCommands<SK, SV> getSourceRedisServerCommands();

	@Override
	default List<K> aclCat(K categoryname) {
		List<SK> list = getSourceRedisServerCommands().aclCat(getKeyCodec().encode(categoryname));
		return getKeyCodec().decodeAll(list);
	}

	@Override
	default Long aclDelUser(K username, K... usernames) {
		return getSourceRedisServerCommands().aclDelUser(getKeyCodec().encode(username),
				getKeyCodec().encodeAll(usernames));
	}

	@Override
	default String aclGenPass(Integer bits) {
		return getSourceRedisServerCommands().aclGenPass(bits);
	}

	@Override
	default List<K> aclList() {
		List<SK> list = getSourceRedisServerCommands().aclList();
		return getKeyCodec().decodeAll(list);
	}

	@Override
	default String aclLoad() {
		return getSourceRedisServerCommands().aclLoad();
	}

	@Override
	default List<K> aclLog(Integer count) {
		List<SK> list = getSourceRedisServerCommands().aclLog(count);
		return getKeyCodec().decodeAll(list);
	}

	@Override
	default String aclLogReset() {
		return getSourceRedisServerCommands().aclLogReset();
	}

	@Override
	default String aclSave() {
		return getSourceRedisServerCommands().aclSave();
	}

	@Override
	default String aclSetuser(K username, K... rules) {
		return getSourceRedisServerCommands().aclSetuser(getKeyCodec().encode(username), getKeyCodec().encodeAll(rules));
	}

	@Override
	default List<K> aclUsers() {
		List<SK> list = getSourceRedisServerCommands().aclUsers();
		return getKeyCodec().decodeAll(list);
	}

	@Override
	default K aclWhoami() {
		SK v = getSourceRedisServerCommands().aclWhoami();
		return getKeyCodec().decode(v);
	}

	@Override
	default String bgrewriteaof() {
		return getSourceRedisServerCommands().bgrewriteaof();
	}

	@Override
	default String bgsave() {
		return getSourceRedisServerCommands().bgsave();
	}

	@Override
	default List<V> configGet(K parameter) {
		List<SV> list = getSourceRedisServerCommands().configGet(getKeyCodec().encode(parameter));
		return getValueCodec().decodeAll(list);
	}

	@Override
	default String configResetstat() {
		return getSourceRedisServerCommands().configResetstat();
	}

	@Override
	default String configRewrite() {
		return getSourceRedisServerCommands().configRewrite();
	}

	@Override
	default String configSet(K parameter, V value) {
		return getSourceRedisServerCommands().configSet(getKeyCodec().encode(parameter), getValueCodec().encode(value));
	}

	@Override
	default Long dbsize() {
		return getSourceRedisServerCommands().dbsize();
	}

	@Override
	default String failover(FailoverParams params) {
		return getSourceRedisServerCommands().failover(params);
	}

	@Override
	default String failoverAbort() {
		return getSourceRedisServerCommands().failoverAbort();
	}

	@Override
	default String flushall(FlushMode flushMode) {
		return getSourceRedisServerCommands().flushall(flushMode);
	}

	@Override
	default String flushdb(FlushMode flushMode) {
		return getSourceRedisServerCommands().flushdb(flushMode);
	}

	@Override
	default String info(String section) {
		return getSourceRedisServerCommands().info(section);
	}

	@Override
	default Long lastsave() {
		return getSourceRedisServerCommands().lastsave();
	}

	@Override
	default String memoryDoctor() {
		return getSourceRedisServerCommands().memoryDoctor();
	}

	@Override
	default Long memoryUsage(K key, int samples) {
		return getSourceRedisServerCommands().memoryUsage(getKeyCodec().encode(key), samples);
	}

	@Override
	default List<Module> moduleList() {
		return getSourceRedisServerCommands().moduleList();
	}

	@Override
	default String moduleLoad(String path) {
		return getSourceRedisServerCommands().moduleLoad(path);
	}

	@Override
	default String moduleUnload(String name) {
		return getSourceRedisServerCommands().moduleUnload(name);
	}

	@Override
	default List<Object> role() {
		return getSourceRedisServerCommands().role();
	}

	@Override
	default String save() {
		return getSourceRedisServerCommands().save();
	}

	@Override
	default void shutdown(SaveMode saveMode) {
		getSourceRedisServerCommands().shutdown(saveMode);
	}

	@Override
	default String slaveof(String host, int port) {
		return getSourceRedisServerCommands().slaveof(host, port);
	}

	@Override
	default List<Slowlog> slowlogGet(Long count) {
		return getSourceRedisServerCommands().slowlogGet(count);
	}

	@Override
	default Long slowlogLen() {
		return getSourceRedisServerCommands().slowlogLen();
	}

	@Override
	default String slowlogReset() {
		return getSourceRedisServerCommands().slowlogReset();
	}

	@Override
	default String swapdb(int index1, int index2) {
		return getSourceRedisServerCommands().swapdb(index1, index2);
	}

	@Override
	default List<String> time() {
		return getSourceRedisServerCommands().time();
	}
}
