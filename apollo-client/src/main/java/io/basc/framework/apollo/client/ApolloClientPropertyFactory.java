package io.basc.framework.apollo.client;

import java.util.Collection;
import java.util.Iterator;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventTypes;
import io.basc.framework.event.support.StandardBroadcastEventDispatcher;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.Value;

@Provider
public class ApolloClientPropertyFactory extends StandardBroadcastEventDispatcher<ChangeEvent<Collection<String>>>
		implements PropertyFactory, ConfigChangeListener {
	private final Config config;

	public ApolloClientPropertyFactory(Config config) {
		this.config = config;
		config.addChangeListener(this);
	}

	@Override
	public Iterator<String> iterator() {
		return config.getPropertyNames().iterator();
	}

	@Override
	public Value get(String key) {
		String value = config.getProperty(key, null);
		return Value.of(value);
	}

	@Override
	public void onChange(ConfigChangeEvent changeEvent) {
		publishEvent(new ChangeEvent<Collection<String>>(EventTypes.UPDATE, changeEvent.changedKeys()));
	}
}
