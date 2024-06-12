package io.basc.framework.execution.param;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import io.basc.framework.util.element.Elements;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@NoArgsConstructor
@Setter
public class Args implements Parameters, Serializable {
	private static final long serialVersionUID = 1L;
	@NonNull
	private Elements<Parameter> elements = Elements.empty();

	public Args(Parameter... parameters) {
		this(Arrays.asList(parameters));
	}

	public Args(Iterable<Parameter> parameters) {
		this.elements = Elements.of(parameters);
	}

	@Override
	public Elements<Parameter> getElements() {
		return elements.sorted(Comparator.comparing(Parameter::getPositionIndex));
	}
}
