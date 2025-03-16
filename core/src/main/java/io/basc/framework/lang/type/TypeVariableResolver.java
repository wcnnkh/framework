package io.basc.framework.lang.type;

import java.lang.reflect.TypeVariable;

/**
 * TypeVariable解析器
 * 
 * @author soeasy.run
 *
 */
@FunctionalInterface
public interface TypeVariableResolver {
	TypeProvider resolveTypeVariable(TypeVariable<?> typeVariable);
}
