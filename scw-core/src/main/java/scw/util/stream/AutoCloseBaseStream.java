package scw.util.stream;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.stream.BaseStream;

public interface AutoCloseBaseStream<T, S extends BaseStream<T, S>> extends BaseStream<T, S> {
	/**
	 * 此方法不会自动关闭Stream
	 */
	@Override
	Iterator<T> iterator();

	/**
	 * 此方法不会自动关闭Stream
	 */
	@Override
	Spliterator<T> spliterator();
}
