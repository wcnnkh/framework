package io.basc.framework.factory.support;

import io.basc.framework.factory.NoArgsInstanceFactory;
import io.basc.framework.factory.ServiceLoader;
import io.basc.framework.util.Assert;
import io.basc.framework.value.Value;
import io.basc.framework.value.ValueFactory;

import java.util.Arrays;
import java.util.Iterator;

public class ConfigServiceLoader<S> implements ServiceLoader<S> {
	private final Class<S> serviceClass;
	private final ValueFactory<String> configFactory;
	private final NoArgsInstanceFactory instanceFactory;
	private volatile String[] names;

	public ConfigServiceLoader(Class<S> serviceClass, ValueFactory<String> configFactory,
			NoArgsInstanceFactory instanceFactory) {
		Assert.requiredArgument(serviceClass != null, "serviceClass");
		Assert.requiredArgument(configFactory != null, "configFactory");
		Assert.requiredArgument(instanceFactory != null, "instanceFactory");
		this.serviceClass = serviceClass;
		this.configFactory = configFactory;
		this.instanceFactory = instanceFactory;
	}

	public String[] getNames() {
		Value value = configFactory.getValue(serviceClass.getName());
		if (value == null) {
			return new String[0];
		}

		String[] names = value.getAsObject(String[].class);
		if (names == null) {
			return new String[0];
		}
		return names;
	}

	public void reload() {
		synchronized (this) {
			this.names = getNames();
		}
	}

	public Iterator<S> iterator() {
		if (names == null) {
			synchronized (this) {
				if (names == null) {
					this.names = getNames();
				}
			}
		}
		return new InstanceIterator<S>(instanceFactory, Arrays.asList(names).iterator());
	}

}
