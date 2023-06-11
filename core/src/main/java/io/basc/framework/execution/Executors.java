package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.Elements;

public interface Executors extends Executable {
	Elements<? extends Executor> getExecutors();
	
	@Override
	default boolean isExecuted(Elements<? extends TypeDescriptor> types) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	default Object execute(Elements<? extends TypeDescriptor> types, Elements<? extends Object> args) throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}
}
