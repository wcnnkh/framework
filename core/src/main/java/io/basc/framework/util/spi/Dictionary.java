package io.basc.framework.util.spi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Publisher;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.register.container.TreeMapRegistry;
import lombok.NonNull;

public class Dictionary<K, V> extends TreeMapRegistry<K, V> {

	public Dictionary() {
		this(Publisher.empty());
	}

	public Dictionary(@NonNull Publisher<? super Elements<ChangeEvent<K>>> publisher) {
		super((events) -> {
			// 转换并去重
			Map<K, ChangeEvent<K>> changeMap = events
					.map((e) -> new ChangeEvent<>(e.getSource().getKey(), e.getChangeType()))
					.collect(Collectors.toMap((e) -> e.getSource(), (e) -> e, (a, b) -> b, LinkedHashMap::new));
			return publisher.publish(Elements.of(changeMap.values()));
		});
	}
}
