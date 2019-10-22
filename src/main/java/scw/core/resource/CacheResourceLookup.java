package scw.core.resource;

import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import scw.core.Consumer;
import scw.core.Converter;
import scw.core.Verification;
import scw.io.IOUtils;
import scw.io.UnsafeByteArrayInputStream;

public final class CacheResourceLookup implements ResourceLookup {
	private final ConcurrentHashMap<String, ResourceData> resourceMap = new ConcurrentHashMap<String, ResourceData>();
	private final ResourceLookup resourceLookup;

	public CacheResourceLookup(ResourceLookup resourceLookup) {
		this.resourceLookup = resourceLookup;
	}

	public boolean lookup(String resource) {
		return lookup(resource, null);
	}

	public boolean lookup(String resource, Consumer<InputStream> consumer) {
		ResourceData data = resourceMap.get(resource);
		if (data == null) {
			byte[] buff = resourceLookup.getResource(resource, new Converter<InputStream, byte[]>() {

				public byte[] convert(InputStream k) throws Exception {
					return IOUtils.read(k, 1024, 0).toByteArray();
				}
			});
			data = new ResourceData(buff);
			ResourceData old = resourceMap.putIfAbsent(resource, data);
			if (old != null) {
				data = old;
			}
		}

		if (data.isEmpty()) {
			return false;
		}

		if (consumer != null) {
			try {
				consumer.consume(new UnsafeByteArrayInputStream(data.getData()));
			} catch (Exception e) {
				throw new RuntimeException(resource, e);
			}
		}
		return true;

	}

	public <T> T getResource(String resource, Converter<InputStream, T> converter) {
		InputStreamConvertConsumer<T> inputStreamConvertConsumer = new InputStreamConvertConsumer<T>(converter);
		lookup(resource, inputStreamConvertConsumer);
		return inputStreamConvertConsumer.getValue();
	}

	public Collection<Class<?>> getClasses() {
		return resourceLookup.getClasses();
	}

	public Collection<Class<?>> getClasses(String resource) {
		return resourceLookup.getClasses(resource);
	}

	public Collection<Class<?>> getClasses(String resource, Verification<String> classNameVerification) {
		return resourceLookup.getClasses(resource, classNameVerification);
	}

	public void clear() {
		resourceMap.clear();
	}

	private final class ResourceData {
		private final byte[] data;

		public ResourceData(byte[] data) {
			this.data = data;
		}

		public byte[] getData() {
			return data;
		}

		public boolean isEmpty() {
			return data == null;
		}
	}
}
