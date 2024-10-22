package io.basc.framework.util.logging;

import java.util.logging.Level;

import io.basc.framework.util.spi.ConfigurableServices;

public class ConfigurableLevelFactory extends ConfigurableServices<LevelFactory> implements LevelFactory {

	public ConfigurableLevelFactory() {
		setServiceClass(LevelFactory.class);
	}

	@Override
	public Level getLevel(String name) {
		for (LevelFactory factory : this) {
			Level level = factory.getLevel(name);
			if (level != null) {
				return level;
			}
		}
		return null;
	}

}
