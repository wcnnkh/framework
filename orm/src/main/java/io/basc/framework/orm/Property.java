package io.basc.framework.orm;

import java.util.Collection;

import io.basc.framework.data.domain.Range;
import io.basc.framework.mapper.Field;

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
}
