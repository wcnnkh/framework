package scw.event;

import scw.util.Pair;

public class PairChangeEvent<K, V> extends ChangeEvent<Pair<K, V>> {
	private static final long serialVersionUID = 1L;

	public PairChangeEvent(ChangeEvent<Pair<K, V>> event) {
		super(event);
	}

	public PairChangeEvent(EventType eventType, K key, V value) {
		super(eventType, new Pair<K, V>(key, value));
	}

	public PairChangeEvent(EventType eventType,
			Pair<K, V> keyValuePair) {
		super(eventType, keyValuePair);
	}
}
