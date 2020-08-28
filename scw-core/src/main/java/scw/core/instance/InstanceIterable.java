package scw.core.instance;

import java.util.Iterator;
import java.util.NoSuchElementException;

import scw.util.AbstractIterator;

public final class InstanceIterable<E> implements Iterable<E> {
	private NoArgsInstanceFactory instanceFactory;
	private Iterable<String> names;

	public InstanceIterable(NoArgsInstanceFactory instanceFactory, Iterable<String> names) {
		this.instanceFactory = instanceFactory;
		this.names = names;
	}

	public Iterator<E> iterator() {
		return new InstanceIterator();
	}

	private final class InstanceIterator extends AbstractIterator<E> {
		private Iterator<String> iterator = names == null? null: names.iterator();
		private String name;

		public boolean hasNext() {
			if(iterator == null){
				return false;
			}
			
			if (name != null) {
				return true;
			}

			while (iterator.hasNext()) {
				name = iterator.next();
				if (instanceFactory.isInstance(name)) {
					return true;
				}
				name = null;
			}
			return false;
		}

		public E next() {
			if(!hasNext()){
				throw new NoSuchElementException();
			}

			E instance = instanceFactory.getInstance(name);
			name = null;
			return instance;
		}
	}
}
