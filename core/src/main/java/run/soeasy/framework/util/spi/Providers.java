package run.soeasy.framework.util.spi;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.util.check.NestingChecker;
import run.soeasy.framework.util.check.ThreadLocalNestingChecker;
import run.soeasy.framework.util.function.Predicate;

@Getter
@Setter
public class Providers<T, E extends Throwable> extends ConfigurableServices<T> {
	@NonNull
	private NestingChecker<? super T> nestingChecker = new ThreadLocalNestingChecker<>();

	public Provider<T, E> optional() {
		return optional(Predicate.alwaysTruePredicate());
	}
	
	public Provider<T, E> optional(Predicate<? super T, ? extends E> predicate) {
		return new Provider<>(this, nestingChecker, predicate);
	}
}
