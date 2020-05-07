package scw.mapper.support;

import scw.core.parameter.DefaultParameterDescriptor;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterFactory;
import scw.core.utils.StringUtils;
import scw.mapper.FieldContext;
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
	protected Object getValue(FieldContext fieldContext) {
		Setter setter = fieldContext.getField().getSetter();
		String name = nestingName ? getNestingDisplayName(fieldContext) : getDisplayName(setter);
		if(!StringUtils.isEmpty(basePrefix)){
			name = basePrefix + "." + name;
		}
		
		ParameterDescriptor parameterDescriptor = new DefaultParameterDescriptor(name, setter);
		return parameterFactory.getParameter(parameterDescriptor);
	}

	@Override
	protected boolean isNesting(FieldContext fieldContext) {
		if (!nestingName) {
			return false;
		}
		
		return super.isNesting(fieldContext);
	}
}
