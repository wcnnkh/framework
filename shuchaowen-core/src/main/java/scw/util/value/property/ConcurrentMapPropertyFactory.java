package scw.util.value.property;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import scw.util.value.Value;

public class ConcurrentMapPropertyFactory extends
		AbstractConcurrentMapPropertyFactory {
	private ConcurrentMap<String, Value> targetMap;

	public ConcurrentMapPropertyFactory() {
		this.targetMap = new ConcurrentHashMap<String, Value>();
	}

	public ConcurrentMapPropertyFactory(ConcurrentMap<String, Value> targetMap) {
		this.targetMap = targetMap;
	}

	@Override
	protected ConcurrentMap<String, Value> getTargetMap() {
		return targetMap;
	}
}
