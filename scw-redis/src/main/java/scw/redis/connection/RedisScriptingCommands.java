package scw.redis.connection;

import java.util.List;

public interface RedisScriptingCommands {
	Object eval(byte[] script, int numberKeys, byte[]... args);

	Object evalsha(byte[] sha1, int numberKeys, byte[]... args);

	static enum DebugType {
		YES, SYNC, NO
	}

	/**
	 * https://redis.io/commands/script-debug
	 * 
	 * @param type
	 * @return Simple string reply: OK.
	 */
	byte[] scriptDebug(DebugType type);

	List<Integer> scriptexists(byte[] sha1);

	static enum FlushType {
		ASYNC, SYNC
	}

	/**
	 * https://redis.io/commands/script-flush
	 * 
	 * @param type
	 * @return Simple string reply
	 */
	byte[] scriptFlush(FlushType type);

	/**
	 * https://redis.io/commands/script-kill
	 * 
	 * @return
	 */
	byte[] scriptKill();

	/**
	 * https://redis.io/commands/script-load
	 * 
	 * @param script
	 * @return Bulk string reply This command returns the SHA1 digest of the script
	 *         added into the script cache.
	 */
	byte[] scriptLoad(byte[] script);
}
