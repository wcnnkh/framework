package io.basc.framework.io.event;

import java.nio.charset.Charset;
import java.util.List;

import io.basc.framework.event.Observable;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.io.UnsafeByteArrayInputStream;
import io.basc.framework.util.stream.Processor;

public final class ObservableResourceUtils {
	private ObservableResourceUtils() {
	}

	public static Observable<byte[]> getBytes(final Resource resource) {
		return getObservableResource(resource, (k) -> ResourceUtils.getBytes(k));
	}

	public static Observable<UnsafeByteArrayInputStream> getInputStream(final Resource resource) {
		return getObservableResource(resource, (k) -> {
			byte[] data = ResourceUtils.getBytes(k);
			return data == null ? null : new UnsafeByteArrayInputStream(data);
		});
	}

	public static Observable<List<String>> getLines(final Resource resource, final String charsetName) {
		return getObservableResource(resource, (k) -> ResourceUtils.getLines(k, charsetName));
	}

	public static Observable<List<String>> getLines(Resource resource, Charset charset) {
		return getLines(resource, charset.name());
	}

	public static Observable<String> getContent(final Resource resource, final Charset charset) {
		return getContent(resource, charset.name());
	}

	public static Observable<String> getContent(Resource resource, final String charsetName) {
		return getObservableResource(resource, (k) -> ResourceUtils.getContent(k, charsetName));
	}

	public static Observable<Resource> getResource(Resource resource) {
		return getObservableResource(resource, (e) -> e);
	}

	public static <R> Observable<R> getObservableResource(final Resource resource,
			final Processor<Resource, R, ? extends RuntimeException> processor) {
		return new ObservableResource<R>(resource, processor);
	}
}
