package scw.core.utils;

import java.io.InputStream;

import scw.core.Consumer;
import scw.core.Converter;

public class ResourceInputStreamConvertConsumer<T> implements Consumer<InputStream> {
	private Converter<InputStream, T> converter;
	private T value;

	public ResourceInputStreamConvertConsumer(Converter<InputStream, T> converter) {
		this.converter = converter;
	}

	public void consume(InputStream message) throws Exception {
		this.value = converter.convert(message);
	}

	public T getValue() {
		return value;
	}
}
