package scw.beans.property;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.w3c.dom.NodeList;

import scw.core.PropertyFactory;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.logger.LoggerUtils;

public final class AutoRefreshPropertyFactory implements PropertyFactory {
	private volatile Map<String, String> propertyMap;
	private PropertyFactory propertyFactory;

	public AutoRefreshPropertyFactory(NodeList nodeList, Timer timer, long defaultRefreshPeriod) {
		if (nodeList == null || nodeList.getLength() == 0) {
			return;
		}

		propertyMap = new HashMap<String, String>();
		timer = new Timer(AutoRefreshPropertyFactory.class.getName());
		SimplePropertyFactory tempPropertyFactory = new SimplePropertyFactory(nodeList);
		for (Properties properties : tempPropertyFactory.getPropertiesList()) {
			refreshProperties(properties, tempPropertyFactory, true);
		}

		this.propertyFactory = tempPropertyFactory;
		for (Properties properties : tempPropertyFactory.getPropertiesList()) {
			if (properties.getRefreshPeriod() < 0) {
				continue;
			}

			long t = properties.getRefreshPeriod() == 0 ? defaultRefreshPeriod : properties.getRefreshPeriod();
			if (t > 0) {
				t = t * 1000;
				timer.scheduleAtFixedRate(new RefreshPropertiesTask(properties), t, t);
			}
		}
	}

	private void refreshValue(Property property, String value) {
		synchronized (propertyMap) {
			if (property.isSystem()) {
				SystemPropertyUtils.setSystemProperty(property.getName(), value);
				propertyMap.remove(property.getName());
			} else {
				propertyMap.put(property.getName(), value);
				SystemPropertyUtils.clearSystemProperty(property.getName());
			}
		}
	}

	private void refreshProperty(Property property, boolean first) {
		String value = property.getValue();
		if (first) {
			refreshValue(property, value);
		} else {
			String oldValue = getProperty(property.getName());
			if (!StringUtils.isAeqB(value, oldValue)) {
				LoggerUtils.info(AutoRefreshPropertyFactory.class,
						"Property {} changes the \noriginal value\n{}\n----------to----------\n{}", property.getName(),
						oldValue, value);
				refreshValue(property, value);
			}
		}
	}

	private void refreshProperties(Properties properties, PropertyFactory propertyFactory, boolean first) {
		Map<String, Property> map = properties.getPropertyMap();
		for (Entry<String, Property> entry : map.entrySet()) {
			if (first) {
				refreshProperty(entry.getValue(), first);
			} else {
				if (entry.getValue().isRefresh()) {
					refreshProperty(entry.getValue(), first);
				}
			}
		}
	}

	public String getProperty(String key) {
		if (propertyMap == null || propertyMap.isEmpty()) {
			return SystemPropertyUtils.getProperty(key);
		}

		String value = propertyMap.get(key);
		return value == null ? SystemPropertyUtils.getProperty(key) : value;
	}

	private final class RefreshPropertiesTask extends TimerTask {
		private Properties properties;

		public RefreshPropertiesTask(Properties properties) {
			this.properties = properties;
		}

		@Override
		public void run() {
			try {
				refreshProperties(properties, propertyFactory, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
