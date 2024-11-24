package io.basc.framework.mapper.io.template;

import io.basc.framework.core.execution.param.Parameters;

public interface Record extends Parameters {
	public static Record forArgs(Iterable<? extends Object> args) {
		return () -> Parameters.forArgs(args).getElements();
	}
}
