package io.basc.framework.core.execution.stractegy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.convert.transform.Parameter;
import io.basc.framework.core.convert.transform.ParameterDescriptor;
import io.basc.framework.core.convert.transform.Parameters;
import io.basc.framework.core.execution.Executable;
import io.basc.framework.util.Elements;

public class DefaultExecutionStrategy implements ExecutionStrategy {

	@Override
	public boolean match(Executable executable, Parameters parameters) {
		// TODO Auto-generated method stub
		return false;
	}

	private ValueDescriptorAnalysis executionStrategy;

	protected boolean isPresent(Parameter parameter) {
		return parameter != null && parameter.get() != null;
	}

	protected boolean test(ParameterDescriptor parameterDescriptor, Parameter parameter) {
		if (parameter == null) {
			return false;
		}

		if ((parameter.getIndex() != -1 && parameter.getIndex() == parameterDescriptor.getIndex())
				|| parameterDescriptor.getName().equals(parameter.getName())) {
			ResolvableType type1 = parameterDescriptor.getTypeDescriptor().getResolvableType();
			ResolvableType type2 = parameter.getTypeDescriptor().getResolvableType();
			if (type2.isAssignableFrom(type1)) {
				if (executionStrategy.isRequired(parameterDescriptor) && isPresent(parameter)) {
					return false;
				}
				return true;
			}
		}
		return false;
	}

	private ParameterMatchingResults test(Elements<? extends ParameterDescriptor> parameterDescriptors,
			Parameters parameters) {
		ParameterMatchingResults matchingResults = new ParameterMatchingResults();
		if (parameterDescriptors.isEmpty() && isEmpty()) {
			matchingResults.setSuccessful(true);
			return matchingResults;
		}

		List<ParameterDescriptor> parameterDescriptorList = parameterDescriptors.collect(Collectors.toList());
		List<Parameter> parameterList = parameters.getElements().toList();
		List<ParameterMatched> results = new ArrayList<ParameterMatched>();
		Iterator<ParameterDescriptor> iterator = parameterDescriptorList.iterator();
		while (iterator.hasNext()) {
			ParameterDescriptor indexed = iterator.next();
			Iterator<Parameter> paramIterator = parameterList.iterator();
			while (paramIterator.hasNext()) {
				Parameter parameter = paramIterator.next();
				if (test(indexed, parameter)) {
					// 匹配成功
					ParameterMatched matched = new ParameterMatched();
					matched.setParameter(parameter);
					matched.setParameterDescriptor(parameter);
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
				for (ParameterDescriptor indexed : parameterDescriptorList) {
					ParameterMatched matched = new ParameterMatched();
					matched.setParameter(null);
					matched.setParameterDescriptor(indexed);
					matched.setSuccessful(!executionStrategy.isRequired(indexed));
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
