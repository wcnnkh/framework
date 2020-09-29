package scw.io.event;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import scw.core.Converter;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.MultiEventRegistration;
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
					if (isExist && !res.exists()) {
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
		return getObservableResource(resource, new Converter<Resource, byte[]>() {

			public byte[] convert(Resource k) {
				return ResourceUtils.getBytes(k);
			}
		});
	}

	public static ObservableResource<UnsafeByteArrayInputStream> getInputStream(final Resource resource) {
		return getObservableResource(resource, new Converter<Resource, UnsafeByteArrayInputStream>() {

			public UnsafeByteArrayInputStream convert(Resource k) {
				byte[] data = ResourceUtils.getBytes(k);
				return data == null ? null : new UnsafeByteArrayInputStream(data);
			}
		});
	}

	public static ObservableResource<List<String>> getLines(final Resource resource, final String charsetName) {
		return getObservableResource(resource, new Converter<Resource, List<String>>() {

			public List<String> convert(Resource k) {
				return ResourceUtils.getLines(k, charsetName);
			}
		});
	}

	public static ObservableResource<List<String>> getLines(Resource resource, Charset charset) {
		return getLines(resource, charset.name());
	}

	public static ObservableResource<String> getContent(final Resource resource, final Charset charset) {
		return getContent(resource, charset.name());
	}

	public static ObservableResource<String> getContent(Resource resource, final String charsetName) {
		return getObservableResource(resource, new Converter<Resource, String>() {
			public String convert(Resource k) {
				return ResourceUtils.getContent(k, charsetName);
			}
		});
	}

	public static ObservableResource<Resource> getResource(Resource resource) {
		return getObservableResource(resource, new Converter<Resource, Resource>() {
			public Resource convert(Resource k) {
				return k;
			}
		});
	}

	public static <R> ObservableResource<R> getObservableResource(final Resource resource,
			final Converter<Resource, R> converter) {
		R r = converter.convert(resource);
		return new ObservableResource<R>(r) {

			@Override
			public EventRegistration registerListener(final ObservableResourceEventListener<R> eventListener,
					boolean isExist) {
				if (isExist && !resource.exists()) {
					return EventRegistration.EMPTY;
				}

				return resource.getEventDispatcher().registerListener(new EventListener<ResourceEvent>() {
					public void onEvent(ResourceEvent event) {
						R r = converter.convert(event.getResource());
						eventListener.onEvent(new ObservableResourceEvent<R>(event, r));
					}
				});
			}
		};
	}
}
