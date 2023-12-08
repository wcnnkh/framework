package io.basc.framework.execution.param;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.element.Indexed;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * 多个参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Parameters implements Function<Elements<? extends ParameterDescriptor>, ParameterMatchingResults> {
	@NonNull
	private Elements<Parameter> elements = Elements.empty();

	public Parameters(Parameter... parameters) {
		this(Elements.forArray(parameters));
	}

	public Elements<Object> getArgs() {
		return elements == null ? Elements.empty() : elements.map(Parameter::getValue);
	}

	public boolean isEmpty() {
		return elements == null || elements.isEmpty();
	}

	@Override
	public ParameterMatchingResults apply(Elements<? extends ParameterDescriptor> parameterDescriptors) {
		ParameterMatchingResults matchingResults = new ParameterMatchingResults();
		if (parameterDescriptors.isEmpty() && isEmpty()) {
			matchingResults.setSuccessful(true);
			return matchingResults;
		}

		List<Indexed<? extends ParameterDescriptor>> parameterDescriptorList = parameterDescriptors.index()
				.collect(Collectors.toList());
		List<Parameter> parameterList = elements.collect(Collectors.toList());
		List<ParameterMatched> results = new ArrayList<ParameterMatched>();
		Iterator<Indexed<? extends ParameterDescriptor>> iterator = parameterDescriptorList.iterator();
		while (iterator.hasNext()) {
			Indexed<? extends ParameterDescriptor> indexed = iterator.next();
			Iterator<Parameter> paramIterator = parameterList.iterator();
			while (paramIterator.hasNext()) {
				Parameter parameter = paramIterator.next();
				if (parameter.test(indexed)) {
					// 匹配成功
					ParameterMatched matched = new ParameterMatched();
					matched.setParameter(parameter);
					matched.setParameterDescriptor(indexed.getElement());
					matched.setSuccessful(true);
					results.add(matched);
					iterator.remove();
					paramIterator.remove();
					break;
				}
			}
		}

		if (parameterDescriptorList.isEmpty()) {
			if (parameterList.isEmpty()) {
				matchingResults.setSuccessful(true);
			} else {
				// 匹配成功 半匹配
				for (Parameter parameter : parameterList) {
					ParameterMatched matched = new ParameterMatched();
					matched.setParameter(parameter);
					matched.setParameterDescriptor(null);
					matched.setSuccessful(true);
					results.add(matched);
				}
				matchingResults.setSuccessful(true);
			}
		} else {
			if (parameterList.isEmpty()) {
				// 补充空数据
				for (Indexed<? extends ParameterDescriptor> indexed : parameterDescriptorList) {
					ParameterMatched matched = new ParameterMatched();
					matched.setParameter(null);
					matched.setParameterDescriptor(indexed.getElement());
					matched.setSuccessful(indexed.getElement().isNullable());
					results.add(matched);
				}

				matchingResults
						.setSuccessful(results.stream().filter((e) -> e.exists()).allMatch((e) -> e.isSuccessful()));
			} else {
				// 无法匹配
				matchingResults.setSuccessful(false);
			}
		}
		matchingResults.setElements(Elements.of(results));
		return matchingResults;
	}
}
