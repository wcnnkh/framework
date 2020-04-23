package scw.io.resource;

import java.io.InputStream;

import scw.core.Converter;
import scw.io.IOUtils;
import scw.io.UnsafeByteArrayInputStream;
import scw.util.ConcurrentReferenceHashMap;
import scw.util.queue.Consumer;

final class CacheResourceLookup implements ResourceLookup {
	private final ConcurrentReferenceHashMap<String, ResourceData> resourceMap = new ConcurrentReferenceHashMap<String, ResourceData>();
	private final ResourceLookup resourceLookup;

	public CacheResourceLookup(ResourceLookup resourceLookup) {
		this.resourceLookup = resourceLookup;
	}

	public boolean lookup(String resource, Consumer<InputStream> consumer) {
		ResourceData data = resourceMap.get(resource);
		if (data == null) {
			byte[] buff = ResourceUtils.getResource(resource, new Converter<InputStream, byte[]>() {

				public byte[] convert(InputStream k) throws Exception {
					return IOUtils.read(k, 1024, 0).toByteArray();
				}
			}, resourceLookup);
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
			} catch (Throwable e) {
				throw new RuntimeException(resource, e);
			}
		}
		return true;
	}

	private static class ResourceData {
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
