package io.basc.framework.util.logging;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.event.ChangeEvent;
import io.basc.framework.util.event.EventsDispatcher;
import io.basc.framework.util.event.Exchange;
import io.basc.framework.util.register.container.TreeSetRegistry;
import lombok.Getter;

/**
 * 动态管理日志等级管理
 * 
 * @author wcnnkh
 *
 */
@Getter
public class LevelManager extends LevelEditor {
	private Exchange<Elements<ChangeEvent<String>>> exchange;

	private final TreeSetRegistry<LevelFactory> registry = new TreeSetRegistry<>((events) -> {
		Map<String, ChangeEvent<String>> changeMap = events
				.flatMap((e) -> e.getSource().getElements()
						.map((keyValue) -> new ChangeEvent<>(keyValue.getKey(), e.getChangeType())))
				.collect(Collectors.toMap((e) -> e.getSource(), (e) -> e, (a, b) -> b, LinkedHashMap::new));
		return exchange.publish(Elements.of(changeMap.values()));
	});

	public LevelManager() {
		this(new EventsDispatcher<>());
	}

	public LevelManager(Exchange<Elements<ChangeEvent<String>>> exchange) {
		super(exchange);
	}

	@Override
	public Level getLevel(String name) {
		Level level = super.getLevel(name);
		if (level == null) {
			for (LevelFactory factory : registry.getElements()) {
				level = factory.getLevel(name);
				if (level != null) {
					break;
				}
			}
		}
		return level;
	}

	@Override
	public Elements<KeyValue<String, Level>> getElements() {
		return super.getElements().concat(registry.getElements().flatMap((e) -> e.getElements())).distinct();
	}

	@Override
	public Elements<KeyValue<String, Level>> getElements(String key) {
		return super.getElements(key).concat(registry.getElements().flatMap((e) -> e.getElements(key))).distinct();
	}

	@Override
	public boolean match(String name, String config) {
		return super.match(name, config) || registry.getElements().anyMatch((e) -> e.match(name, config));
	}
}
