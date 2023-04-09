package io.basc.framework.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

import io.basc.framework.event.ChangeType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(exclude = "lock")
@EqualsAndHashCode(exclude = "lock")
public class ElementRegistry<E> {
	private Object lock = this;
	private final ElementCollection<E> elements;
	private volatile long version;

	public ElementRegistry() {
		this(new CopyOnWriteArraySet<>());
	}

	public ElementRegistry(Collection<E> elements) {
		Assert.requiredArgument(elements != null, "elements");
		this.elements = new ElementCollection<>(elements);
	}

	public ElementRegistration<E> clear() {
		synchronized (lock) {
			Elements<E> elements = new ElementCollection<>(new ArrayList<>(this.elements.toList()));
			this.elements.clear();
			this.version++;
			return new Registration(elements, version, ChangeType.CREATE);
		}
	}

	/**
	 * 对此操作不会触发事件
	 * 
	 * @return
	 */
	public final ElementCollection<E> getElements() {
		return elements;
	}

	public final Object getLock() {
		return lock;
	}

	public long getVersion() {
		return version;
	}

	public final ElementRegistration<E> register(E element) {
		return registers(Arrays.asList(element));
	}

	public ElementRegistration<E> registers(Iterable<? extends E> elements) {
		Assert.requiredArgument(elements != null, "elements");
		ElementCollection<E> changes = new ElementCollection<>(new ArrayList<>(8));
		long version;
		synchronized (lock) {
			version = this.version;
			for (E element : elements) {
				if (element == null) {
					continue;
				}

				if (this.elements.add(element)) {
					changes.add(element);
				}
			}
		}
		return new Registration(changes, version, ChangeType.DELETE);
	}

	public void setLock(Object lock) {
		Assert.requiredArgument(lock != null, "lock");
		this.lock = lock;
	}

	private void unregister(long version, Elements<E> elements, ChangeType changeType) {
		if (version == this.version) {
			synchronized (lock) {
				if (version == this.version) {
					for (E element : elements) {
						if (changeType == ChangeType.DELETE) {
							this.elements.remove(element);
						} else if (changeType == ChangeType.CREATE) {
							this.elements.add(element);
						}
					}
				}
			}
		}
	}

	private class Registration extends ElementRegistration<E> {
		private final long version;
		private final ChangeType changeType;

		Registration(Elements<E> elements, long version, ChangeType changeType) {
			super(elements);
			this.version = version;
			this.changeType = changeType;
		}

		@Override
		public boolean isEmpty() {
			return super.isEmpty() || (version != ElementRegistry.this.version);
		}

		@Override
		protected void unregister(Elements<E> elements) {
			ElementRegistry.this.unregister(version, elements, changeType);
		}
	}
}
