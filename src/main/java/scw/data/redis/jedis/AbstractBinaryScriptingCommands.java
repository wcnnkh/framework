package scw.data.redis.jedis;

import java.util.List;

import redis.clients.jedis.BinaryScriptingCommands;
import scw.data.redis.ResourceManager;

public abstract class AbstractBinaryScriptingCommands
		implements ResourceManager<BinaryScriptingCommands>, scw.data.redis.BinaryScriptingCommands {

	public Object eval(byte[] script, byte[] keyCount, byte[]... params) {
		BinaryScriptingCommands commands = null;
		try {
			commands = getResource();
			return commands.eval(script, keyCount, params);
		} finally {
			close(commands);
		}
	}

	public Object eval(byte[] script, int keyCount, byte[]... params) {
		BinaryScriptingCommands commands = null;
		try {
			commands = getResource();
			return commands.eval(script, keyCount, params);
		} finally {
			close(commands);
		}
	}

	public Object eval(byte[] script, List<byte[]> keys, List<byte[]> args) {
		BinaryScriptingCommands commands = null;
		try {
			commands = getResource();
			return commands.eval(script, keys, args);
		} finally {
			close(commands);
		}
	}

	public Object eval(byte[] script) {
		BinaryScriptingCommands commands = null;
		try {
			commands = getResource();
			return commands.eval(script);
		} finally {
			close(commands);
		}
	}

	public Object evalsha(byte[] script) {
		BinaryScriptingCommands commands = null;
		try {
			commands = getResource();
			return commands.evalsha(script);
		} finally {
			close(commands);
		}
	}

	public Object evalsha(byte[] sha1, List<byte[]> keys, List<byte[]> args) {
		BinaryScriptingCommands commands = null;
		try {
			commands = getResource();
			return commands.evalsha(sha1, keys, args);
		} finally {
			close(commands);
		}
	}

	public Object evalsha(byte[] sha1, int keyCount, byte[]... params) {
		BinaryScriptingCommands commands = null;
		try {
			commands = getResource();
			return commands.evalsha(sha1, keyCount, params);
		} finally {
			close(commands);
		}
	}

	public List<Long> scriptExists(byte[]... sha1) {
		BinaryScriptingCommands commands = null;
		try {
			commands = getResource();
			return commands.scriptExists(sha1);
		} finally {
			close(commands);
		}
	}

	public byte[] scriptLoad(byte[] script) {
		BinaryScriptingCommands commands = null;
		try {
			commands = getResource();
			return commands.scriptLoad(script);
		} finally {
			close(commands);
		}
	}

	public String scriptFlush() {
		BinaryScriptingCommands commands = null;
		try {
			commands = getResource();
			return commands.scriptFlush();
		} finally {
			close(commands);
		}
	}

	public String scriptKill() {
		BinaryScriptingCommands commands = null;
		try {
			commands = getResource();
			return commands.scriptKill();
		} finally {
			close(commands);
		}
	}
}
