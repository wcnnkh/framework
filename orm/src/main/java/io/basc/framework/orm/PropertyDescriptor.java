package io.basc.framework.orm;

import io.basc.framework.mapper.Member;
import io.basc.framework.util.Range;
import io.basc.framework.util.element.Elements;

/**
 * 一个属性的定义
 * 
 * @author wcnnkh
 *
 */
public interface PropertyDescriptor extends Member {

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