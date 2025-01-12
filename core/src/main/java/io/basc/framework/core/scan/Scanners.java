package io.basc.framework.core.scan;

import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.function.Selector;
import io.basc.framework.util.spi.ConfigurableServices;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class Scanners<T extends Scanner<S>, S> extends ConfigurableServices<T> implements Scanner<S> {
	@NonNull
	private Selector<Elements<S>> selector = Selector.first();

	@Override
	public boolean canScan(String location) {
		return anyMatch((e) -> e.canScan(location));
	}

	@Override
	public Elements<S> scan(String locationPattern) {
		return getSelector().apply(this.filter((e) -> e.canScan(locationPattern)).map((e) -> e.scan(locationPattern)));
	}

	@Override
	public Elements<S> scan(String location, ResourceFilter filter) {
		return getSelector().apply(this.filter((e) -> e.canScan(location)).map((e) -> e.scan(location, filter)));
	}
}
