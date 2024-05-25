package io.basc.framework.mapper.entity;

import io.basc.framework.mapper.property.Items;
import io.basc.framework.mapper.property.Named;
import io.basc.framework.util.element.Elements;

/**
 * 映射
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public interface Mapping<T extends FieldDescriptor> extends Named, Items<T> {

	/**
	 * 获取所有字段
	 */
	@Override
	Elements<T> getElements();
}
