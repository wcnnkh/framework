package io.basc.framework.convert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.util.Assert;

public class SimpleInverterFactory<R, E extends Throwable> extends SimpleConverterFactory<R, E>
		implements InverterFactory<R, E> {
	private final Map<Class<?>, Inverter<?, ? extends R, ? extends E>> map = new ConcurrentHashMap<>();

	@Override
	public boolean isInverterRegistred(Class<?> type) {
		return map.containsKey(type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S> Inverter<S, R, E> getInverter(Class<? extends S> type) {
		return (Inverter<S, R, E>) map.get(type);
	}

	@Override
	public <S> void registerInverter(Class<S> type, Inverter<? extends S, ? extends R, ? extends E> inverter) {
		Assert.requiredArgument(type != null, "type");
		Assert.requiredArgument(inverter != null, "inverter");
		map.put(type, inverter);
	}

	@Override
	public Object newInstance(TypeDescriptor type) {
		return super.newInstance(type);
	}
}