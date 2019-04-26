package scw.data.redis;

import java.util.List;
import java.util.Set;

public interface MultiKeyCommands {
	Long del(String... keys);

	Long exists(String... keys);

	List<String> blpop(int timeout, String... keys);

	List<String> brpop(int timeout, String... keys);

	List<String> mget(String... keys);

	String mset(String... keysvalues);

	Long msetnx(String... keysvalues);

	String rename(String oldkey, String newkey);

	Long renamenx(String oldkey, String newkey);

	String rpoplpush(String srckey, String dstkey);

	Set<String> sdiff(String... keys);

	Long sdiffstore(String dstkey, String... keys);

	Set<String> sinter(String... keys);

	Long sinterstore(String dstkey, String... keys);

	Long smove(String srckey, String dstkey, String member);

	Long sort(String key, String dstkey);

	Set<String> sunion(String... keys);

	Long sunionstore(String dstkey, String... keys);

	Long zinterstore(String dstkey, String... sets);

	Long zunionstore(String dstkey, String... sets);

	String brpoplpush(String source, String destination, int timeout);

	Long publish(String channel, String message);

	String pfmerge(final String destkey, final String... sourcekeys);

	long pfcount(final String... keys);
}
