package io.basc.framework.execution.config;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Executable;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefaultExecutable implements Executable {
	@NonNull
	private volatile TypeDescriptor source;
	@NonNull
	private volatile String name;
	@NonNull
	private volatile TypeDescriptor returnTypeDescriptor;
	@NonNull
	private volatile Elements<? extends ParameterDescriptor> parameterDescriptors;

	public DefaultExecutable(DefaultExecutable defaultExecutable) {
		Assert.requiredArgument(defaultExecutable != null, "defaultExecutable");
		this.source = defaultExecutable.source;
		this.name = defaultExecutable.name;
		this.returnTypeDescriptor = defaultExecutable.returnTypeDescriptor;
		this.parameterDescriptors = defaultExecutable.parameterDescriptors;
	}
}
