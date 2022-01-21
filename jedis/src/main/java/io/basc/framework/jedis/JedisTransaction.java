package io.basc.framework.jedis;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.basc.framework.redis.RedisSystemException;
import io.basc.framework.redis.RedisTransaction;
import redis.clients.jedis.Transaction;

public class JedisTransaction extends JedisPipelineCommands<Transaction> implements RedisTransaction<byte[], byte[]> {
	private AtomicBoolean active = new AtomicBoolean(true);

	public JedisTransaction(Transaction commands) {
		super(commands);
	}

	@Override
	public String discard() {
		if (active.compareAndSet(true, false)) {
			return commands.discard();
		}
		throw new RedisSystemException("The transaction has been discarded");
	}

	@Override
	public List<Object> exec() {
		if (!active.get()) {
			throw new RedisSystemException("The transaction has been discarded");
		}

		try {
			return commands.exec();
		} finally {
			responseDown();
		}
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

	@Override
	public boolean isAlive() {
		return active.get();
	}
}
