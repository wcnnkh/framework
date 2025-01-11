package io.basc.framework.core.execution;

import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.function.Wrapper;

/**
 * 参数模板
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
@FunctionalInterface
public interface ParameterDescriptorTemplate {
	@FunctionalInterface
	public static interface ParameterDescriptorTemplateWrapper<W extends ParameterDescriptorTemplate>
			extends ParameterDescriptorTemplate, Wrapper<W> {
		default Elements<ParameterDescriptor> getParameterDescriptors() {
			return getSource().getParameterDescriptors();
		}
	}

	Elements<ParameterDescriptor> getParameterDescriptors();

}
