package io.basc.framework.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.element.Indexed;
import lombok.Data;
import lombok.NonNull;

/**
 * 多个参数
 */
@Data
public class Parameters
		implements Function<Elements<? extends ParameterDescriptor>, Elements<ParameterMatchingResult>> {
	@NonNull
	private Elements<Parameter> elements = Elements.empty();

	@Override
	public Elements<ParameterMatchingResult> apply(Elements<? extends ParameterDescriptor> parameterDescriptors) {
		if (parameterDescriptors.isEmpty() && elements.isEmpty()) {
			return Elements.empty();
		}

		List<Indexed<? extends ParameterDescriptor>> parameterDescriptorList = parameterDescriptors.index()
				.collect(Collectors.toList());
		List<Parameter> parameterList = elements.collect(Collectors.toList());
		List<ParameterMatchingResult> results = new ArrayList<ParameterMatchingResult>();
		for(Indexed<? extends ParameterDescriptor> indexed : parameterDescriptors.index()) {
			
		}

		ParameterDescriptor[] parameterDescriptorArray = parameterDescriptors.toArray(ParameterDescriptor[]::new);
		Parameter[] parameterArray = elements.toArray(Parameter[]::new);
		int matchCount = 0;
		for (Parameter parameter : parameterArray) {

		}

		return Elements.empty();
	}
}
