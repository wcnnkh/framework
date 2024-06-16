package io.basc.framework.execution.param;

import io.basc.framework.util.element.Elements;
import lombok.Data;

@Data
public class SimpleParameters implements Parameters {
	private Elements<? extends Parameter> elements;

	@Override
	public Elements<Parameter> getElements() {
		return elements == null ? Elements.empty() : elements.map((e) -> e);
	}
}
