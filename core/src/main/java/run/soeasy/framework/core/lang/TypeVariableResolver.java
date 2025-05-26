package run.soeasy.framework.core.lang;

import java.lang.reflect.TypeVariable;

/**
 * TypeVariable解析器
 * 
 * @author soeasy.run
 *
 */
@FunctionalInterface
public interface TypeVariableResolver {
	ResolvableType resolveTypeVariable(TypeVariable<?> typeVariable);
}
