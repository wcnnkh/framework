package shuchaowen.torrent;

import java.util.HashMap;
import java.util.Map;

import shuchaowen.core.exception.AlreadyExistsException;
import shuchaowen.core.exception.KeyNonexistentException;

public enum Key {
	announce("announce"),
	announce_list("announce-list"),
	creation_date("creation date"),
	comment("comment"),
	created_by("created by"),
	info("info"),
	length("length"),
	md5sum("md5sum"),
	name("name"),
	piece_length("piece length"),
	pieces("pieces"),
	files("files"),
	path("path")
	;

	private static Map<String, Key> keyMap = new HashMap<String, Key>();
	static {
		for (Key key : values()) {
			if (keyMap.containsKey(key.getKey())) {
				throw new AlreadyExistsException(key.getKey());
			}

			keyMap.put(key.getKey(), key);
		}
	}

	private final String key;

	Key(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public static Key getKey(String key) {
		Key k = keyMap.get(key);
		if (k == null) {
			throw new KeyNonexistentException(key);
		}
		return k;
	}
}
