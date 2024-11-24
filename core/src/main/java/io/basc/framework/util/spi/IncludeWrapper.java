package io.basc.framework.util.spi;

import io.basc.framework.util.RegistrationWrapper;
import io.basc.framework.util.ServiceLoaderWrapper;

public interface IncludeWrapper<S, W extends Include<S>>
		extends Include<S>, RegistrationWrapper<W>, ServiceLoaderWrapper<S, W> {
	@Override
	default void reload() {
		getSource().reload();
	}
}
