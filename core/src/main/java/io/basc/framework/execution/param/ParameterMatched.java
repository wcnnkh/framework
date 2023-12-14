package io.basc.framework.execution.param;

import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import lombok.Data;

/**
 * 参数匹配
 * 
 * @see Parameters#apply(io.basc.framework.util.element.Elements)
 */
@Data
public class ParameterMatched {
	private ParameterDescriptor parameterDescriptor;
	private Parameter parameter;
	private boolean successful;

	/**
	 * 是否存在此参数
	 * 
	 * @return
	 */
	public boolean exists() {
		return parameterDescriptor != null;
	}

	public Object getValue() {
		return parameter == null ? null : parameter.getValue();
	}
}
