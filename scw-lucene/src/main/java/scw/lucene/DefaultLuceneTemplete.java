package scw.lucene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import scw.core.Assert;
import scw.core.utils.ArrayUtils;
import scw.env.Sys;

public class DefaultLuceneTemplete extends AbstractLuceneTemplete {
	public static final String DIRECTORY_PREFIX = "lucene-documents";
	private final Directory directory;
	private final Analyzer analyzer;
	
	/**
	 * @param more 注意此参数并不是一个路径，只是一个名称，使用的是workPath下此名称的目录
	 * @see Paths#get(String, String...)
	 * @see Sys#getWorkPath()
	 * @throws LuceneException
	 */
	public DefaultLuceneTemplete(String ...more) throws LuceneException{
		this(Paths.get(Paths.get(new File(Sys.env.getWorkPath()).toPath().toString(), DIRECTORY_PREFIX).toString(), checkAndReturnMore(more)));
	}
	
	private static String[] checkAndReturnMore(String ...more) {
		Assert.requiredArgument(!ArrayUtils.isEmpty(more), "more");
		return more;
	}
	
	public DefaultLuceneTemplete(Path directory) throws LuceneException {
		this(directory, new StandardAnalyzer());
	}
	
	public DefaultLuceneTemplete(Path directory, Analyzer analyzer) throws LuceneException {
		try {
			this.directory = MMapDirectory.open(directory);
		} catch (IOException e) {
			throw new LuceneException(e);
		}
		this.analyzer = analyzer;
	}
	
	public DefaultLuceneTemplete(Directory directory, Analyzer analyzer) {
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
	public IndexWriter getIndexWrite() throws IOException {
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		return new IndexWriter(getDirectory(), indexWriterConfig);
	}

	@Override
	protected IndexReader getIndexReader() throws IOException {
		return DirectoryReader.open(directory);
	}

	@Override
	public boolean indexExists() throws LuceneException{
		try {
			return DirectoryReader.indexExists(directory);
		} catch (IOException e) {
			throw new LuceneException(e);
		}
	}
}
