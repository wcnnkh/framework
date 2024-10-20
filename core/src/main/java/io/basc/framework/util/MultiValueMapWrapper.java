package io.basc.framework.util;

import java.util.List;
import java.util.Map;

public class MultiValueMapWrapper<K, V> extends AbstractMultiValueMap<K, V> {
	private static final long serialVersionUID = 1L;
	private Map<K, List<V>> targetMap;

	public MultiValueMapWrapper(Map<K, List<V>> targetMap) {
		Assert.notNull(targetMap);
		this.targetMap = targetMap;
	}

	@Override
	protected Map<K, List<V>> getTargetMap() {
		return targetMap;
	}

}
