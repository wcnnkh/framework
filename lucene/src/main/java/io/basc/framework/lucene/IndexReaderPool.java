package io.basc.framework.lucene;

import java.io.IOException;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;

import io.basc.framework.lang.NamedThreadLocal;

public class IndexReaderPool extends LucenePool<IndexReader> {
	private ThreadLocal<DirectoryReader> localReader = new NamedThreadLocal<>(IndexReaderPool.class.getName());

	public IndexReaderPool(Directory directory) {
		super(directory);
	}

	@Override
	public IndexReader get() {
		DirectoryReader reader = localReader.get();
		if (reader != null) {
			DirectoryReader newReader;
			try {
				newReader = DirectoryReader.openIfChanged(reader);
			} catch (IOException e) {
				throw new LuceneException(e);
			}

			if (newReader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new LuceneException(e);
				}
				reader = newReader;
				localReader.set(reader);
			}
			return reader;
		}

		try {
			reader = DirectoryReader.open(getDirectory());
		} catch (IOException e) {
			throw new LuceneException(e);
		}
		localReader.set(reader);
		return reader;
	}

	@Override
	public void release(IndexReader resource) {
		IndexReader reader = localReader.get();
		if (reader == resource) {
			return;
		}

		if (resource != null) {
			try {
				resource.close();
			} catch (IOException e) {
				throw new LuceneException(e);
			}
		}
	}

}
