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
			long t = properties.getRefreshPeriod() > 0 ? properties.getRefreshPeriod() : defaultRefreshPeriod;
			if (t > 0) {
				timer.scheduleAtFixedRate(new RefreshPropertiesTask(properties), t, t);
			}
		}
	}

	private void refreshProperty(Property property) {
		synchronized (propertyMap) {
			String value = property.getValue();
			if (property.isSystem()) {
				System.setProperty(property.getName(), value);
				propertyMap.remove(property.getName());
			} else {
				propertyMap.put(property.getName(), value);
				System.clearProperty(property.getName());
			}
		}
	}

	private void refreshProperties(Properties properties, PropertyFactory propertyFactory, boolean first) {
		Map<String, Property> map = properties.getPropertyMap();
		for (Entry<String, Property> entry : map.entrySet()) {
			if (first) {
				refreshProperty(entry.getValue());
			} else {
				if (entry.getValue().isRefresh()) {
					refreshProperty(entry.getValue());
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
