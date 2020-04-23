package scw.io.resource;

import java.io.InputStream;

import scw.core.Converter;
import scw.util.queue.Consumer;

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
