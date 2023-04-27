package io.basc.framework.mapper;

import java.lang.reflect.Method;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.value.Value;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MethodGetter extends AbstractGetter {
	public static final String BOOLEAN_METHOD_PREFIX = "is";
	public static final String METHOD_PREFIX = "get";

	private final Method method;
	private volatile String name;
	private volatile TypeDescriptor typeDescriptor;

	public MethodGetter(Method method) {
		Assert.requiredArgument(method != null, "method");
		this.method = method;
	}

	public String getName() {
		if (name == null) {
			synchronized (this) {
				if (name == null) {
					name = method.getName();
					if (method.getReturnType() == boolean.class && name.startsWith(BOOLEAN_METHOD_PREFIX)) {
						name = name.substring(BOOLEAN_METHOD_PREFIX.length());
					} else if (name.startsWith(METHOD_PREFIX)) {
						name = name.substring(METHOD_PREFIX.length());
					}
					name = StringUtils.toLowerCase(name, 0, 1);
				}
			}
		}
		return name;
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		if (typeDescriptor == null) {
			synchronized (this) {
				if (typeDescriptor == null) {
					typeDescriptor = TypeDescriptor.forMethodReturnType(method);
				}
			}
		}
		return typeDescriptor;
	}

	@Override
	public Object get(Value source) {
		return ReflectionUtils.invoke(method, source == null ? null : source.getSource(), new Object[0]);
	}

	@Override
	public MethodGetter rename(String name) {
		MethodGetter methodGetter = new MethodGetter(method);
		methodGetter.name = name;
		methodGetter.typeDescriptor = this.typeDescriptor;
		return methodGetter;
	}

}
