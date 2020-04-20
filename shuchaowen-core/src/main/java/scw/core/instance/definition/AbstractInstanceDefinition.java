package scw.core.instance.definition;

import java.lang.reflect.AnnotatedElement;

import scw.core.instance.AbstractInstanceBuilder;
import scw.core.instance.InstanceFactory;
import scw.core.utils.XUtils;

public abstract class AbstractInstanceDefinition extends
		AbstractInstanceBuilder<Object> implements InstanceDefinition {
	private final InstanceFactory instanceFactory;
	protected String id;

	public AbstractInstanceDefinition(Class<?> targetClass,
			InstanceFactory instanceFactory) {
		super(targetClass);
		this.id = targetClass.getName();
		this.instanceFactory = instanceFactory;
	}

	public String getId() {
		return id;
	}

	public AnnotatedElement getAnnotatedElement() {
		return getTargetClass();
	}

	public void init(Object instance) throws Exception {
		if (instance instanceof InstanceDefinitionAware) {
			((InstanceDefinitionAware) instance).setInstanceDefinition(this);
		}

		if (instance instanceof InstanceFactoryAware) {
			((InstanceFactoryAware) instance)
					.setInstanceFactory(instanceFactory);
		}
		XUtils.init(instance);
	}
}
