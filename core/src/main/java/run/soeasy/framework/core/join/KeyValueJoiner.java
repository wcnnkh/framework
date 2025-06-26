package run.soeasy.framework.core.join;

import java.io.IOException;
import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 将keyValue连接起来
 * 
 * @author soeasy.run
 *
 * @param <K>
 * @param <V>
 */
@RequiredArgsConstructor
@Getter
@Setter
public class KeyValueJoiner<K, V> implements Joiner<KeyValue<? extends K, ? extends V>> {
	/**
	 * 多个keyValue之间的分割符
	 */
	private final CharSequence delimiter;
	/**
	 * key和value的连接符
	 */
	private final CharSequence connector;
	@NonNull
	private final Function<? super K, ? extends CharSequence> keyEncoder;
	@NonNull
	private final Function<? super V, ? extends CharSequence> valueEncoder;

	@Override
	public long join(Appendable appendable, long count, KeyValue<? extends K, ? extends V> element) throws IOException {
		CharSequence key = keyEncoder.apply(element.getKey());
		CharSequence value = valueEncoder.apply(element.getValue());
		if (key == null || value == null) {
			return 0;
		}

		if (count != 0) {
			appendable.append(delimiter);
		}

		appendable.append(key);
		appendable.append(connector);
		appendable.append(value);
		return 1;
	}
}
