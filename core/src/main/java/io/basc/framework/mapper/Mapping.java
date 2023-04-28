package io.basc.framework.mapper;

import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.StringUtils;

/**
 * 映射
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public interface Mapping<T extends Field> {
	String getName();

	Elements<String> getAliasNames();

	Elements<T> getElements();

	/**
	 * 可能存在多个重名的element
	 * 
	 * @param name
	 * @return
	 */
	default Elements<T> getElements(String name) {
		Assert.requiredArgument(StringUtils.hasText(name), "name");
		return getElements().filter((e) -> e.getName().equals(name));
	}
}
