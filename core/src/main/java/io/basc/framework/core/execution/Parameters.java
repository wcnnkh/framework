package io.basc.framework.core.execution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.transform.stereotype.Property;
import io.basc.framework.util.collections.Elements;
import lombok.Data;
import lombok.NonNull;

/**
 * 多个参数的定义
 * 
 * @author shuchaowen
 *
 */
public interface Parameters
		extends ParameterTemplate<Parameter>, Comparable<Parameters>, Predicate<ParameterDescriptorTemplate> {
	public static final Parameters EMPTY_PARAMETERS = completed();

	@FunctionalInterface
	public static interface ParametersWrapper<W extends Parameters>
			extends Parameters, ParameterMappingWrapper<Parameter, W> {
		@Override
		default int compareTo(Parameters o) {
			return getSource().compareTo(o);
		}

	}

	@Data
	public static class ListParameters implements Parameters {
		@NonNull
		private List<? extends Parameter> parameterList;

		@Override
		public Parameter get(int index) {
			return parameterList == null ? null : parameterList.get(index);
		}

		@Override
		public Elements<Parameter> getElements() {
			if (parameterList == null) {
				return Elements.empty();
			}

			return Elements.of(parameterList);
		}

		@Override
		public int size() {
			return parameterList == null ? 0 : parameterList.size();
		}
	}

	public static Parameters forArgs(@NonNull Object... args) {
		Parameter[] parameters = new Parameter[args.length];
		for (int i = 0; i < parameters.length; i++) {
			parameters[i] = Parameter.of(i, null, Value.of(args[i]));
		}
		return completed(parameters);
	}

	public static Parameters forList(@NonNull List<? extends Parameter> list) {
		return new ListParameters(list);
	}

	public static Parameters forProperties(@NonNull Property... properties) {
		Parameter[] parameters = new Parameter[properties.length];
		for (int i = 0; i < parameters.length; i++) {
			parameters[i] = Parameter.of(i, properties[i]);
		}
		return completed(parameters);
	}

	public static Parameters forTemplate(@NonNull ParameterDescriptorTemplate template, @NonNull Object... args) {
		List<Parameter> list = new ArrayList<>();
		Iterator<ParameterDescriptor> iterator = template.getParameterDescriptors().iterator();
		Iterator<Object> argIterator = Arrays.asList(args).iterator();
		while (iterator.hasNext() && argIterator.hasNext()) {
			Parameter parameter = Parameter.of(iterator.next());
			parameter.set(argIterator.next());
			list.add(parameter);
		}

		while (iterator.hasNext()) {
			Parameter parameter = Parameter.of(iterator.next());
			list.add(parameter);
		}
		return Parameters.completed(list.toArray(new Parameter[0]));
	}

	/**
	 * 使用完整有效的参数构造
	 * 
	 * @param parameters
	 * @return
	 */
	public static Parameters completed(@NonNull Parameter... parameters) {
		return new EffectiveParameters(parameters);
	}

	public static class EffectiveParameters implements Parameters {
		@NonNull
		private final Parameter[] parameters;

		public EffectiveParameters(@NonNull Parameter... parameters) {
			this.parameters = parameters;
		}

		@Override
		public Parameter get(int index) {
			return parameters[index];
		}

		@Override
		public int size() {
			return parameters.length;
		}

	}

	/**
	 * 比较参数使用的优先级
	 */
	@Override
	default int compareTo(Parameters o) {
		return Integer.compare(getValidCount(), getValidCount());
	}

	@Override
	default boolean test(ParameterDescriptorTemplate template) {
		List<ParameterDescriptor> parameterDescriptorList = template.getParameterDescriptors()
				.collect(Collectors.toList());
		List<Parameter> parameterList = getElements().toList();
		List<Parameter> results = new ArrayList<>();
		Iterator<ParameterDescriptor> iterator = parameterDescriptorList.iterator();
		while (iterator.hasNext()) {
			ParameterDescriptor indexed = iterator.next();
			Iterator<Parameter> paramIterator = parameterList.iterator();
			while (paramIterator.hasNext()) {
				Parameter parameter = paramIterator.next();
				if (parameter.test(indexed)) {
					// 匹配成功，判断是否已存在，如果存在那么删除并忽略
					boolean find = false;
					for (Parameter result : results) {
						if (result.getIndex() == indexed.getIndex()) {
							paramIterator.remove();
							find = true;
							break;
						}
					}

					if (find) {
						break;
					}

					Parameter result = parameter.reconstruct(indexed);
					results.add(result);
					iterator.remove();
					paramIterator.remove();
					break;
				}
			}
		}

		if (parameterDescriptorList.isEmpty()) {
			if (!parameterList.isEmpty()) {
				// 参数不够用
				return false;
			}
		} else {
			if (!parameterList.isEmpty()) {
				// 一些参数匹配不上
				return false;
			}
		}
		return true;
	}

	/**
	 * 根据模板重新构造
	 * 
	 * @see #test(ParameterDescriptorTemplate) 和此方法是成对出现的
	 * @param template 参数模板
	 * @return
	 * @throws IllegalStateException 匹配错误，检查{@link #test(ParameterDescriptorTemplate)}方法
	 */
	default Parameters reconstruct(ParameterDescriptorTemplate template) throws IllegalStateException {
		List<ParameterDescriptor> parameterDescriptorList = template.getParameterDescriptors()
				.collect(Collectors.toList());
		List<Parameter> parameterList = getElements().toList();
		List<Parameter> results = new ArrayList<>();
		Iterator<ParameterDescriptor> iterator = parameterDescriptorList.iterator();
		while (iterator.hasNext()) {
			ParameterDescriptor indexed = iterator.next();
			Iterator<Parameter> paramIterator = parameterList.iterator();
			while (paramIterator.hasNext()) {
				Parameter parameter = paramIterator.next();
				if (parameter.test(indexed)) {
					// 匹配成功，判断是否已存在，如果存在那么删除并忽略
					boolean find = false;
					for (Parameter result : results) {
						if (result.getIndex() == indexed.getIndex()) {
							paramIterator.remove();
							find = true;
							break;
						}
					}

					if (find) {
						break;
					}

					Parameter result = parameter.reconstruct(indexed);
					results.add(result);
					iterator.remove();
					paramIterator.remove();
					break;
				}
			}
		}

		if (parameterDescriptorList.isEmpty()) {
			if (!parameterList.isEmpty()) {
				// 参数不够用
				throw new IllegalStateException("There are still parameters that cannot be matched");
			}
		} else {
			if (parameterList.isEmpty()) {
				// 半匹配，剩下用空参数补
				for (ParameterDescriptor parameterDescriptor : parameterDescriptorList) {
					Parameter result = Parameter.of(parameterDescriptor);
					results.add(result);
				}
			} else {
				// 一些参数匹配不上
				throw new IllegalStateException("Parameter mismatch");
			}
		}

		// 全匹配
		results.sort((o1, o2) -> Integer.compare(o1.getIndex(), o2.getIndex()));
		return completed(results.toArray(new Parameter[0]));
	}

}
