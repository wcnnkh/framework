package io.basc.framework.util.logging;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Publisher;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.match.StringMatcher;
import io.basc.framework.util.match.StringMatchers;
import io.basc.framework.util.register.container.TreeMapRegistry;
import lombok.NonNull;

public class LevelEditor extends TreeMapRegistry<String, Level> implements LevelFactory {
	private StringMatcher stringMatcher;

	public LevelEditor(@NonNull Publisher<? super Elements<ChangeEvent<String>>> publisher) {
		super((events) -> {
			// 转换并去重
			Map<String, ChangeEvent<String>> changeMap = events
					.map((e) -> new ChangeEvent<>(e.getSource().getKey(), e.getChangeType()))
					.collect(Collectors.toMap((e) -> e.getSource(), (e) -> e, (a, b) -> b, LinkedHashMap::new));
			return publisher.publish(Elements.of(changeMap.values()));
		});
		setComparator(StringMatchers.PREFIX);
	}

	public StringMatcher getStringMatcher() {
		return stringMatcher;
	}

	public void setStringMatcher(StringMatcher stringMatcher) {
		this.stringMatcher = stringMatcher;
		setComparator(stringMatcher);
	}

	@Override
	public Level getLevel(String name) {
		Level level = get(name);
		if (level != null) {
			return level;
		}

		for (Entry<String, Level> entry : entrySet()) {
			if (stringMatcher.match(entry.getKey(), name)) {
				return entry.getValue();
			}
		}
		return null;
	}
}
