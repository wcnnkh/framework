package io.basc.framework.jedis;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.basc.framework.redis.RedisPipeline;
import io.basc.framework.redis.RedisSystemException;
import redis.clients.jedis.Pipeline;

public class JedisPipeline extends JedisPipelineCommands<Pipeline> implements RedisPipeline<byte[], byte[]> {
	private AtomicBoolean closed = new AtomicBoolean(false);

	public JedisPipeline(Pipeline commands) {
		super(commands);
	}

	@Override
	public void close() {
		if (closed.compareAndSet(false, true)) {
			commands.close();
		}
	}

	@Override
	public List<Object> exec() throws RedisSystemException {
		if (closed.get()) {
			throw new RedisSystemException("The pipeline has been closed");
		}

		try {
			return commands.syncAndReturnAll();
		} finally {
			responseDown();
		}
	}

	@Override
	public boolean isClosed() {
		return closed.get();
	}
}
