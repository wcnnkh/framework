package run.soeasy.framework.core.invoke;

import java.util.Arrays;
import java.util.Iterator;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.property.PropertyDescriptor;

/**
 * 可执行的
 * 
 * @author wcnnkh
 *
 */
public interface ExecutableMetadata extends ExecutableDescriptor {
	default boolean canExecuted(@NonNull Class<?>... parameterTypes) {
		Iterator<PropertyDescriptor> iterator1 = getParameterTemplate().iterator();
		Iterator<Class<?>> iterator2 = Arrays.asList(parameterTypes).iterator();
		while (iterator1.hasNext() && iterator2.hasNext()) {
			PropertyDescriptor parameterDescriptor = iterator1.next();
			Class<?> type = iterator2.next();
			if (!type.isAssignableFrom(parameterDescriptor.getReturnTypeDescriptor().getType())) {
				return false;
			}
		}
		return !iterator1.hasNext() && !iterator2.hasNext();
	}

	String getName();

	/**
	 * 声明的类型描述
	 * 
	 * @return
	 */
	TypeDescriptor getDeclaringTypeDescriptor();

	/**
	 * 
	 * 异常类型
	 * 
	 * @return
	 */
	Elements<TypeDescriptor> getExceptionTypeDescriptors();

	ParameterTemplate getParameterTemplate();
}
