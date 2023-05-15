package io.basc.framework.mapper.support;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.MethodParameter;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MethodSetter extends AbstractSetter {
	public static final String METHOD_PREFIX = "set";
	private final Method method;
	private volatile TypeDescriptor typeDescriptor;
	private volatile String name;

	public MethodSetter(Method method) {
		Assert.requiredArgument(method != null, "method");
		Assert.isTrue(method.getParameterCount() == 1, "The inserted method can only have one parameter");
		this.method = method;
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		if (typeDescriptor == null) {
			synchronized (this) {
				if (typeDescriptor == null) {
					this.typeDescriptor = new TypeDescriptor(new MethodParameter(method, 0));
				}
			}
		}
		return typeDescriptor;
	}

	@Override
	public String getName() {
		if (name == null) {
			synchronized (this) {
				if (name == null) {
					name = method.getName();
					if (name.endsWith(METHOD_PREFIX)) {
						name = name.substring(METHOD_PREFIX.length());
						// 首字母转小写
						name = StringUtils.toLowerCase(name, 0, 1);
					}
				}
			}
		}
		return name;
	}

	@Override
	public void set(Object target, Object value) {
		ReflectionUtils.invoke(method, Modifier.isStatic(method.getModifiers()) ? null : target, value);
	}

	@Override
	public MethodSetter rename(String name) {
		MethodSetter methodSetter = new MethodSetter(this.method);
		methodSetter.typeDescriptor = this.typeDescriptor;
		methodSetter.name = name;
		return methodSetter;
	}

}
