package io.basc.framework.convert.strings;

import java.io.IOException;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiPredicate;

import io.basc.framework.codec.Codec;
import lombok.Data;

@Data
public class DefaultQueryStringHandler implements QueryStringHandler {
	private String connector;
	private String keyValueConnector;
	private Codec<String, String> codec;

	public DefaultQueryStringHandler() {
		this("&", "?");
	}

	public DefaultQueryStringHandler(String connector, String keyValueConnector) {
		this.connector = connector;
		this.keyValueConnector = keyValueConnector;
	}

	@Override
	public void write(LongAdder writeCount, String key, String value, Appendable target)
			throws IOException {
		if (writeCount.longValue() == 0) {
			target.append(connector);
			writeCount.increment();
		}

		if (codec != null) {
			key = codec.encode(key);
			value = codec.encode(value);
		}

		target.append(key);
		target.append(keyValueConnector);
		target.append(value);
	}

	@Override
	public void read(LongAdder readCount, Readable source, BiPredicate<String, String> predicate) throws IOException {
		// TODO Auto-generated method stub
		
	}
}
