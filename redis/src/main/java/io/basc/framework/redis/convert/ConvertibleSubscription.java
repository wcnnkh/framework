package io.basc.framework.redis.convert;

import io.basc.framework.codec.Codec;
import io.basc.framework.convert.Converter;
import io.basc.framework.redis.MessageListener;
import io.basc.framework.redis.RedisInvalidSubscriptionException;
import io.basc.framework.redis.Subscription;

import java.util.Collection;
import java.util.LinkedHashSet;

@SuppressWarnings("unchecked")
public class ConvertibleSubscription<TK, TV, K, V> implements Subscription<K, V> {
	private final Subscription<TK, TV> subscription;
	private final Codec<K, TK> keyCodec;
	private final Converter<V, TV> valueConverter;

	public ConvertibleSubscription(Subscription<TK, TV> subscription, Codec<K, TK> keyCodec,
			Converter<V, TV> valueConverter) {
		this.subscription = subscription;
		this.keyCodec = keyCodec;
		this.valueConverter = valueConverter;
	}

	@Override
	public void subscribe(K... channels) throws RedisInvalidSubscriptionException {
		TK[] cs = keyCodec.encode(channels);
		subscription.subscribe(cs);
		;
	}

	@Override
	public void pSubscribe(K... patterns) throws RedisInvalidSubscriptionException {
		TK[] cs = keyCodec.encode(patterns);
		subscription.pSubscribe(cs);
	}

	@Override
	public void unsubscribe() {
		subscription.unsubscribe();
	}

	@Override
	public void unsubscribe(K... channels) {
		TK[] cs = keyCodec.encode(channels);
		subscription.unsubscribe(cs);
	}

	@Override
	public void pUnsubscribe() {
		subscription.pUnsubscribe();
	}

	@Override
	public void pUnsubscribe(K... patterns) {
		TK[] cs = keyCodec.encode(patterns);
		subscription.pUnsubscribe(cs);
	}

	@Override
	public Collection<K> getChannels() {
		Collection<TK> ks = subscription.getChannels();
		return keyCodec.toDecodeConverter().convert(ks, new LinkedHashSet<K>(ks.size()));
	}

	@Override
	public Collection<K> getPatterns() {
		Collection<TK> ks = subscription.getPatterns();
		return keyCodec.toDecodeConverter().convert(ks, new LinkedHashSet<K>(ks.size()));
	}

	@Override
	public MessageListener<K, V> getListener() {
		return new ConvertibleMessageListener<TK, TV, K, V>(subscription.getListener(), keyCodec.toEncodeConverter(),
				valueConverter);
	}

	@Override
	public boolean isAlive() {
		return subscription.isAlive();
	}

	@Override
	public void close() {
		subscription.close();
	}

}
