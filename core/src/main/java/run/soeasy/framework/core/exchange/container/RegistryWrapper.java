package run.soeasy.framework.core.exchange.container;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.ElementsWrapper;
import run.soeasy.framework.core.exchange.Registration;

public interface RegistryWrapper<E, W extends Registry<E>> extends Registry<E>, ElementsWrapper<E, W> {
	@Override
	default Registration registers(Elements<? extends E> elements) throws RegistrationException {
		return getSource().registers(elements);
	}

	@Override
	default Registration register(E element) throws RegistrationException {
		return getSource().register(element);
	}
}