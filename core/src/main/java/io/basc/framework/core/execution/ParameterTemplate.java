package io.basc.framework.core.execution;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Wrapper;

/**
 * 参数模板
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
@FunctionalInterface
public interface ParameterTemplate {
	@FunctionalInterface
	public static interface ParameterTemplateWrapper<W extends ParameterTemplate>
			extends ParameterTemplate, Wrapper<W> {
		default Elements<ParameterDescriptor> getParameterDescriptors() {
			return getSource().getParameterDescriptors();
		}
	}

	Elements<ParameterDescriptor> getParameterDescriptors();

}
