package io.basc.framework.orm;

import java.util.Collection;

import io.basc.framework.data.domain.Range;
import io.basc.framework.mapper.Field;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.stream.Processor;

public interface Property extends PropertyMetadata {
	/**
	 * 对应的字段
	 * 
	 * @return
	 */
	Field getField();

	Collection<String> getAliasNames();

	Collection<Range<Double>> getNumberRanges();

	boolean isVersion();

	boolean isIncrement();

	boolean isEntity();

	default <V, E extends Throwable> V getValueByNames(Processor<String, V, E> processor) throws E {
		V value = processor.process(getName());
		if (value != null) {
			return value;
		}

		Collection<String> names = getAliasNames();
		if (!CollectionUtils.isEmpty(names)) {
			for (String name : names) {
				value = processor.process(name);
				if (value != null) {
					return value;
				}
			}
		}

		Field field = getField();
		if (field != null) {
			value = field.getValueByNames(processor);
			if (value != null) {
				return value;
			}
		}
		return null;
	}
}
