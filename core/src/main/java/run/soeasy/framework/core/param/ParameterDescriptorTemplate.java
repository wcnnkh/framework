package run.soeasy.framework.core.param;

import run.soeasy.framework.core.Wrapper;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.transform.mapping.ParameterDescriptor;

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
