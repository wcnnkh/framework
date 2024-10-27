package io.basc.framework.apollo.client;

import java.util.Collection;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;

import io.basc.framework.convert.lang.ObjectValue;
import io.basc.framework.env.PropertyFactory;
import io.basc.framework.util.Elements;
import io.basc.framework.util.SetElements;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.actor.ChangeType;
import io.basc.framework.util.actor.broadcast.support.StandardBroadcastEventDispatcher;

public class ApolloClientPropertyFactory extends StandardBroadcastEventDispatcher<ChangeEvent<Collection<String>>>
		implements PropertyFactory, ConfigChangeListener {
	private final Config config;

	public ApolloClientPropertyFactory(Config config) {
		this.config = config;
		config.addChangeListener(this);
	}

	@Override
	public Elements<String> keys() {
		return new SetElements<>(config.getPropertyNames());
	}

	@Override
	public ObjectValue get(String key) {
		String value = config.getProperty(key, null);
		return ObjectValue.of(value);
	}

	@Override
	public void onChange(ConfigChangeEvent changeEvent) {
		publishEvent(new ChangeEvent<Collection<String>>(ChangeType.UPDATE, changeEvent.changedKeys()));
	}
}
