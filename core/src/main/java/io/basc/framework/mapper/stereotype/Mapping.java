package io.basc.framework.mapper.stereotype;

import io.basc.framework.util.Items;
import io.basc.framework.util.Named;
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
