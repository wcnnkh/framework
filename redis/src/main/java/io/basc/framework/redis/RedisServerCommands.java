package io.basc.framework.redis;

import java.util.List;

import io.basc.framework.lang.Nullable;

@SuppressWarnings("unchecked")
public interface RedisServerCommands<K, V> {
	default List<K> aclCat() {
		return aclCat(null);
	}

	List<K> aclCat(@Nullable K categoryname);

	Long aclDelUser(K username, K... usernames);

	default String aclGenPass() {
		return aclGenPass(null);
	}

	String aclGenPass(@Nullable Integer bits);

	List<K> aclList();

	String aclLoad();

	default List<K> aclLog() {
		return aclLog(null);
	}

	List<K> aclLog(@Nullable Integer count);

	String aclLogReset();

	String aclSave();

	String aclSetuser(K username, K... rules);

	List<K> aclUsers();

	K aclWhoami();

	String bgrewriteaof();

	String bgsave();

	List<V> configGet(K parameter);

	String configResetstat();

	String configRewrite();

	String configSet(K parameter, V value);

	Long dbsize();

	default String failover() {
		return failover(null);
	}

	String failoverAbort();

	String failover(@Nullable FailoverParams params);

	default String flushall() {
		return flushall(null);
	}

	String flushall(@Nullable FlushMode flushMode);

	default String flushdb() {
		return flushdb(null);
	}

	String flushdb(@Nullable FlushMode flushMode);

	default String info() {
		return info(null);
	}

	String info(@Nullable String section);

	Long lastsave();

	String memoryDoctor();

	default Long memoryUsage(K key) {
		return memoryUsage(key, 0);
	}

	Long memoryUsage(K key, int samples);

	List<Module> moduleList();

	String moduleLoad(String path);

	String moduleUnload(String name);

	List<Object> role();

	String save();

	default void shutdown() {
		shutdown(null);
	}

	void shutdown(@Nullable SaveMode saveMode);

	String slaveof(String host, int port);

	default List<Slowlog> slowlogGet() {
		return slowlogGet(null);
	}

	List<Slowlog> slowlogGet(@Nullable Long count);

	Long slowlogLen();

	String slowlogReset();

	String swapdb(int index1, int index2);

	List<String> time();
}
