package io.basc.framework.apollo.client;

import java.util.Collection;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;

import io.basc.framework.convert.lang.Value;
import io.basc.framework.env.PropertyFactory;
import io.basc.framework.event.broadcast.support.StandardBroadcastEventDispatcher;
import io.basc.framework.observe.ObservableEvent;
import io.basc.framework.observe.ChangeType;
import io.basc.framework.util.element.ElementSet;
import io.basc.framework.util.element.Elements;

public class ApolloClientPropertyFactory extends StandardBroadcastEventDispatcher<ObservableEvent<Collection<String>>>
		implements PropertyFactory, ConfigChangeListener {
	private final Config config;

	public ApolloClientPropertyFactory(Config config) {
		this.config = config;
		config.addChangeListener(this);
	}

	@Override
	public Elements<String> keys() {
		return new ElementSet<>(config.getPropertyNames());
	}

	@Override
	public Value get(String key) {
		String value = config.getProperty(key, null);
		return Value.of(value);
	}

	@Override
	public void onChange(ConfigChangeEvent changeEvent) {
		publishEvent(new ObservableEvent<Collection<String>>(ChangeType.UPDATE, changeEvent.changedKeys()));
	}
}
