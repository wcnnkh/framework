package scw.beans.property;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.w3c.dom.NodeList;

import scw.core.PropertyFactory;
import scw.core.utils.SystemPropertyUtils;

public final class AutoRefreshPropertyFactory implements PropertyFactory {
	private Map<String, String> propertyMap;
	private Timer timer;
	private PropertyFactory propertyFactory;

	public AutoRefreshPropertyFactory(NodeList nodeList) {
		if (nodeList == null || nodeList.getLength() == 0) {
			return;
		}

		propertyMap = new HashMap<String, String>();
		timer = new Timer(AutoRefreshPropertyFactory.class.getName());
		SimplePropertyFactory tempPropertyFactory = new SimplePropertyFactory(
				nodeList);
		for (Properties properties : tempPropertyFactory.getPropertiesList()) {
			refreshProperties(properties, tempPropertyFactory);
		}

		this.propertyFactory = tempPropertyFactory;
		for (Properties properties : tempPropertyFactory.getPropertiesList()) {
			if (properties.getRefreshPeriod() > 0) {
				timer.scheduleAtFixedRate(
						new RefreshPropertiesTask(properties),
						properties.getRefreshPeriod(),
						properties.getRefreshPeriod());
			}
		}
	}

	private void refreshProperties(Properties properties,
			PropertyFactory propertyFactory) {
		synchronized (propertyMap) {
			Map<String, PropertyValue> map = properties.getPropertyMap();
			for (Entry<String, PropertyValue> entry : map.entrySet()) {
				String value = propertyFactory.getProperty(entry.getKey());
				if (entry.getValue().isSystem()) {
					System.setProperty(entry.getKey(), value);
					propertyMap.remove(entry.getKey());
				} else {
					propertyMap.put(entry.getKey(), value);
					System.clearProperty(entry.getKey());
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
				refreshProperties(properties, propertyFactory);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
