package run.soeasy.framework.messaging.convert.support;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.stream.Stream;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import run.soeasy.framework.beans.format.ObjectFormat;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.codec.lang.URLCodec;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.value.TypedValue;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.io.IOUtils;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class QueryStringFormat extends ObjectFormat {
	private final String connector;
	private final String keyValueConnector;
	private Codec<String, String> codec;
	private long formatCount;

	public QueryStringFormat() {
		this("&", "=");
	}

	public QueryStringFormat(@NonNull String connector, @NonNull String keyValueConnector) {
		this.connector = connector;
		this.keyValueConnector = keyValueConnector;
	}

	public void setCharset(Charset charset) {
		this.codec = charset == null ? null : new URLCodec(charset);
	}

	public void reset() {
		formatCount = 0;
	}

	@Override
	public void format(Stream<KeyValue<String, TypedValue>> source, Appendable target) throws IOException {
		Iterator<KeyValue<String, TypedValue>> iterator = source.iterator();
		while (iterator.hasNext()) {
			KeyValue<String, TypedValue> pair = iterator.next();
			String key = pair.getKey();
			String value = pair.getValue().getAsString();
			if (codec != null) {
				key = codec.encode(key);
				value = codec.encode(value);
			}

			if (formatCount > 0) {
				target.append(connector);
			}
			formatCount++;
			target.append(key);
			target.append(keyValueConnector);
			target.append(value);
			if (iterator.hasNext()) {
				target.append(connector);
			}
		}
	}

	@Override
	public Stream<KeyValue<String, TypedValue>> parse(Readable source) throws IOException {
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

			return KeyValue.of(key, TypedValue.of(value));
		});
	}
}
