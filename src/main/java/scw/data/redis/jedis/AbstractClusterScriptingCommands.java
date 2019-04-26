package scw.data.redis.jedis;

import java.util.List;

import redis.clients.jedis.JedisClusterScriptingCommands;
import scw.data.redis.ResourceManager;
import scw.data.redis.ScriptingCommands;

public abstract class AbstractClusterScriptingCommands
		implements ResourceManager<JedisClusterScriptingCommands>, ScriptingCommands {

	public Object eval(String script, int keyCount, String... params) {
		JedisClusterScriptingCommands commands = null;
		try {
			commands = getResource();
			return commands.eval(script, keyCount, params);
		} finally {
			close(commands);
		}
	}

	public Object eval(String script, List<String> keys, List<String> args) {
		JedisClusterScriptingCommands commands = null;
		try {
			commands = getResource();
			return commands.eval(script, keys, args);
		} finally {
			close(commands);
		}
	}

	public Object evalsha(String sha1, List<String> keys, List<String> args) {
		JedisClusterScriptingCommands commands = null;
		try {
			commands = getResource();
			return commands.evalsha(sha1, keys, args);
		} finally {
			close(commands);
		}
	}

	public Object evalsha(String sha1, int keyCount, String... params) {
		JedisClusterScriptingCommands commands = null;
		try {
			commands = getResource();
			return commands.evalsha(sha1, keyCount, params);
		} finally {
			close(commands);
		}
	}
}
