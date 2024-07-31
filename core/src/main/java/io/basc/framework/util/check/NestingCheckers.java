package io.basc.framework.util.check;

import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.register.Registration;

public class NestingCheckers<E> implements NestingChecker<E> {
	private final Elements<NestingChecker<E>> elements;

	public NestingCheckers(Elements<NestingChecker<E>> elements) {
		Assert.requiredArgument(elements != null, "elements");
		this.elements = elements;
	}

	public Elements<NestingChecker<E>> getElements() {
		return elements;
	}

	@Override
	public boolean isNestingExists(E element) {
		return elements.anyMatch((e) -> e.isNestingExists(element));
	}

	@Override
	public Registration registerNestedElement(E element) {
		return Registration.registers(elements, (e) -> e.registerNestedElement(element));
	}

}
