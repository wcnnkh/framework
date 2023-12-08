package io.basc.framework.beans.factory.spi;

import io.basc.framework.util.element.Elements;
import io.basc.framework.util.element.ServiceLoader;
import lombok.Data;
import lombok.NonNull;

@Data
public class JdkServiceLoader<S> implements ServiceLoader<S> {
	@NonNull
	private final java.util.ServiceLoader<S> serviceLoader;

	@Override
	public void reload() {
		serviceLoader.reload();
	}

	@Override
	public Elements<S> getServices() {
		return Elements.of(serviceLoader);
	}
}
