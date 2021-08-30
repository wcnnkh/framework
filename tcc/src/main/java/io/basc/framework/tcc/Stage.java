package io.basc.framework.tcc;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.BeanFactoryAware;
import io.basc.framework.reflect.ReflectionUtils;
import io.basc.framework.reflect.SerializableMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Stage extends SerializableMethod implements Runnable, BeanFactoryAware {
	private static final long serialVersionUID = 1L;
	private transient BeanFactory beanFactory;

	private final String beanName;
	private final Object[] args;
	private transient Object instance;

	public Stage(Class<?> declaringClass, Method method, String beanName, Object[] args) {
		super(declaringClass, method);
		this.beanName = beanName;
		this.args = args;
	}

	public Object getInstance() {
		return instance;
	}

	public void setInstance(Object instance) {
		this.instance = instance;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	public void run() {
		Method method = getMethod();
		if (method == null) {
			throw new IllegalArgumentException("not found method");
		}

		Object instance = null;
		if (!Modifier.isStatic(method.getModifiers())) {
			instance = getInstance();
			if (instance == null) {
				if (beanFactory == null) {
					throw new IllegalArgumentException("not found beanfactory");
				}

				if (!beanFactory.isInstance(beanName)) {
					throw new IllegalArgumentException("not found bean " + beanName);
				}

				instance = beanFactory.getInstance(beanName);
			}
		}

		ReflectionUtils.makeAccessible(method);
		ReflectionUtils.invokeMethod(method, instance, args);
	}

}
