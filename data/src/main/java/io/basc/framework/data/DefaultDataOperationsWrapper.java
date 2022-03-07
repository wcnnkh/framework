package io.basc.framework.data;

import io.basc.framework.codec.Codec;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.util.Wrapper;

public class DefaultDataOperationsWrapper<W extends DataOperations<K>, K> extends Wrapper<W>
		implements DataOperationsWrapper<K> {
	private Codec<K, K> keyFomatter;
	private Codec<Object, Object> valueFomatter;
	private ConversionService conversionService;

	public DefaultDataOperationsWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public DataOperations<K> getSourceOperations() {
		return wrappedTarget;
	}

	public Codec<K, K> getKeyFomatter() {
		return keyFomatter;
	}

	public void setKeyFomatter(Codec<K, K> keyFomatter) {
		this.keyFomatter = keyFomatter;
	}

	public Codec<Object, Object> getValueFomatter() {
		return valueFomatter;
	}

	public void setValueFomatter(Codec<Object, Object> valueFomatter) {
		this.valueFomatter = valueFomatter;
	}

	@Override
	public ConversionService getConversionService() {
		return conversionService == null ? DataOperationsWrapper.super.getConversionService() : conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
}
