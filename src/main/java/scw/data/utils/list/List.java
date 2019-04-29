package scw.data.utils.list;

public interface List<E> {
	int size();

	E get(int index);

	boolean remove(int index);

	void add(E e);
}
