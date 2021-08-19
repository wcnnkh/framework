package scw.apollo.client;

import java.util.Iterator;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;

import scw.context.annotation.Provider;
import scw.event.ChangeEvent;
import scw.event.EventType;
import scw.event.support.SimpleStringNamedEventDispatcher;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.value.AnyValue;
import scw.value.PropertyFactory;
import scw.value.Value;

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
