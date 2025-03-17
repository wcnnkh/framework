package run.soeasy.framework.util.collections;

import java.util.Iterator;

public interface CloseableIterator<E> extends AutoCloseable, Iterator<E> {

	@Override
	void close();
}
