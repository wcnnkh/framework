package io.basc.framework.lucene;

import java.io.IOException;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.Directory;

import io.basc.framework.util.Pipeline;
import io.basc.framework.util.Pool;

public abstract class LucenePool<T> implements Pool<T> {
	private final Directory directory;

	public LucenePool(Directory directory) {
		this.directory = directory;
	}

	public Directory getDirectory() {
		return directory;
	}

	private volatile boolean indexExists;

	public boolean indexExists() throws LuceneException {
		if (!indexExists) {
			synchronized (this) {
				if (!indexExists) {
					try {
						indexExists = DirectoryReader.indexExists(directory);
					} catch (IOException e) {
						throw new LuceneException(e);
					}
				}
			}
		}
		return indexExists;
	}

	@Override
	public <V, E extends Throwable> V process(Pipeline<? super T, ? extends V, ? extends E> processor) throws E {
		try {
			return Pool.super.process(processor);
		} catch (Throwable e) {
			indexExists = false;
			throw e;
		}
	}
}
