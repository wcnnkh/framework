package scw.beans;

import scw.core.instance.InstanceFactory;

public interface BeanFactory extends InstanceFactory {
	boolean contains(String name);
}
