package run.soeasy.framework.core.scan;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.util.collections.Elements;
import run.soeasy.framework.util.function.Selector;
import run.soeasy.framework.util.spi.ConfigurableServices;

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
