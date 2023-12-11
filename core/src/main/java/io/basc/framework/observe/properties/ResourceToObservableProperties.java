package io.basc.framework.observe.properties;

import java.nio.charset.Charset;
import java.util.Properties;

import io.basc.framework.io.Resource;
import io.basc.framework.io.resolver.PropertiesResolver;
import io.basc.framework.observe.ChangeEvent;
import io.basc.framework.observe.ChangeType;
import io.basc.framework.observe.value.ObservableValue;
import io.basc.framework.observe.watch.ResourcePollingObserver;
import io.basc.framework.observe.watch.ResourceWatcher;
import io.basc.framework.util.Registration;

public class ResourceToObservableProperties extends ResourcePollingObserver implements ObservableValue<Properties> {
	private volatile Charset charset;
	private final Properties properties;
	private final PropertiesResolver propertiesResolver;

	public ResourceToObservableProperties(Resource resource, PropertiesResolver propertiesResolver) {
		this(resource, new Properties(), propertiesResolver);
	}

	public ResourceToObservableProperties(Resource resource, Properties properties,
			PropertiesResolver propertiesResolver) {
		super(resource);
		this.properties = properties;
		this.propertiesResolver = propertiesResolver;
	}

	public Charset getCharset() {
		return charset;
	}

	@Override
	public Properties orElse(Properties other) {
		return properties;
	}

	public void reload() {
		synchronized (this) {
			this.properties.clear();
			this.propertiesResolver.resolveProperties(this.properties, getResource(), charset);
			publishEvent(new ChangeEvent(this, ChangeType.UPDATE));
		}
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
		reload();
	}

	public Registration watch(ResourceWatcher resourceWatcher) {
		Registration registration = resourceWatcher.register(getResource());
		registration = registration.and(resourceWatcher.registerBatchListener((events) -> {
			if (events.anyMatch((event) -> event.getPayload().getResource() == getResource())) {
				reload();
			}
		}));
		return registration;
	}
}
