package io.basc.framework.context.annotation;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.BeanResolver;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.factory.support.FactoryBeanDefinition;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.ParameterDescriptors;
import io.basc.framework.mapper.support.ExecutableParameterDescriptors;
import io.basc.framework.mapper.support.ExecutableParameterDescriptorsIterator;
import io.basc.framework.util.CollectionUtils;

public class ExecutableBeanDefinition extends FactoryBeanDefinition {
	private static Logger logger = LoggerFactory.getLogger(ExecutableBeanDefinition.class);
	private final Class<?> sourceClass;
	private final Executable executable;
	private final ParameterDescriptors parameterDescriptors;

	public ExecutableBeanDefinition(BeanFactory beanFactory, Class<?> sourceClass, Executable executable) {
		super(beanFactory, executable instanceof Method ? TypeDescriptor.forMethodReturnType((Method) executable)
				: new TypeDescriptor(ResolvableType.forClass(sourceClass), sourceClass, executable));
		this.sourceClass = sourceClass;
		this.executable = executable;
		this.parameterDescriptors = new ExecutableParameterDescriptors(executable.getDeclaringClass(), executable);
	}

	@Override
	protected String getDefaultId() {
		return executable.getName();
	}

	@Override
	public Collection<String> getNames() {
		Collection<String> names = super.getNames();
		return CollectionUtils.isEmpty(names) ? Arrays.asList(getTypeDescriptor().getName()) : names;
	}

	private final AtomicBoolean error = new AtomicBoolean();

	public boolean isInstance() {
		boolean accept = getBeanResolver().isAccept(parameterDescriptors);
		if (!accept) {
			if (!error.get() && error.compareAndSet(false, true)) {
				logger.error("not found {} accept executable {}", this, executable);
			}
		}
		return accept;
	}

	@Override
	public Object create() throws InstanceException {
		if (!isInstance()) {
			throw new UnsupportedException("不支持的构造方式");
		}

		return createInternal(getBeanResolver(), getTypeDescriptor(), parameterDescriptors,
				getBeanResolver().getParameters(parameterDescriptors));
	}

	@Override
	protected Object createInternal(BeanResolver beanResolver, TypeDescriptor typeDescriptor,
			ParameterDescriptors parameterDescriptors, Object[] params) {
		if (executable instanceof Method) {
			Method method = ReflectionUtils.getDeclaredMethods(sourceClass).all().find(executable.getName(),
					parameterDescriptors.getElements().map((e) -> e.getTypeDescriptor().getType()));
			Object bean = ReflectionUtils.invoke(method,
					Modifier.isStatic(method.getModifiers()) ? null : getBeanFactory().getInstance(sourceClass));
			if (getBeanFactory().getAop().isProxy(bean)) {
				// 已经被代理过的
				return bean;
			}

			// 必须要是接口，因为非接口不一定是无法保证一定可以代理实例
			if (method.getReturnType().isInterface() && (isAopEnable(getTypeDescriptor(), getBeanResolver())
					|| isAopEnable(TypeDescriptor.forObject(bean), getBeanResolver()))) {
				return createInstanceProxy(getAop(), bean, sourceClass, null).create();
			}
			return bean;
		}
		return super.createInternal(beanResolver, typeDescriptor, parameterDescriptors, params);
	}

	@Override
	public Iterator<ParameterDescriptors> iterator() {
		if (executable instanceof Method) {
			return new ExecutableParameterDescriptorsIterator(sourceClass, (Method) executable, true);
		}
		return super.iterator();
	}

	public Class<?> getSourceClass() {
		return sourceClass;
	}

	public Executable getExecutable() {
		return executable;
	}

}
