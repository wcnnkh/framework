package io.basc.framework.util.check;

import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.registry.Registration;
import io.basc.framework.util.registry.Registrations;

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
		return Registrations.register(elements.iterator(), (e) -> e.registerNestedElement(element));
	}

}
