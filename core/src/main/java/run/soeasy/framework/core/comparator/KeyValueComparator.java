package run.soeasy.framework.core.comparator;

import java.util.Comparator;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.domain.KeyValue;

@RequiredArgsConstructor
@Getter
public class KeyValueComparator<K, V> implements Comparator<KeyValue<K, V>> {
	@NonNull
	private final Comparator<? super K> keyComparator;
	@NonNull
	private final Comparator<? super V> valueComparator;

	@Override
	public int compare(KeyValue<K, V> o1, KeyValue<K, V> o2) {
		int v = keyComparator.compare(o1.getKey(), o2.getKey());
		int ov = valueComparator.compare(o1.getValue(), o2.getValue());
		if (v == 0) {
			return ov;
		} else if (ov == 0) {
			return v;
		} else {
			if (ov >= 0) {
				return v;
			}
			return 0;
		}
	}
}
