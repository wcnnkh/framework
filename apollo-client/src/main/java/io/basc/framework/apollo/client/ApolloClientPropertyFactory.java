package io.basc.framework.apollo.client;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventType;
import io.basc.framework.event.support.SimpleStringNamedEventDispatcher;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.Value;

import java.util.Iterator;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;

@Provider
public class ApolloClientPropertyFactory extends SimpleStringNamedEventDispatcher<ChangeEvent<String>>
		implements PropertyFactory, ConfigChangeListener {
	private static Logger logger = LoggerFactory.getLogger(ApolloClientPropertyFactory.class);
	private final Config config;

	public ApolloClientPropertyFactory(Config config) {
		super(true);
		this.config = config;
		config.addChangeListener(this);
	}

	@Override
	public Iterator<String> iterator() {
		return config.getPropertyNames().iterator();
	}

	@Override
	public Value getValue(String key) {
		String value = config.getProperty(key, null);
		return value == null ? null : new AnyValue(value);
	}

	@Override
	public void onChange(ConfigChangeEvent changeEvent) {
		for (String key : changeEvent.changedKeys()) {
			try {
				publishEvent(key, new ChangeEvent<String>(EventType.UPDATE, key));
			} catch (Exception e) {
				logger.error(e, changeEvent.toString());
			}
		}
	}
}
