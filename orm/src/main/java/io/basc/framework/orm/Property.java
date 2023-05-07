package io.basc.framework.orm;

import io.basc.framework.mapper.Field;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Range;

/**
 * 一个属性的定义
 * 
 * @author wcnnkh
 *
 */
public interface Property extends Field {

	boolean isAutoIncrement();

	String getCharsetName();

	String getComment();

	boolean isIncrement();

	Elements<? extends Range<Double>> getNumberRanges();

	boolean isPrimaryKey();

	boolean isUnique();

	boolean isVersion();

	boolean isEntity();
}