package scw.core.resource;

import java.io.InputStream;

import scw.core.Consumer;
import scw.core.Converter;

public class InputStreamConvertConsumer<T> implements Consumer<InputStream> {
	private Converter<InputStream, T> converter;
	private T value;

	public InputStreamConvertConsumer(Converter<InputStream, T> converter) {
		this.converter = converter;
	}

	public void consume(InputStream message) throws Exception {
		this.value = converter.convert(message);
	}

	public T getValue() {
		return value;
	}
}
