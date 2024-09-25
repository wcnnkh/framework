package io.basc.framework.util.logging;

import io.basc.framework.util.spi.ObservableKeys;

public interface ObservableLevelFactory extends LevelFactory, ObservableKeys<String> {
	/**
	 * 名称和配置是否匹配
	 * 
	 * @param name
	 * @param config
	 * @return
	 */
	boolean match(String name, String config);
}
