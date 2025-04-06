package run.soeasy.framework.util.check;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.Supplier;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.util.ObjectUtils;
import run.soeasy.framework.util.exchange.Registration;
import run.soeasy.framework.util.register.DisposableRegistration;

@RequiredArgsConstructor
public class ThreadLocalNestingChecker<E> extends ThreadLocal<Collection<E>> implements NestingChecker<E> {
	@NonNull
	private final Supplier<? extends Collection<E>> supplier;

	public ThreadLocalNestingChecker() {
		this(HashSet::new);
	}

	@Override
	public boolean isNestingExists(E element) {
		Collection<E> collection = get();
		return collection == null ? false : collection.contains(element);
	}
	
	@Override
	public Registration registerNestedElement(E element) {
		if (element == null) {
			return Registration.FAILURE;
		}

		Collection<E> collection = get();
		if (collection == null) {
			collection = supplier.get();
			set(collection);
		}
		
		if(collection.add(element)) {
			return new DisposableRegistration(() -> collection.remove(element));
		}
		return Registration.FAILURE;
	}
}
