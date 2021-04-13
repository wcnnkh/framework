package scw.logger;

import java.util.logging.Level;

import scw.util.KeyValuePair;

public final class LevelConfig extends KeyValuePair<String, Level> {
	private static final long serialVersionUID = 1L;

	public LevelConfig(String key, Level value) {
		super(key, value);
	}
}
