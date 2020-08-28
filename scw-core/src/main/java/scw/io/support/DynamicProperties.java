package scw.io.support;

import java.util.LinkedHashSet;
import java.util.Properties;

import scw.event.BasicEventDispatcher;
import scw.event.EventRegistration;
import scw.event.ObjectEvent;
import scw.event.support.DefaultBasicEventDispatcher;
import scw.io.ResourceUtils;
import scw.io.event.ObservableResource;
import scw.io.event.ObservableResourceEvent;
import scw.io.event.ObservableResourceEventListener;

public class DynamicProperties {
	private LinkedHashSet<String> resources = new LinkedHashSet<String>();
	private volatile Properties properties;
	private String charsetName;
	private EventRegistration eventRegistration;
	private final BasicEventDispatcher<ObjectEvent<Properties>> eventDispatcher = new DefaultBasicEventDispatcher<ObjectEvent<Properties>>(
			true);
	private final boolean registerByExist;

	/**
	 * @param registerByExist 是否仅当存在时才注册
	 */
	public DynamicProperties(boolean registerByExist) {
		this(null, registerByExist);
	}

	/**
	 * @param charsetName
	 * @param registerByExist 是否仅当存在时才注册
	 */
	public DynamicProperties(String charsetName, boolean registerByExist) {
		this.charsetName = charsetName;
		this.registerByExist = registerByExist;
	}

	public synchronized boolean load(String resource) {
		if (!resources.add(resource)) {
			return false;
		}

		if (eventRegistration != null) {
			eventRegistration.unregister();
		}

		ObservableResource<Properties> observableResource = ResourceUtils.getResourceOperations()
				.getProperties(resources, charsetName);
		this.properties = observableResource.getResource();
		eventDispatcher.publishEvent(new ObjectEvent<Properties>(properties));
		eventRegistration = observableResource.registerListener(new EventListener(), registerByExist);
		return true;
	}

	private class EventListener implements ObservableResourceEventListener<Properties> {

		public void onEvent(ObservableResourceEvent<Properties> event) {
			properties = event.getSource();
			eventDispatcher.publishEvent(new ObjectEvent<Properties>(properties));
		}
	}

	public Properties getProperties() {
		return properties;
	}

	public BasicEventDispatcher<ObjectEvent<Properties>> getEventDispatcher() {
		return eventDispatcher;
	}
}
