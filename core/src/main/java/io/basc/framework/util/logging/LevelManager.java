package io.basc.framework.util.logging;

import java.util.logging.Level;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Listener;
import io.basc.framework.util.Publisher;
import io.basc.framework.util.Registration;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.actor.EventDispatcher;
import io.basc.framework.util.actor.Exchange;
import io.basc.framework.util.dict.MergedKeys;
import lombok.NonNull;

public class LevelManager extends MergedKeys<String, ObservableLevelFactory> implements LevelFactory {
	private final LevelEditor custom;
	private final Exchange<Elements<ChangeEvent<String>>> exchange;

	public LevelManager() {
		this(new EventDispatcher<>());
	}

	public LevelManager(@NonNull Publisher<? super Elements<ChangeEvent<String>>> keyEventsPublisher) {
		super(keyEventsPublisher, (e) -> true);
		this.custom = new LevelEditor(keyEventsPublisher);
		this.exchange = exchange;
	}

	@Override
	public Registration registerListener(Listener<? super Elements<ChangeEvent<String>>> listener) {
		return exchange.registerListener(listener);
	}

	@Override
	public Level getLevel(String name) {
		Level level = custom.getLevel(name);
		if (level != null) {
			return level;
		}

		for (LevelFactory factory : includes()) {
			level = factory.getLevel(name);
			if (level != null) {
				return level;
			}
		}
		return null;
	}

	@Override
	public boolean match(String name, String config) {
		return custom.match(name, config)
				|| includes().readAsBoolean((set) -> set.stream().anyMatch((e) -> e.getPayload().match(name, config)));
	}

	public LevelEditor custom() {
		return custom;
	}
}
