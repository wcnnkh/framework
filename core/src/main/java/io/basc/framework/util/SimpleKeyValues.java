package io.basc.framework.util;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SimpleKeyValues<K, V, W extends Elements<KeyValue<K, V>>>
		implements KeyValues<K, V>, ElementsWrapper<KeyValue<K, V>, W> {
	@NonNull
	private final W source;
}
