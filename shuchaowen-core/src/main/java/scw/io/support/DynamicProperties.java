package scw.io.support;

import java.util.LinkedHashSet;
import java.util.Properties;

import scw.event.BasicEventDispatcher;
import scw.event.ObjectEvent;
import scw.event.EventRegistration;
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

	public DynamicProperties() {
		this(null);
	}

	public DynamicProperties(String charsetName) {
		this.charsetName = charsetName;
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
		eventRegistration = observableResource.registerListener(new ObservableResourceEventListener<Properties>() {

			public void onEvent(ObservableResourceEvent<Properties> event) {
				properties = event.getSource();
				eventDispatcher.publishEvent(new ObjectEvent<Properties>(properties));
			}
		});
		return true;
	}

	public Properties getProperties() {
		return properties;
	}

	public BasicEventDispatcher<ObjectEvent<Properties>> getEventDispatcher() {
		return eventDispatcher;
	}
}
