package io.basc.framework.orm;

import io.basc.framework.mapper.Element;
import io.basc.framework.util.Range;
import io.basc.framework.util.element.Elements;

/**
 * 一个属性的定义
 * 
 * @author wcnnkh
 *
 */
public interface Property extends Element {

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
}