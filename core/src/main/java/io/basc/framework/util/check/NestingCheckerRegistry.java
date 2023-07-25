package io.basc.framework.util.check;

import java.util.function.Predicate;

import io.basc.framework.util.Assert;
import io.basc.framework.util.registry.DefaultElementRegistry;
import io.basc.framework.util.registry.Registration;
import io.basc.framework.util.registry.Registrations;

public class NestingCheckerRegistry<E> extends DefaultElementRegistry<NestingChecker<E>> implements NestingChecker<E> {

	@Override
	public boolean isNestingExists(E element) {
		for (NestingChecker<E> nestingChecker : getElements()) {
			if (nestingChecker.isNestingExists(element)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Registration registerNestedElement(E element) {
		return Registrations.register(getElements().iterator(), (e) -> e.registerNestedElement(element));
	}

	public Registration register(Predicate<? super E> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		return register(NestingChecker.predicate(predicate));
	}
}
