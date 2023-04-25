package io.basc.framework.mapper;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ParentDiscover;
import io.basc.framework.value.Value;

/**
 * 一个字段的定义
 * 
 * @author wcnnkh
 *
 */
public interface Field extends ParentDiscover<Field> {
	/**
	 * 类型描述
	 * 
	 * @return
	 */
	TypeDescriptor getTypeDescriptor();

	/**
	 * 名称
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 别名
	 * 
	 * @return
	 */
	Elements<String> getAliasNames();

	/**
	 * 是否支持读取
	 * 
	 * @return
	 */
	boolean isSupportGetter();

	/**
	 * 从给定来源中获取值
	 * 
	 * @param source
	 * @return
	 */
	Value get(Value source);

	/**
	 * 是否支持插入
	 * 
	 * @return
	 */
	boolean isSupportSetter();

	/**
	 * 在指定目标中插入值
	 * 
	 * @param target
	 * @param value
	 */
	void set(Value target, Value value);
}
