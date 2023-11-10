package io.basc.framework.text;

import java.io.IOException;
import java.util.Iterator;
import java.util.stream.Stream;

import io.basc.framework.codec.Codec;
import io.basc.framework.io.IOUtils;
import io.basc.framework.util.Pair;
import io.basc.framework.util.StringUtils;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.Value;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class QueryStringFormat extends ObjectFormat {
	private final String connector;
	private final String keyValueConnector;
	private Codec<String, String> codec;
	private long formatIndex;

	public QueryStringFormat() {
		this("&", "=");
	}

	public QueryStringFormat(String connector, String keyValueConnector) {
		this.connector = connector;
		this.keyValueConnector = keyValueConnector;
	}

	@Override
	public void format(Stream<Pair<String, Value>> source, Appendable target) throws IOException {
		Iterator<Pair<String, Value>> iterator = source.iterator();
		while (iterator.hasNext()) {
			Pair<String, Value> pair = iterator.next();
			String key = pair.getKey();
			String value = pair.getValue().getAsString();
			if (codec != null) {
				key = codec.encode(key);
				value = codec.encode(value);
			}

			target.append(key);
			target.append(keyValueConnector);
			target.append(value);
			if (iterator.hasNext()) {
				target.append(connector);
			}
		}
	}

	@Override
	public Stream<Pair<String, Value>> parse(Readable source) throws IOException {
		return IOUtils.split(source, connector).map((e) -> {
			String[] kv = StringUtils.splitToArray(e, keyValueConnector);
			if (kv.length == 0) {
				return null;
			}
			// 忽略多余的分割，只使用两个
			String key = kv[0];
			String value = kv.length == 1 ? null : kv[1];
			if (codec != null) {
				key = codec.decode(key);
				value = codec.decode(value);
			}

			return new Pair<>(key, new AnyValue(value, getConversionService()));
		});
	}
}
