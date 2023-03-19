package io.basc.framework.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public final class SharedResultSet<E> implements ResultSet<E>, Serializable {
	private static final long serialVersionUID = 1L;
	private volatile List<E> list;
	private volatile int version;

	public SharedResultSet() {
	}

	public SharedResultSet(Collection<? extends E> list) {
		this.list = new ArrayList<>(list);
	}

	public Registration register(E element) {
		if (list == null) {
			synchronized (this) {
				if (list == null) {
					list = new ArrayList<>();
				}
			}
		}

		synchronized (this) {
			if (list.contains(element)) {
				return Registration.EMPTY;
			}

			list.add(element);
			return new ElementRegistion(element, this.version);
		}
	}

	private void unregister(E element, int version) {
		if (list != null && version == this.version) {
			synchronized (this) {
				if (list != null && version == this.version) {
					list.remove(element);
				}
			}
		}
	}

	private class ElementRegistion implements Registration {
		private final E element;
		private int version;
		private AtomicBoolean closed = new AtomicBoolean();

		ElementRegistion(E element, int version) {
			this.element = element;
			this.version = version;
		}

		@Override
		public void unregister() {
			if (closed.compareAndSet(false, true)) {
				SharedResultSet.this.unregister(element, version);
			}
		}

		@Override
		public boolean isEmpty() {
			return closed.get() || this.version != SharedResultSet.this.version;
		}
	}

	public void clear() {
		if (list != null) {
			synchronized (this) {
				if (list != null) {
					list.clear();
					this.version++;
				}
			}
		}
	}

	@Override
	public Cursor<E> iterator() {
		return Cursor.of(list);
	}

	@Override
	public <T, X extends Throwable> T export(Processor<? super Stream<E>, ? extends T, ? extends X> processor)
			throws X {
		if (!isEmpty()) {
			synchronized (this) {
				if (!isEmpty()) {
					return ResultSet.super.export(processor);
				}
			}
		}
		return processor.process(XUtils.emptyStream());
	}

	public boolean isEmpty() {
		return CollectionUtils.isEmpty(list);
	}

	@Override
	public <X extends Throwable> void transfer(ConsumeProcessor<? super Stream<E>, ? extends X> processor) throws X {
		if (!isEmpty()) {
			synchronized (this) {
				if (!isEmpty()) {
					ResultSet.super.transfer(processor);
					return;
				}
			}
		}
		processor.process(XUtils.emptyStream());
	}
}
