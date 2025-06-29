package run.soeasy.framework.core.join;

import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.io.IOUtils;

@Getter
public class KeyValueSplitter extends KeyValueJoiner<Object, Object> implements Splitter<KeyValue<String, String>> {
	private final Function<String, String> keyDecoder;
	private final Function<String, String> valueDecoder;

	public KeyValueSplitter(@NonNull CharSequence delimiter, @NonNull CharSequence connector,
			@NonNull Codec<String, String> keyCodec, @NonNull Codec<String, String> valueCodec) {
		super(delimiter, connector, (key) -> key == null ? null : keyCodec.encode(String.valueOf(key)),
				(value) -> value == null ? null : valueCodec.encode(String.valueOf(value)));
		Assert.isTrue(!delimiter.equals(connector), "The delimiter and connector cannot be the same");
		this.keyDecoder = keyCodec::decode;
		this.valueDecoder = valueCodec::decode;
	}

	@Override
	public Stream<KeyValue<String, String>> split(Readable readable) throws IOException {
		return IOUtils.split(readable, getDelimiter()).map((e) -> {
			String[] kv = StringUtils.splitToArray(e, getConnector());
			if (kv.length == 0) {
				return null;
			}
			// 忽略多余的分割，只使用两个
			String key = kv[0];
			String value = kv.length == 1 ? null : kv[1];

			key = keyDecoder.apply(key);
			value = valueDecoder.apply(value);
			return KeyValue.of(key, value);
		});
	}

}