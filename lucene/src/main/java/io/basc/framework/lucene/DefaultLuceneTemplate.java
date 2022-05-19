package io.basc.framework.lucene;

import io.basc.framework.env.Sys;
import io.basc.framework.lang.NamedThreadLocal;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.concurrent.AsyncExecutor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executor;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

public class DefaultLuceneTemplate extends AbstractLuceneTemplate {
	public static final String DIRECTORY_PREFIX = "lucene-documents";
	private ThreadLocal<DirectoryReader> localReader = new NamedThreadLocal<>(DIRECTORY_PREFIX);
	private final Directory directory;
	private final Analyzer analyzer;

	public DefaultLuceneTemplate(String... more) throws LuceneException {
		this(new StandardAnalyzer(), more);
	}

	/**
	 * @param more 注意此参数并不是一个路径，只是一个名称，使用的是workPath/{DIRECTORY_PREFIX}/{more}
	 *             下此名称的目录
	 * @see Paths#get(String, String...)
	 * @see Sys#getWorkPath()
	 * @throws LuceneException
	 */
	public DefaultLuceneTemplate(Analyzer analyzer, String... more) throws LuceneException {
		this(analyzer,
				Paths.get(Paths.get(new File(Sys.env.getWorkPath()).toPath().toString(), DIRECTORY_PREFIX).toString(),
						checkAndReturnMore(more)));
	}

	private static String[] checkAndReturnMore(String... more) {
		Assert.requiredArgument(!ArrayUtils.isEmpty(more), "more");
		return more;
	}

	public DefaultLuceneTemplate(Analyzer analyzer, Path path) throws LuceneException {
		super();
		this.analyzer = analyzer;
		try {
			this.directory = MMapDirectory.open(path);
		} catch (IOException e) {
			throw new LuceneException(path.toString(), e);
		}
	}

	public DefaultLuceneTemplate(AsyncExecutor writeExecutor, Analyzer analyzer, Path path) throws LuceneException {
		super(writeExecutor);
		this.analyzer = analyzer;
		try {
			this.directory = MMapDirectory.open(path);
		} catch (IOException e) {
			throw new LuceneException(path.toString(), e);
		}
	}

	public DefaultLuceneTemplate(Analyzer analyzer, Directory directory) {
		super();
		this.directory = directory;
		this.analyzer = analyzer;
	}

	public DefaultLuceneTemplate(AsyncExecutor writeExecutor, Analyzer analyzer, Directory directory) {
		super(writeExecutor);
		this.directory = directory;
		this.analyzer = analyzer;
	}

	public DefaultLuceneTemplate(AsyncExecutor writeExecutor, @Nullable Executor readExecutor, Analyzer analyzer,
			Directory directory) {
		super(writeExecutor, readExecutor);
		this.directory = directory;
		this.analyzer = analyzer;
	}

	public final Directory getDirectory() {
		return directory;
	}

	public final Analyzer getAnalyzer() {
		return analyzer;
	}

	@Override
	public IndexWriter getIndexWriter() throws IOException {
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		return new IndexWriter(getDirectory(), indexWriterConfig);
	}

	@Override
	protected IndexReader getIndexReader() throws IOException {
		DirectoryReader reader = localReader.get();
		if (reader == null) {
			reader = DirectoryReader.open(directory);
			localReader.set(reader);
		} else {
			DirectoryReader newReader = DirectoryReader.openIfChanged(reader);
			if (newReader != null) {
				reader.close();
				reader = newReader;
				localReader.set(reader);
			}
		}
		return reader;
	}

	@Override
	protected void closeIndexReader(IndexReader indexReader) throws IOException {
		// 不关闭
	}

	private volatile Boolean indexExists;

	public boolean indexExists() throws LuceneException {
		if (indexExists == null) {
			synchronized (this) {
				if (indexExists == null) {
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
	public <T> SearchResults<T> search(SearchParameters parameters, ScoreDocMapper<T> rowMapper)
			throws LuceneSearchException {
		if (!indexExists()) {
			return new SearchResults<>(parameters, null, rowMapper, this);
		}

		try {
			return super.search(parameters, rowMapper);
		} catch (LuceneException e) {
			indexExists = null;
			throw e;
		}

	}

	@Override
	public <T> SearchResults<T> searchAfter(ScoreDoc after, SearchParameters parameters, ScoreDocMapper<T> rowMapper)
			throws LuceneSearchException {
		if (!indexExists()) {
			return new SearchResults<>(parameters, null, rowMapper, this);
		}

		try {
			return super.searchAfter(after, parameters, rowMapper);
		} catch (LuceneException e) {
			indexExists = null;
			throw e;
		}
	}
}
