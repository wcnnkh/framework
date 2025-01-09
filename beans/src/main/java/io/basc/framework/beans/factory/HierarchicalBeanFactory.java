package io.basc.framework.beans.factory;

import lombok.NonNull;

public interface HierarchicalBeanFactory extends BeanFactory {
	BeanFactory getParentBeanFactory();

	boolean containsLocalBean(@NonNull String name);
}
