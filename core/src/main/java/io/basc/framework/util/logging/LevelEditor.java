package io.basc.framework.util.logging;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import io.basc.framework.util.Elements;
import io.basc.framework.util.match.StringMatcher;
import io.basc.framework.util.match.StringMatchers;
import io.basc.framework.util.observe.Listener;
import io.basc.framework.util.observe.Registration;
import io.basc.framework.util.observe.event.ChangeEvent;
import io.basc.framework.util.observe.event.EventDispatcher;
import io.basc.framework.util.observe.event.Exchange;
import io.basc.framework.util.observe.register.container.TreeMapRegistry;

public class LevelEditor extends TreeMapRegistry<String, Level> implements LevelFactory {
	private final Exchange<Elements<ChangeEvent<String>>> exchange;
	private StringMatcher stringMatcher;

	public LevelEditor() {
		this(new EventDispatcher<>());
	}

	public LevelEditor(Exchange<Elements<ChangeEvent<String>>> exchange) {
		super((events) -> {
			// 转换并去重
			Map<String, ChangeEvent<String>> changeMap = events
					.map((e) -> new ChangeEvent<>(e.getSource().getKey(), e.getChangeType()))
					.collect(Collectors.toMap((e) -> e.getSource(), (e) -> e, (a, b) -> b, LinkedHashMap::new));
			return exchange.publish(Elements.of(changeMap.values()));
		});
		this.exchange = exchange;
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

	@Override
	public boolean match(String name, String config) {
		return stringMatcher.match(config, name);
	}

	@Override
	public Registration registerListener(Listener<? super Elements<ChangeEvent<String>>> listener) {
		return exchange.registerListener(listener);
	}
}
