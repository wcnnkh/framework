package io.basc.framework.orm;

import io.basc.framework.data.repository.IndexInfo;
import io.basc.framework.mapper.stereotype.FieldDescriptor;
import io.basc.framework.util.Range;
import io.basc.framework.util.element.Elements;

/**
 * 一个属性的定义
 * 
 * @author wcnnkh
 *
 */
public interface ColumnDescriptor extends FieldDescriptor {

	boolean isAutoIncrement();

	String getCharsetName();

	String getComment();

	boolean isIncrement();

	Elements<? extends Range<Double>> getNumberRanges();

	boolean isPrimaryKey();

	boolean isUnique();

	boolean isVersion();

	boolean isEntity();

	boolean isNullable();

	Elements<IndexInfo> getIndexs();

	default boolean hasIndex() {
		return isPrimaryKey() || isUnique() || !getIndexs().isEmpty();
	}
}