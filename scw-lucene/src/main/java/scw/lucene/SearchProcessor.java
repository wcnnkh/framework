package scw.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import scw.core.utils.ArrayUtils;
import scw.util.stream.Processor;
import scw.util.task.support.TaskExecutors;

public class SearchProcessor<T> implements Processor<IndexReader, SearchResults<T>, LuceneException> {
	protected final ScoreDoc after;
	protected final SearchParameters parameters;
	protected final LuceneTemplete luceneTemplete;
	protected final ScoreDocMapper<T> rowMapper;

	public SearchProcessor(LuceneTemplete luceneTemplete, ScoreDoc after, SearchParameters parameters,
			ScoreDocMapper<T> rowMapper) {
		this.after = after;
		this.luceneTemplete = luceneTemplete;
		this.parameters = parameters;
		this.rowMapper = rowMapper;
	}

	@Override
	public SearchResults<T> process(IndexReader reader) throws LuceneException {
		IndexSearcher indexSearcher = new IndexSearcher(reader, TaskExecutors.getGlobalExecutor());
		TopDocs topDocs;
		List<T> rows;
		try {
			topDocs = search(reader, indexSearcher);
			rows = mapperRows(reader, indexSearcher, topDocs);
		} catch (IOException e) {
			throw new LuceneSearchException(e);
		}
		return new SearchResults<>(parameters, after, topDocs, rows, rowMapper, luceneTemplete);
	}

	protected List<T> mapperRows(IndexReader indexReader, IndexSearcher indexSearcher, TopDocs topDocs)
			throws IOException {
		if (ArrayUtils.isEmpty(topDocs.scoreDocs)) {
			return Collections.emptyList();
		}

		List<T> rows = new ArrayList<>(topDocs.scoreDocs.length);
		for (int i = 0; i < topDocs.scoreDocs.length; i++) {
			rows.add(rowMapper.map(indexReader, indexSearcher, topDocs.scoreDocs[i]));
		}
		return rows;
	}

	protected TopDocs search(IndexReader indexReader, IndexSearcher indexSearcher) throws IOException {
		if (after == null) {
			if (parameters.getSort() == null) {
				return indexSearcher.search(parameters.getQuery(), parameters.getTop());
			} else {
				return indexSearcher.search(parameters.getQuery(), parameters.getTop(), parameters.getSort(),
						parameters.isDoDocScores());
			}
		} else {
			if (parameters.getSort() == null) {
				return indexSearcher.searchAfter(after, parameters.getQuery(), parameters.getTop());
			} else {
				return indexSearcher.searchAfter(after, parameters.getQuery(), parameters.getTop(),
						parameters.getSort(), parameters.isDoDocScores());
			}
		}
	}
}
