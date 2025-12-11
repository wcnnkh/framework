package run.soeasy.framework.core.streaming;

import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.domain.KeyValue;

@RequiredArgsConstructor
@Getter
public class StreamableMapping<K, V> implements Mapping<K, V> {
	private final Streamable<KeyValue<K, V>> keyValues;

	@Override
	public Stream<KeyValue<K, V>> stream() {
		return keyValues.stream();
	}

	@Override
	public Mapping<K, V> reload() {
		Streamable<KeyValue<K, V>> reloadedSource = keyValues.reload();
		if (reloadedSource == keyValues) {
			return this;
		}
		return new StreamableMapping<>(reloadedSource);
	}
}