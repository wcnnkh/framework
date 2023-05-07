package io.basc.framework.orm.support;

import java.lang.reflect.AnnotatedElement;

import io.basc.framework.aop.support.ProxyUtils;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.env.Environment;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.value.PropertyFactory;

public class Configurator extends DefaultEntityMapper1 {
	protected final Object source;
	private final TypeDescriptor sourceType;

	public Configurator(Object source) {
		this(source, null);
	}

	public Configurator(Environment environment) {
		this(environment.getProperties(), TypeDescriptor.valueOf(PropertyFactory.class));
		setPlaceholderFormat(environment.getProperties());
	}

	public Configurator(Object source, @Nullable TypeDescriptor sourceType) {
		Assert.requiredArgument(source != null, "source");
		this.source = source;
		this.sourceType = sourceType == null ? TypeDescriptor.forObject(source) : sourceType;
	}

	public void configurationProperties(Object target) {
		if (target == null) {
			return;
		}

		transform(target, TypeDescriptor.valueOf(ProxyUtils.getFactory().getUserClass(target.getClass())));
	}

	public void configurationProperties(Object target, AnnotatedElement annotatedElement) {
		if (target == null) {
			return;
		}

		TypeDescriptor targetType = new TypeDescriptor(
				ResolvableType.forClass(ProxyUtils.getFactory().getUserClass(target.getClass())), null,
				annotatedElement);
		configurationProperties(target, targetType);
	}

	public void configurationProperties(Object target, TypeDescriptor targetType) {
		if (target == null) {
			return;
		}

		configurationProperties(this.source, this.sourceType, target, targetType);
	}

	public void transform(Object target) {
		if (target == null) {
			return;
		}

		transform(target, TypeDescriptor.forObject(target));
	}

	public void transform(Object target, TypeDescriptor targetType) {
		if (target == null) {
			return;
		}

		transform(this.source, this.sourceType, target, targetType);
	}
}
