package io.basc.framework.util.element;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * 可以被序列化的Elements
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
public abstract class SerializableElements<E> extends AbstractElements<E> implements Serializable {
	private static final long serialVersionUID = 1L;
	private volatile ArrayList<E> list;

	private void init() {
		if (list == null) {
			synchronized (this) {
				if (list == null) {
					this.list = create();
					if (this.list == null) {
						this.list = new ArrayList<>(0);
					}
				}
			}
		}
	}

	@Override
	public Elements<E> reverse() {
		if (list == null) {
			synchronized (this) {
				if (list == null) {
					return super.reverse();
				}
			}
		}

		List<E> newList = new ArrayList<>();
		Collections.reverse(newList);
		return new ElementList<>(newList);
	}

	protected abstract ArrayList<E> create();

	@Override
	public Iterator<E> iterator() {
		init();
		return list.iterator();
	}

	@Override
	public Stream<E> stream() {
		init();
		return list.stream();
	}

	private void writeObject(ObjectOutputStream output) throws IOException {
		init();
		output.writeObject(list);
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
		list = (ArrayList<E>) input.readObject();
	}
}
