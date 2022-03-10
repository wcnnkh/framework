package io.basc.framework.data.kv;

public class DefaultObjectOperationsWrapper<K, W extends ObjectOperations<K>>
		extends DefaultKeyValueOperations<K, Object, W> implements ObjectOperationsWrapper<K> {

	public DefaultObjectOperationsWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}
}
