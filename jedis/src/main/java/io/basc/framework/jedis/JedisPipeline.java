package io.basc.framework.jedis;

import java.util.List;

import io.basc.framework.redis.RedisPipeline;
import io.basc.framework.redis.RedisSystemException;
import redis.clients.jedis.Pipeline;

public class JedisPipeline extends JedisPipelineCommands<Pipeline> implements RedisPipeline<byte[], byte[]> {

	public JedisPipeline(Pipeline commands) {
		super(commands);
	}

	@Override
	public void close() {
		commands.close();
	}

	@Override
	public List<Object> exec() throws RedisSystemException {
		return commands.syncAndReturnAll();
	}
}
