package scw.event;

import scw.util.Pair;

public class KeyValuePairEvent<K, V> extends ChangeEvent<Pair<K, V>> {
	private static final long serialVersionUID = 1L;

	public KeyValuePairEvent(ChangeEvent<Pair<K, V>> event) {
		super(event);
	}

	public KeyValuePairEvent(EventType eventType, K key, V value) {
		super(eventType, new Pair<K, V>(key, value));
	}

	public KeyValuePairEvent(EventType eventType,
			Pair<K, V> keyValuePair) {
		super(eventType, keyValuePair);
	}
}
