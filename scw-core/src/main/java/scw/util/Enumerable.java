package scw.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

import scw.core.instance.NoArgsInstanceFactory;

public interface Enumerable<E> {
	Enumeration<E> enumeration();

	public static final class CollectionEnumerable<E> implements Enumerable<E> {
		private Collection<E> collection;

		public CollectionEnumerable(Collection<E> collection) {
			this.collection = collection;
		}

		public Enumeration<E> enumeration() {
			if(collection == null){
				return Collections.emptyEnumeration();
			}
			
			return Collections.enumeration(collection);
		}
	}

	public static final class InstanceNamesEnumerable<E> implements
			Enumerable<E> {
		private NoArgsInstanceFactory instanceFactory;
		private Collection<String> names;

		public InstanceNamesEnumerable(NoArgsInstanceFactory instanceFactory,
				Collection<String> names) {
			this.instanceFactory = instanceFactory;
			this.names = names;
		}

		public Enumeration<E> enumeration() {
			return new InstanceNamesEnumeration<E>(instanceFactory, names);
		}
	}

	static final class InstanceNamesEnumeration<E> implements Enumeration<E> {
		private final NoArgsInstanceFactory instanceFactory;
		private final Iterator<String> iterator;
		private String name;
		private boolean next = false;

		InstanceNamesEnumeration(NoArgsInstanceFactory instanceFactory,
				Collection<String> filterNames) {
			this.instanceFactory = instanceFactory;
			this.iterator = filterNames == null ? null : filterNames.iterator();
		}

		public boolean hasMoreElements() {
			if (next) {
				return true;
			}

			if (iterator == null || !iterator.hasNext()) {
				return false;
			}

			name = iterator.next();
			if (instanceFactory.isInstance(name)) {
				next = true;
				return true;
			}

			return false;
		}

		public E nextElement() {
			if (!next) {
				throw new NoSuchElementException();
			}

			next = false;
			return instanceFactory.getInstance(name);
		}
	}
}
