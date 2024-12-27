package io.basc.framework.util.logging;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.exchange.Publisher;
import io.basc.framework.util.match.StringMatcher;
import io.basc.framework.util.match.StringMatchers;
import io.basc.framework.util.register.container.TreeMapContainer;
import lombok.NonNull;

public class LevelEditor extends TreeMapContainer<String, Level> implements LevelFactory {
	private StringMatcher stringMatcher;

	public LevelEditor(@NonNull Publisher<? super Elements<ChangeEvent<String>>> publisher) {
		setComparator(StringMatchers.PREFIX);
	}

	@Override
	public void setPublisher(Publisher<? super Elements<ChangeEvent<KeyValue<String, Level>>>> publisher) {
		super.setPublisher((events) -> {
			// 转换并去重
			Map<String, ChangeEvent<KeyValue<String, Level>>> changeMap = events.collect(Collectors
					.toMap((e) -> e.getSource().getKey(), (e) -> e, (a, b) -> b, () -> new TreeMap<>(getComparator())));
			return publisher.publish(Elements.of(changeMap.values()));
		});
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
