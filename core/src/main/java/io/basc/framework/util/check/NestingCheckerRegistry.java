package io.basc.framework.util.check;

import java.util.function.Predicate;

import io.basc.framework.util.Assert;
import io.basc.framework.util.exchange.Registration;
import io.basc.framework.util.spi.ConfigurableServices;

public class NestingCheckerRegistry<E> extends ConfigurableServices<NestingChecker<E>> implements NestingChecker<E> {

	@Override
	public boolean isNestingExists(E element) {
		for (NestingChecker<E> nestingChecker : this) {
			if (nestingChecker.isNestingExists(element)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Registration registerNestedElement(E element) {
		return Registration.registers(this, (e) -> e.registerNestedElement(element));
	}

	public Registration register(Predicate<? super E> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		return register(NestingChecker.predicate(predicate));
	}
}
