package run.soeasy.framework.core.exchange.container;

import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Receipt;

public class MappedContainer<S, T, R extends PayloadRegistration<S>, W extends Container<S, R>>
		extends MappedRegistry<S, T, W> implements Container<T, PayloadRegistration<T>> {

	public MappedContainer(W regisry, Codec<T, S> codec) {
		super(regisry, codec);
	}

	@Override
	public Elements<PayloadRegistration<T>> getElements() {
		return getRegistry().getElements().map((e) -> e.map(getCodec()::decode));
	}

	@Override
	public Receipt deregisters(Elements<? extends T> elements) {
		Elements<S> target = getCodec().encodeAll(elements);
		return getRegistry().deregisters(target);
	}

	@Override
	public boolean isEmpty() {
		return super.isEmpty();
	}
}