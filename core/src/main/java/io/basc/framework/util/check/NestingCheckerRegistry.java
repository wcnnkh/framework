package io.basc.framework.util.check;

import java.util.function.Predicate;

import io.basc.framework.observe.register.ElementRegistry;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Registration;
import io.basc.framework.util.Registrations;

public class NestingCheckerRegistry<E> extends ElementRegistry<NestingChecker<E>> implements NestingChecker<E> {

	@Override
	public boolean isNestingExists(E element) {
		for (NestingChecker<E> nestingChecker : getServices()) {
			if (nestingChecker.isNestingExists(element)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Registration registerNestedElement(E element) {
		return Registrations.register(getServices().iterator(), (e) -> e.registerNestedElement(element));
	}

	public Registration register(Predicate<? super E> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		return register(NestingChecker.predicate(predicate));
	}
}
