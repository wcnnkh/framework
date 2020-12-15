package scw.event;

import scw.util.KeyValuePair;

public class KeyValuePairEvent<K, V> extends ChangeEvent<KeyValuePair<K, V>> {
	private static final long serialVersionUID = 1L;

	public KeyValuePairEvent(ChangeEvent<KeyValuePair<K, V>> event) {
		super(event);
	}

	public KeyValuePairEvent(EventType eventType, K key, V value) {
		super(eventType, new KeyValuePair<K, V>(key, value));
	}

	public KeyValuePairEvent(EventType eventType,
			KeyValuePair<K, V> keyValuePair) {
		super(eventType, keyValuePair);
	}
}
