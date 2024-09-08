package io.basc.framework.util.logging;

import java.util.logging.Level;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.event.EventPublishService;
import io.basc.framework.util.match.StringMatcher;
import io.basc.framework.util.match.StringMatchers;
import io.basc.framework.util.observe.container.TreeMapRegistry;
import io.basc.framework.util.observe.event.ChangeEvent;
import lombok.NonNull;

/**
 * 动态管理日志等级管理
 * 
 * @author wcnnkh
 *
 */
public final class LevelManager extends TreeMapRegistry<String, Level> {
	private StringMatcher nameMatcher = StringMatchers.PREFIX;

	public LevelManager(@NonNull EventPublishService<ChangeEvent<KeyValue<String, Level>>> eventPublishService) {
		super(eventPublishService);
	}

	public StringMatcher getNameMatcher() {
		return nameMatcher;
	}

	public void setNameMatcher(StringMatcher nameMatcher) {
		this.nameMatcher = nameMatcher;
		setComparator(nameMatcher);
	}

	public boolean exists(String name) {
		if (containsKey(name)) {
			return true;
		}

		for (String key : keySet()) {
			if (nameMatcher.match(key, name)) {
				return true;
			}
		}

		return false;
	}

	@Nullable
	public Level getLevel(String name) {
		Level level = get(name);
		if (level != null) {
			return level;
		}

		for (Entry<String, Level> entry : entrySet()) {
			if (nameMatcher.match(entry.getKey(), name)) {
				return entry.getValue();
			}
		}
		return null;
	}
}
