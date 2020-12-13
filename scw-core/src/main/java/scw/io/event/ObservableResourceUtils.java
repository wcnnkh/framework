package scw.io.event;

import java.nio.charset.Charset;
import java.util.List;

import scw.core.Converter;
import scw.event.Observable;
import scw.io.Resource;
import scw.io.ResourceUtils;
import scw.io.UnsafeByteArrayInputStream;

public final class ObservableResourceUtils {
	private ObservableResourceUtils() {
	}

	public static Observable<byte[]> getBytes(final Resource resource) {
		return getObservableResource(resource,
				new Converter<Resource, byte[]>() {

					public byte[] convert(Resource k) {
						return ResourceUtils.getBytes(k);
					}
				});
	}

	public static Observable<UnsafeByteArrayInputStream> getInputStream(
			final Resource resource) {
		return getObservableResource(resource,
				new Converter<Resource, UnsafeByteArrayInputStream>() {

					public UnsafeByteArrayInputStream convert(Resource k) {
						byte[] data = ResourceUtils.getBytes(k);
						return data == null ? null
								: new UnsafeByteArrayInputStream(data);
					}
				});
	}

	public static Observable<List<String>> getLines(final Resource resource,
			final String charsetName) {
		return getObservableResource(resource,
				new Converter<Resource, List<String>>() {

					public List<String> convert(Resource k) {
						return ResourceUtils.getLines(k, charsetName);
					}
				});
	}

	public static Observable<List<String>> getLines(Resource resource,
			Charset charset) {
		return getLines(resource, charset.name());
	}

	public static Observable<String> getContent(final Resource resource,
			final Charset charset) {
		return getContent(resource, charset.name());
	}

	public static Observable<String> getContent(Resource resource,
			final String charsetName) {
		return getObservableResource(resource,
				new Converter<Resource, String>() {
					public String convert(Resource k) {
						return ResourceUtils.getContent(k, charsetName);
					}
				});
	}

	public static Observable<Resource> getResource(Resource resource) {
		return getObservableResource(resource,
				new Converter<Resource, Resource>() {
					public Resource convert(Resource k) {
						return k;
					}
				});
	}

	public static <R> Observable<R> getObservableResource(
			final Resource resource, final Converter<Resource, R> converter) {
		return new ObservableResource<R>(resource, converter);
	}
}
