package io.basc.framework.hibernate;

import io.basc.framework.core.utils.ArrayUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.hibernate.Session;

public class SessionProxyInvocationHandler implements InvocationHandler{
	private Session targetSession;
	
	public SessionProxyInvocationHandler(Session targetSession){
		this.targetSession = targetSession;
	}
	
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if(ArrayUtils.isEmpty(args)){
			if(method.getName().equals("getTargetSession")){
				return targetSession;
			}else if(method.getName().equals("close")){
				//ignore 忽略关闭行为
				return null;
			}
		}
		return method.invoke(targetSession, args);
	}
}
