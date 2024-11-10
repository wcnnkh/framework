package io.basc.framework.mapper.stereotype;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Items;
import io.basc.framework.util.alias.Named;

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
