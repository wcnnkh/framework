package scw.lucene;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import scw.core.utils.ClassUtils;
import scw.mapper.FieldDescriptor;
import scw.value.Value;

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
	protected Field toField(FieldDescriptor fieldDescriptor, Value value) {
		if (ClassUtils.isLong(fieldDescriptor.getType()) || ClassUtils.isInt(fieldDescriptor.getType())
				|| ClassUtils.isShort(fieldDescriptor.getType())) {
			return new NumericDocValuesField(fieldDescriptor.getName(), value.getAsLong());
		}

		scw.lucene.annotation.Field annotation = fieldDescriptor.getAnnotation(scw.lucene.annotation.Field.class);
		if (annotation == null) {
			return new StringField(fieldDescriptor.getName(), value.getAsString(), Store.YES);
		}
		if (annotation.indexed()) {
			if (annotation.tokenized()) {
				return new TextField(fieldDescriptor.getName(), value.getAsString(),
						annotation.stored() ? Store.YES : Store.NO);
			} else {
				return new StringField(fieldDescriptor.getName(), value.getAsString(),
						annotation.stored() ? Store.YES : Store.NO);
			}
		} else if (annotation.stored()) {
			return new StoredField(fieldDescriptor.getName(), value.getAsString());
		}
		return null;
	}

	@Override
	protected IndexReader getIndexReader() throws IOException {
		return DirectoryReader.open(directory);
	}
}
