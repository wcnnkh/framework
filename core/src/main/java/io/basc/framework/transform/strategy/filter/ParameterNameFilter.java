package io.basc.framework.transform.strategy.filter;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.param.ParameterDescriptor;
import io.basc.framework.util.match.StringMatcher;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@RequiredArgsConstructor
public class ParameterNameFilter extends ParameterDescriptorFilter {
	@NonNull
	private final StringMatcher stringMatcher;
	@NonNull
	private final String namePattern;

	@Override
	public boolean test(TypeDescriptor sourceTypeDescriptor, ParameterDescriptor parameter) {
		return stringMatcher.match(namePattern, parameter.getName()) && super.test(sourceTypeDescriptor, parameter);
	}
}
