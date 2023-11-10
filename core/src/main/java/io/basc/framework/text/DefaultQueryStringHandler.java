package io.basc.framework.text;

import java.io.IOException;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;

import io.basc.framework.codec.Codec;
import io.basc.framework.io.IOUtils;
import io.basc.framework.util.StringUtils;
import lombok.Data;

@Data
public class DefaultQueryStringHandler implements QueryStringHandler {
	private String connector;
	private String keyValueConnector;
	private Codec<String, String> codec;

	public DefaultQueryStringHandler() {
		this("&", "=");
	}

	public DefaultQueryStringHandler(String connector, String keyValueConnector) {
		this.connector = connector;
		this.keyValueConnector = keyValueConnector;
	}

	@Override
	public void read(LongAdder readSize, Readable source, BiConsumer<String, String> consumer) throws IOException {
		IOUtils.split(source, connector).forEach((e) -> {
			readSize.increment();
			String[] kv = StringUtils.splitToArray(e, keyValueConnector);
			if (kv.length == 0) {
				return;
			}
			// 忽略多余的分割，只使用两个
			consumer.accept(kv[0], kv.length == 1 ? null : kv[1]);
		});
	}

	@Override
	public void write(LongAdder writtenSize, String key, String value, Appendable target) throws IOException {
		if (writtenSize.longValue() == 0) {
			target.append(connector);
		}

		if (codec != null) {
			key = codec.encode(key);
			value = codec.encode(value);
		}

		target.append(key);
		target.append(keyValueConnector);
		target.append(value);
		writtenSize.increment();
	}
}
