package io.basc.framework.util.spi;

import io.basc.framework.util.Receipted;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.ServiceLoaderWrapper;

public class Included<S> extends Receipted implements Configured<S>, ServiceLoaderWrapper<S, ServiceLoader<S>> {
	private static final long serialVersionUID = 1L;
	private final ServiceLoader<S> source;

	public Included(boolean done, boolean success, Throwable cause) {
		this(done, success, cause, ServiceLoader.empty());
	}

	public Included(boolean done, boolean success, Throwable cause, ServiceLoader<S> source) {
		super(done, success, cause);
		this.source = source;
	}

	@Override
	public void reload() {
		source.reload();
	}

	@Override
	public ServiceLoader<S> getSource() {
		return source;
	}
}
