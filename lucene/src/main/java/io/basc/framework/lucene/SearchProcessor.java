package io.basc.framework.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.function.Processor;
import lombok.Data;

@Data
public class SearchProcessor<T> implements Processor<IndexSearcher, SearchResults<T>, LuceneException> {
	private final ScoreDoc after;
	private final SearchParameters parameters;
	private final LuceneTemplate luceneTemplete;
	private final ScoreDocMapper<T> rowMapper;

	public SearchProcessor(LuceneTemplate luceneTemplete, ScoreDoc after, SearchParameters parameters,
			ScoreDocMapper<T> rowMapper) {
		this.after = after;
		this.luceneTemplete = luceneTemplete;
		this.parameters = parameters;
		this.rowMapper = rowMapper;
	}

	@Override
	public SearchResults<T> process(IndexSearcher searcher) throws LuceneException {
		TopDocs topDocs;
		List<T> rows;
		try {
			topDocs = search(searcher);
			rows = mapperRows(searcher, topDocs);
		} catch (IOException e) {
			throw new LuceneSearchException(e);
		}
		return new SearchResults<>(parameters, after, topDocs, rows, rowMapper, luceneTemplete);
	}

	protected List<T> mapperRows(IndexSearcher indexSearcher, TopDocs topDocs)
			throws IOException {
		if (ArrayUtils.isEmpty(topDocs.scoreDocs)) {
			return Collections.emptyList();
		}

		List<T> rows = new ArrayList<>(topDocs.scoreDocs.length);
		for (int i = 0; i < topDocs.scoreDocs.length; i++) {
			rows.add(rowMapper.map(indexSearcher, topDocs.scoreDocs[i]));
		}
		return rows;
	}

	protected TopDocs search(IndexSearcher indexSearcher) throws IOException {
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
