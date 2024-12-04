package io.basc.framework.core.convert.transform;

public interface Parameters extends ParameterMapping<Parameter> {
	public static interface ParametersWrapper<W extends Parameters>
			extends Parameters, ParameterMappingWrapper<Parameter, W> {

	}

	public static Parameters forArgs(Object... args) {
		return null;
	}

	public static Parameters forProperties(Property... properties) {
		return null;
	}
}
