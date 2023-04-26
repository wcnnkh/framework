package io.basc.framework.mapper;

import java.lang.reflect.Method;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.MethodParameter;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.value.Value;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "method")
public class MethodSetter implements Setter {
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
	public void set(Value target, Object value) {
		ReflectionUtils.invoke(method, target == null ? null : target.getSource(), value);
	}

	@Override
	public Setter rename(String name) {
		MethodSetter methodSetter = new MethodSetter(this.method);
		methodSetter.typeDescriptor = this.typeDescriptor;
		methodSetter.name = name;
		return methodSetter;
	}

}
