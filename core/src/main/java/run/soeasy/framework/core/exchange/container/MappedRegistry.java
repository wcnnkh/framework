package run.soeasy.framework.core.exchange.container;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.ElementsWrapper;
import run.soeasy.framework.core.exchange.Registration;

@Data
public class MappedRegistry<S, T, W extends Registry<S>> implements Registry<T>, ElementsWrapper<T, Elements<T>> {
	@NonNull
	private final W registry;
	@NonNull
	private final Codec<T, S> codec;

	@Override
	public Registration register(T element) throws RegistrationException {
		S target = codec.encode(element);
		return registry.register(target);
	}

	@Override
	public Elements<T> getSource() {
		return registry.map(codec::decode);
	}

	@Override
	public Registration registers(Elements<? extends T> elements) throws RegistrationException {
		Elements<S> target = codec.encodeAll(elements);
		return registry.registers(target);
	}
}