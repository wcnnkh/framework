package io.basc.framework.aop;

import io.basc.framework.core.reflect.MethodInvoker;

public interface ProxyInvoker extends MethodInvoker{
	MethodInvoker getOriginalInvoker();
}
