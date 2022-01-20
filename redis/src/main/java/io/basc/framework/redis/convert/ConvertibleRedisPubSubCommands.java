package io.basc.framework.redis.convert;

import io.basc.framework.redis.MessageListener;
import io.basc.framework.redis.RedisCodec;
import io.basc.framework.redis.RedisPubSubCommands;
import io.basc.framework.redis.Subscription;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisPubSubCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisPubSubCommands<K, V> {

	RedisPubSubCommands<SK, SV> getSourceRedisPubSubCommands();

	@Override
	default boolean isSubscribed() {
		return getSourceRedisPubSubCommands().isSubscribed();
	}

	@Override
	default Subscription<K, V> getSubscription() {
		return new ConvertibleSubscription<SK, SV, K, V>(getSourceRedisPubSubCommands().getSubscription(),
				getKeyCodec(), getValueCodec().toEncodeConverter());
	}

	@Override
	default Long publish(K channel, V message) {
		SK k = getKeyCodec().encode(channel);
		SV m = getValueCodec().encode(message);
		return getSourceRedisPubSubCommands().publish(k, m);
	}

	@Override
	default void subscribe(MessageListener<K, V> listener, K... channels) {
		SK[] ks = getKeyCodec().encode(channels);
		MessageListener<SK, SV> messageListener = new ConvertibleMessageListener<K, V, SK, SV>(listener,
				getKeyCodec().toDecodeConverter(), getValueCodec().toDecodeConverter());
		getSourceRedisPubSubCommands().subscribe(messageListener, ks);
	}

	@Override
	default void pSubscribe(MessageListener<K, V> listener, K... patterns) {
		SK[] ks = getKeyCodec().encode(patterns);
		MessageListener<SK, SV> messageListener = new ConvertibleMessageListener<K, V, SK, SV>(listener,
				getKeyCodec().toDecodeConverter(), getValueCodec().toDecodeConverter());
		getSourceRedisPubSubCommands().pSubscribe(messageListener, ks);
	}
}