package io.basc.framework.redis.convert;

import io.basc.framework.redis.MessageListener;
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
				getKeyCodec(), getValueCodec()::encode);
	}

	@Override
	default Long publish(K channel, V message) {
		SK k = getKeyCodec().encode(channel);
		SV m = getValueCodec().encode(message);
		return getSourceRedisPubSubCommands().publish(k, m);
	}

	@Override
	default void subscribe(MessageListener<K, V> listener, K... channels) {
		SK[] ks = getKeyCodec().encodeAll(channels);
		MessageListener<SK, SV> messageListener = new ConvertibleMessageListener<K, V, SK, SV>(listener,
				getKeyCodec()::decode, getValueCodec()::decode);
		getSourceRedisPubSubCommands().subscribe(messageListener, ks);
	}

	@Override
	default void pSubscribe(MessageListener<K, V> listener, K... patterns) {
		SK[] ks = getKeyCodec().encodeAll(patterns);
		MessageListener<SK, SV> messageListener = new ConvertibleMessageListener<K, V, SK, SV>(listener,
				getKeyCodec()::decode, getValueCodec()::decode);
		getSourceRedisPubSubCommands().pSubscribe(messageListener, ks);
	}
}