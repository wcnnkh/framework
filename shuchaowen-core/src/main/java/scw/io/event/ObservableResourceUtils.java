package scw.io.event;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.method.MultiEventRegistration;
import scw.io.Resource;
import scw.io.ResourceUtils;
import scw.io.UnsafeByteArrayInputStream;

public final class ObservableResourceUtils {
	private ObservableResourceUtils() {
	}

	public static ObservableResource<Properties> getProperties(final Collection<Resource> resources,
			final String charsetName) {
		Properties properties = new Properties();
		for (Resource resource : resources) {
			ResourceUtils.loadProperties(properties, resource, charsetName);
		}

		return new ObservableResource<Properties>(properties) {
			
			@Override
			public EventRegistration registerListener(final ObservableResourceEventListener<Properties> eventListener,
					boolean isExist) {
				List<EventRegistration> eventRegistrations = new ArrayList<EventRegistration>(resources.size());
				for (Resource res : resources) {
					if(isExist && !res.exists()){
						continue;
					}
					
					EventRegistration eventRegistration = res.getEventDispatcher()
							.registerListener(new EventListener<ResourceEvent>() {

								public void onEvent(ResourceEvent event) {
									ObservableResource<Properties> observableResource = getProperties(resources,
											charsetName);
									eventListener.onEvent(new ObservableResourceEvent<Properties>(event,
											observableResource.getResource()));
								}
							});
					eventRegistrations.add(eventRegistration);
				}
				return new MultiEventRegistration(eventRegistrations);
			}
		};
	}

	public static ObservableResource<byte[]> getBytes(final Resource resource) {
		byte[] data = ResourceUtils.getBytes(resource);
		return new ObservableResource<byte[]>(data) {

			@Override
			public EventRegistration registerListener(final ObservableResourceEventListener<byte[]> eventListener, boolean isExist) {
				if(isExist && !resource.exists()){
					return EventRegistration.EMPTY;
				}
				
				return resource.getEventDispatcher().registerListener(new EventListener<ResourceEvent>() {

					public void onEvent(ResourceEvent event) {
						eventListener.onEvent(new ObservableResourceEvent<byte[]>(event,
								getBytes(event.getResource()).getResource()));
					}
				});
			}
		};
	}

	public static ObservableResource<UnsafeByteArrayInputStream> getInputStream(final Resource resource) {
		final ObservableResource<byte[]> res = getBytes(resource);
		return new ObservableResource<UnsafeByteArrayInputStream>(
				res.getResource() == null ? null : new UnsafeByteArrayInputStream(res.getResource())) {

			@Override
			public EventRegistration registerListener(
					final ObservableResourceEventListener<UnsafeByteArrayInputStream> eventListener, boolean isExist) {
				return res.registerListener(new ObservableResourceEventListener<byte[]>() {

					public void onEvent(ObservableResourceEvent<byte[]> event) {
						eventListener.onEvent(new ObservableResourceEvent<UnsafeByteArrayInputStream>(event,
								res.getResource() == null ? null : new UnsafeByteArrayInputStream(res.getResource())));
					}
				}, isExist);
			}
		};
	}

	public static ObservableResource<List<String>> getLines(final Resource resource, final String charsetName) {
		List<String> lines = ResourceUtils.getLines(resource, charsetName);
		return new ObservableResource<List<String>>(lines) {

			@Override
			public EventRegistration registerListener(
					final ObservableResourceEventListener<List<String>> eventListener, boolean isExist) {
				if(isExist && !resource.exists()){
					return EventRegistration.EMPTY;
				}
				
				return resource.getEventDispatcher().registerListener(new EventListener<ResourceEvent>() {
					public void onEvent(ResourceEvent event) {
						eventListener.onEvent(new ObservableResourceEvent<List<String>>(event,
								getLines(event.getResource(), charsetName).getResource()));
					}
				});
			}
		};
	}

	public static ObservableResource<List<String>> getLines(Resource resource, Charset charset) {
		return getLines(resource, charset.name());
	}

	public static ObservableResource<String> getContent(final Resource resource, final Charset charset) {
		return getContent(resource, charset.name());
	}

	public static ObservableResource<String> getContent(final Resource resource, final String charsetName) {
		String content = ResourceUtils.getContent(resource, charsetName);
		return new ObservableResource<String>(content) {

			@Override
			public EventRegistration registerListener(final ObservableResourceEventListener<String> eventListener, boolean isExist) {
				if(isExist && !resource.exists()){
					return EventRegistration.EMPTY;
				}
				
				return resource.getEventDispatcher().registerListener(new EventListener<ResourceEvent>() {
					public void onEvent(ResourceEvent event) {
						eventListener.onEvent(new ObservableResourceEvent<String>(event,
								getContent(event.getResource(), charsetName).getResource()));
					}
				});
			}
		};
	}
}
