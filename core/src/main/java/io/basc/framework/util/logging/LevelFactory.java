package io.basc.framework.util.logging;

import java.util.logging.Level;

import io.basc.framework.util.Dictionary;
import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.observe.Listener;
import io.basc.framework.util.observe.Observable;
import io.basc.framework.util.observe.Registration;
import io.basc.framework.util.observe.event.ChangeEvent;

public interface LevelFactory extends Dictionary<String, Level>, Observable<Elements<ChangeEvent<String>>> {
	/**
	 * 获取日志等级
	 * 
	 * @param name
	 * @return
	 */
	Level getLevel(String name);

	@Override
	default Elements<KeyValue<String, Level>> getElements(String key) {
		return getElements().filter((e) -> match(key, e.getKey()));
	}

	/**
	 * 名称和配置是否匹配
	 * 
	 * @param name
	 * @param config
	 * @return
	 */
	boolean match(String name, String config);

	/**
	 * 观察配置变更
	 */
	@Override
	Registration registerListener(Listener<? super Elements<ChangeEvent<String>>> listener);
}
