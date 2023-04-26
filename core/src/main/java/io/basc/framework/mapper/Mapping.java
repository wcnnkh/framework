package io.basc.framework.mapper;

import io.basc.framework.util.Elements;

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
}
