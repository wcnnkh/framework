package io.basc.framework.beans.factory;

import io.basc.framework.lang.Nullable;

public interface HierarchicalBeanFactory extends BeanFactory {
	@Nullable
	BeanFactory getParentBeanFactory();

	boolean containsLocalBean(String name);
}
