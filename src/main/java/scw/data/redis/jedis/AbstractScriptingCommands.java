package scw.data.redis.jedis;

import java.util.List;

import redis.clients.jedis.ScriptingCommands;
import scw.data.redis.ResourceManager;

public abstract class AbstractScriptingCommands
		implements ResourceManager<ScriptingCommands>, scw.data.redis.ScriptingCommands {

	public Object eval(String script, int keyCount, String... params) {
		ScriptingCommands commands = null;
		try {
			commands = getResource();
			return commands.eval(script, keyCount, params);
		} finally {
			close(commands);
		}
	}

	public Object eval(String script, List<String> keys, List<String> args) {
		ScriptingCommands commands = null;
		try {
			commands = getResource();
			return commands.eval(script, keys, args);
		} finally {
			close(commands);
		}
	}

	public Object eval(String script) {
		ScriptingCommands commands = null;
		try {
			commands = getResource();
			return commands.eval(script);
		} finally {
			close(commands);
		}
	}

	public Object evalsha(String script) {
		ScriptingCommands commands = null;
		try {
			commands = getResource();
			return commands.evalsha(script);
		} finally {
			close(commands);
		}
	}

	public Object evalsha(String sha1, List<String> keys, List<String> args) {
		ScriptingCommands commands = null;
		try {
			commands = getResource();
			return commands.evalsha(sha1, keys, args);
		} finally {
			close(commands);
		}
	}

	public Object evalsha(String sha1, int keyCount, String... params) {
		ScriptingCommands commands = null;
		try {
			commands = getResource();
			return commands.evalsha(sha1, keyCount, params);
		} finally {
			close(commands);
		}
	}

	public Boolean scriptExists(String sha1) {
		ScriptingCommands commands = null;
		try {
			commands = getResource();
			return commands.scriptExists(sha1);
		} finally {
			close(commands);
		}
	}

	public List<Boolean> scriptExists(String... sha1) {
		ScriptingCommands commands = null;
		try {
			commands = getResource();
			return commands.scriptExists(sha1);
		} finally {
			close(commands);
		}
	}

	public String scriptLoad(String script) {
		ScriptingCommands commands = null;
		try {
			commands = getResource();
			return commands.scriptLoad(script);
		} finally {
			close(commands);
		}
	}
}
