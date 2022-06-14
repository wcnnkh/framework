package io.basc.framework.lucene;

import java.io.IOException;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;

import io.basc.framework.lang.NamedThreadLocal;

public class IndexWriterPool extends LucenePool<IndexWriter> {
	private final ThreadLocal<IndexWriter> local = new NamedThreadLocal<IndexWriter>(IndexWriterPool.class.getName());
	private final IndexWriterConfig config;

	public IndexWriterPool(Directory directory, IndexWriterConfig config) {
		super(directory);
		this.config = config;
	}

	@Override
	public IndexWriter get() {
		IndexWriter writer = local.get();
		if (writer != null && writer.isOpen()) {
			return writer;
		}

		try {
			writer = new IndexWriter(getDirectory(), this.config);
		} catch (IOException e) {
			throw new LuceneException(e);
		}
		local.set(writer);
		return writer;
	}

	@Override
	public void release(IndexWriter resource) {
		IndexWriter writer = local.get();
		if (writer == resource) {
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
