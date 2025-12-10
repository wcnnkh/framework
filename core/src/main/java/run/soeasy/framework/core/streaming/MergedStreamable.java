package run.soeasy.framework.core.streaming;

import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MergedStreamable<E> implements Streamable<E> {
	@NonNull
	private final Streamable<? extends E> left;
	@NonNull
	private final Streamable<? extends E> right;

	@Override
	public Stream<E> stream() {
		return Stream.concat(left.stream(), right.stream());
	}
	
	@Override
	public long count() {
		return Math.addExact(left.count(), right.count());
	}
	
	@Override
	public boolean isEmpty() {
		return left.isEmpty() && right.isEmpty();
	}
	
	@Override
	public Streamable<E> reload() {
		return new MergedStreamable<>(left.reload(), right.reload());
	}

	@Override
	public boolean anyMatch(@NonNull java.util.function.Predicate<? super E> predicate) {
		return left.anyMatch(predicate) || right.anyMatch(predicate);
	}

	@Override
	public boolean allMatch(@NonNull java.util.function.Predicate<? super E> predicate) {
		return left.allMatch(predicate) && right.allMatch(predicate);
	}

	@Override
	public boolean noneMatch(@NonNull java.util.function.Predicate<? super E> predicate) {
		return left.noneMatch(predicate) && right.noneMatch(predicate);
	}

	@Override
	public boolean contains(Object element) {
		return left.contains(element) || right.contains(element);
	}

	@Override
	public Streamable<E> parallel() {
		return new MergedStreamable<>(left.parallel(), right.parallel());
	}

	@Override
	public Streamable<E> sequential() {
		return new MergedStreamable<>(left.sequential(), right.sequential());
	}

	@Override
	public Streamable<E> onClose(@NonNull Runnable closeHandler) {
		return new MergedStreamable<>(left.onClose(closeHandler), right.onClose(closeHandler));
	}
}