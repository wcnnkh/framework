package scw.mapper.support;

import scw.core.parameter.DefaultParameterDescriptor;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterFactory;
import scw.core.utils.StringUtils;
import scw.mapper.Field;
import scw.mapper.Setter;

public class ParameterFactoryMapping extends AbstractMapping {
	private String basePrefix;
	private boolean nestingName;
	private ParameterFactory parameterFactory;

	public ParameterFactoryMapping(ParameterFactory parameterFactory, boolean nestingName, String basePrefix) {
		this.parameterFactory = parameterFactory;
		this.nestingName = nestingName;
		this.basePrefix = basePrefix;
	}

	@Override
	protected Object getValue(Field field) {
		Setter setter = field.getSetter();
		String name = nestingName ? getNestingDisplayName(field) : getDisplayName(setter);
		if(!StringUtils.isEmpty(basePrefix)){
			name = basePrefix + "." + name;
		}
		
		ParameterDescriptor parameterDescriptor = new DefaultParameterDescriptor(name, setter);
		return parameterFactory.getParameter(parameterDescriptor);
	}

	@Override
	protected boolean isNesting(Field field) {
		if (!nestingName) {
			return false;
		}
		
		return super.isNesting(field);
	}
}
