package io.basc.framework.execution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.Items;
import io.basc.framework.util.element.Elements;
import io.basc.framework.value.ParameterDescriptor;
import io.basc.framework.value.Value;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * 多个参数
 */
@NoArgsConstructor
@Setter
public class Parameters implements Items<Parameter>,
		Function<Elements<? extends ParameterDescriptor>, ParameterMatchingResults>, Serializable {
	private static final long serialVersionUID = 1L;
	@NonNull
	private Elements<Parameter> elements = Elements.empty();

	public Parameters(Parameter... parameters) {
		this(Arrays.asList(parameters));
	}

	public Parameters(Iterable<Parameter> parameters) {
		this.elements = Elements.of(parameters);
	}

	public Elements<Class<?>> getTypes() {
		return elements == null ? Elements.empty()
				: elements.sorted(Comparator.comparing(Parameter::getPositionIndex)).map(Parameter::getTypeDescriptor)
						.map(TypeDescriptor::getType);
	}

	public Elements<Object> getArgs() {
		return elements == null ? Elements.empty()
				: elements.sorted(Comparator.comparing(Parameter::getPositionIndex)).map(Parameter::getValue);
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

		List<ParameterDescriptor> parameterDescriptorList = parameterDescriptors.collect(Collectors.toList());
		List<Parameter> parameterList = elements.collect(Collectors.toList());
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

	public static Parameters forArgs(Iterable<? extends Object> args) {
		Elements<Parameter> parameters = Elements.of(args).index()
				.map((e) -> new Parameter((int) e.getIndex(), e.getElement())).toList();
		return new Parameters(parameters);
	}

	public static Parameters forValues(Iterable<? extends Value> values) {
		Elements<Parameter> parameters = Elements.of(values).index()
				.map((e) -> new Parameter((int) e.getIndex(), null, e.getElement())).toList();
		return new Parameters(parameters);
	}

	@Override
	public Elements<Parameter> getElements() {
		return elements.sorted(Comparator.comparing(Parameter::getPositionIndex));
	}
}
