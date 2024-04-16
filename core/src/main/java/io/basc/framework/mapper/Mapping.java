package io.basc.framework.mapper;

import io.basc.framework.util.Assert;
import io.basc.framework.util.Named;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.element.Elements;

/**
 * 映射
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public interface Mapping<T extends FieldDescriptor> extends Named {
	String getName();

	Elements<String> getAliasNames();

	/**
	 * 可能存在多个重名的element
	 * 
	 * @see FieldDescriptor#getName()
	 * @param name
	 * @return
	 */
	default Elements<T> getElements(String name) {
		Assert.requiredArgument(StringUtils.hasText(name), "name");
		return getElements().filter((field) -> field.getName().equals(name));
	}

	Elements<T> getElements();
}
