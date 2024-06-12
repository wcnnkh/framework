package io.basc.framework.execution.param;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.Value;
import io.basc.framework.util.Items;
import io.basc.framework.util.element.Elements;

/**
 * 多个参数
 */
@FunctionalInterface
public interface Parameters
		extends Items<Parameter>, Function<Elements<? extends ParameterDescriptor>, ParameterMatchingResults> {

	public static Parameters empty() {
		return EmptyParameters.INSTANCE;
	}

	public static Parameters forArgs(Iterable<? extends Object> args) {
		Elements<Parameter> parameters = Elements.of(args).index()
				.map((e) -> new Arg((int) e.getIndex(), Value.of(e.getElement())));
		return new Args(parameters);
	}

	public static Parameters forValues(Iterable<? extends Value> values) {
		Elements<Parameter> parameters = Elements.of(values).index()
				.map((e) -> new Arg((int) e.getIndex(), e.getElement()));
		return new Args(parameters);
	}

	@Override
	default ParameterMatchingResults apply(Elements<? extends ParameterDescriptor> parameterDescriptors) {
		ParameterMatchingResults matchingResults = new ParameterMatchingResults();
		if (parameterDescriptors.isEmpty() && isEmpty()) {
			matchingResults.setSuccessful(true);
			return matchingResults;
		}

		List<ParameterDescriptor> parameterDescriptorList = parameterDescriptors.collect(Collectors.toList());
		List<Parameter> parameterList = getElements().collect(Collectors.toList());
		List<ParameterMatched> results = new ArrayList<ParameterMatched>();
		Iterator<ParameterDescriptor> iterator = parameterDescriptorList.iterator();
		while (iterator.hasNext()) {
			ParameterDescriptor indexed = iterator.next();
			Iterator<Parameter> paramIterator = parameterList.iterator();
			while (paramIterator.hasNext()) {
				Parameter parameter = paramIterator.next();
				if (parameter.test(indexed)) {
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
					matched.setSuccessful(indexed.isNullable());
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

	default Elements<Object> getArgs() {
		return getElements().sorted(Comparator.comparing(Parameter::getPositionIndex)).map(Parameter::getValue);
	}

	default Elements<Class<?>> getTypes() {
		return getElements().sorted(Comparator.comparing(Parameter::getPositionIndex)).map(Parameter::getTypeDescriptor)
				.map(TypeDescriptor::getType);
	}

	default boolean isEmpty() {
		return getElements().isEmpty();
	}
}
