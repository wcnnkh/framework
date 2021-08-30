package io.basc.framework.aop;

import io.basc.framework.reflect.MethodInvoker;

public interface ProxyInvoker extends MethodInvoker{
	MethodInvoker getOriginalInvoker();
}
