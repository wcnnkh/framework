package io.basc.framework.mapper;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.Members;
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

	TypeDescriptor getTypeDescriptor();

	Members<T> getMembers();
}
