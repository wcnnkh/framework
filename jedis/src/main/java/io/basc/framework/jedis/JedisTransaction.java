package io.basc.framework.jedis;

import java.util.List;

import io.basc.framework.redis.RedisTransaction;
import redis.clients.jedis.Transaction;

public class JedisTransaction extends JedisPipelineCommands<Transaction> implements RedisTransaction<byte[], byte[]> {

	public JedisTransaction(Transaction commands) {
		super(commands);
	}

	@Override
	public String discard() {
		return commands.discard();
	}

	@Override
	public List<Object> exec() {
		return commands.exec();
	}

	@Override
	public RedisTransaction<byte[], byte[]> multi() {
		return this;
	}

	@Override
	public String unwatch() {
		return commands.unwatch();
	}

	@Override
	public String watch(byte[]... keys) {
		return commands.watch(keys);
	}
}
