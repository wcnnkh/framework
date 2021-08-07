package scw.lucene;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

public class DefaultLuceneTemplete extends AbstractLuceneTemplete {
	private final Directory directory;
	private final Analyzer analyzer;
	
	public DefaultLuceneTemplete(String directory) throws IOException {
		this(directory, new StandardAnalyzer());
	}
	
	public DefaultLuceneTemplete(String directory, Analyzer analyzer) throws IOException {
		this(MMapDirectory.open(Paths.get(directory)), new StandardAnalyzer());
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
}
