package io.basc.framework.data.kv;

import io.basc.framework.codec.Encoder;
import io.basc.framework.util.Wrapper;

public class DefaultKeyOperationsWrapper<K, KF extends Encoder<K, K>, W extends KeyOperations<K>> extends Wrapper<W>
		implements KeyOperationsWrapper<K> {
	private KF keyFomatter;

	public DefaultKeyOperationsWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public W getSourceOperations() {
		return wrappedTarget;
	}

	@Override
	public KF getKeyFomatter() {
		return keyFomatter;
	}

	public void setKeyFomatter(KF keyFomatter) {
		this.keyFomatter = keyFomatter;
	}
}
