package io.basc.framework.io.scan;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.util.Elements;
import io.basc.framework.util.select.Selector;
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
		return getServices().anyMatch((e) -> e.canScan(location));
	}

	@Override
	public Elements<S> scan(String locationPattern) {
		return getSelector()
				.apply(getServices().filter((e) -> e.canScan(locationPattern)).map((e) -> e.scan(locationPattern)));
	}

	@Override
	public Elements<S> scan(String location, ResourceFilter filter) {
		return getSelector()
				.apply(getServices().filter((e) -> e.canScan(location)).map((e) -> e.scan(location, filter)));
	}
}
