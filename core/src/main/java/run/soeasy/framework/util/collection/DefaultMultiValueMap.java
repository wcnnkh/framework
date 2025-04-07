package run.soeasy.framework.util.collection;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DefaultMultiValueMap<K, V, M extends Map<K, List<V>>> extends AbstractMultiValueMap<K, V, M> {
	@NonNull
	private final M source;
}
