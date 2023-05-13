package io.basc.framework.factory;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;

public interface InstanceFactory2 {
	boolean isInstance(TypeDescriptor typeDescriptor);

	Object getInstance(TypeDescriptor typeDescriptor);

	boolean isInstanceByTypes(TypeDescriptor typeDescriptor, Elements<? extends TypeDescriptor> parameterTypes);

	Object getInstanceByTypes(TypeDescriptor typeDescriptor, Elements<? extends Value> args);

	boolean isInstanceByParams(TypeDescriptor typeDescriptor, Elements<? extends Parameter> parameters);

	Object getInstanceByParams(TypeDescriptor typeDescriptor, Elements<? extends Parameter> parameters);
}
