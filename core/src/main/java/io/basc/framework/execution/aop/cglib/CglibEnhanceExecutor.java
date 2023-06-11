package io.basc.framework.execution.aop.cglib;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Executable;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.sf.cglib.proxy.Enhancer;

@RequiredArgsConstructor
@Data
public class CglibEnhanceExecutor implements Executable {
	private final TypeDescriptor typeDescriptor;
	private final Enhancer enhancer;
	private final Elements<? extends ParameterDescriptor> parameterDescriptors;

	@Override
	public String getName() {
		return typeDescriptor.getName();
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return typeDescriptor;
	}

	@Override
	public Object execute(Elements<? extends Object> args) {
		return enhancer.create(parameterDescriptors.map((e) -> e.getTypeDescriptor().getType()).toArray(new Class[0]),
				args.toArray());
	}

}
