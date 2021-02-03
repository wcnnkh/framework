package scw.aop;

import scw.core.reflect.MethodInvoker;

public interface ProxyInvoker extends MethodInvoker{
	MethodInvoker getOriginalInvoker();
}
