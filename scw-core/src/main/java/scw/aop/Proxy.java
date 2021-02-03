package scw.aop;

import scw.instance.InstanceCreator;

public interface Proxy extends InstanceCreator<Object>{
	Class<?> getTargetClass();
}
