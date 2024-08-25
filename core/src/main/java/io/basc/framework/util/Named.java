package io.basc.framework.util;

import io.basc.framework.util.element.Elements;

public interface Named {
	String getName();

	/**
	 * 别名
	 * 
	 * @return
	 */
	default Elements<String> getAliasNames() {
		return Elements.empty();
	}
}
