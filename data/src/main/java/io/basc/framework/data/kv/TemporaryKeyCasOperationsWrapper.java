package io.basc.framework.data.kv;

public interface TemporaryKeyCasOperationsWrapper<K>
		extends TemporaryKeyCasOperations<K>, KeyCasOperationsWrapper<K>, TemporaryKeyOperationsWrapper<K> {
	@Override
	TemporaryKeyCasOperations<K> getSourceOperations();
}
