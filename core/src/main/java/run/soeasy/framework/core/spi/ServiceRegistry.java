package run.soeasy.framework.core.spi;

import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListSet;

import lombok.NonNull;
import run.soeasy.framework.core.exchange.CollectionContainer;

public class ServiceRegistry<S> extends CollectionContainer<S, ConcurrentSkipListSet<S>> {
	
	public ServiceRegistry(@NonNull Comparator<? super S> comparator) {
		super(new ConcurrentSkipListSet<>(comparator));
	}
}
