package io.basc.framework.util.dict;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Keys;
import io.basc.framework.util.Publisher;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.register.container.TreeMapRegistry;
import lombok.NonNull;

public class Configuration<V, T extends Keys<String>> extends MergedKeys<String, T> {
	private final TreeMapRegistry<String, V> custom;

	public Configuration(@NonNull Publisher<? super Elements<ChangeEvent<String>>> publisher) {
		super(publisher, (e) -> e instanceof ConfigurationModule);
		this.custom = new TreeMapRegistry<>((events) -> {
			// 转换并去重
			Map<String, ChangeEvent<String>> changeMap = events
					.map((e) -> new ChangeEvent<>(e.getSource().getKey(), e.getChangeType()))
					.collect(Collectors.toMap((e) -> e.getSource(), (e) -> e, (a, b) -> b, LinkedHashMap::new));
			return publisher.publish(Elements.of(changeMap.values()));
		});
	}

	public TreeMapRegistry<String, V> custom() {
		return custom;
	}
}
