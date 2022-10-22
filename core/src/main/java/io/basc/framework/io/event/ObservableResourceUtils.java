package io.basc.framework.io.event;

import java.nio.charset.Charset;
import java.util.List;

import io.basc.framework.event.Observable;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.io.UnsafeByteArrayInputStream;

public final class ObservableResourceUtils {
	private ObservableResourceUtils() {
	}

	public static Observable<byte[]> getBytes(final Resource resource) {
		return resource.map((k) -> ResourceUtils.getBytes(k));
	}

	public static Observable<UnsafeByteArrayInputStream> getInputStream(final Resource resource) {
		return resource.map((k) -> {
			byte[] data = ResourceUtils.getBytes(k);
			return data == null ? null : new UnsafeByteArrayInputStream(data);
		});
	}

	public static Observable<List<String>> getLines(final Resource resource, final String charsetName) {
		return resource.map((k) -> ResourceUtils.getLines(k, charsetName));
	}

	public static Observable<List<String>> getLines(Resource resource, Charset charset) {
		return getLines(resource, charset.name());
	}

	public static Observable<String> getContent(final Resource resource, final Charset charset) {
		return getContent(resource, charset.name());
	}

	public static Observable<String> getContent(Resource resource, final String charsetName) {
		return resource.map((e) -> ResourceUtils.getContent(e, charsetName));
	}
}
