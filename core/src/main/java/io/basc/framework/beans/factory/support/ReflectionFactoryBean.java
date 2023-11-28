package io.basc.framework.beans.factory.support;

import java.lang.reflect.Executable;

import io.basc.framework.beans.factory.FactoryBean;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.reflect.ReflectionExecutor;
import io.basc.framework.util.element.Elements;

public class ReflectionFactoryBean<T extends Executable> extends ReflectionExecutor<T>
		implements FactoryBean<Object>, Executor {

	public ReflectionFactoryBean(T executable) {
		super(executable);
	}

	@Override
	public Object execute() throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object execute(Elements<Object> args) throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSingleton() {
		// TODO Auto-generated method stub
		return false;
	}

}
