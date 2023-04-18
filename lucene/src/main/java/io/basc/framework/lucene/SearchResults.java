package io.basc.framework.lucene;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.TotalHits;
import org.apache.lucene.search.TotalHits.Relation;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.page.Pages;

public class SearchResults<T> extends TopFieldDocs implements Pages<ScoreDoc, T> {
	private final SearchParameters parameters;
	private final List<T> list;
	private Sort resultSort;
	private final LuceneTemplate luceneTemplete;
	private final ScoreDocMapper<T> rowMapper;
	private final ScoreDoc cursorId;
	private ScoreDoc nextCursorId;

	public SearchResults(SearchParameters parameters, ScoreDoc cursorId, ScoreDocMapper<T> rowMapper,
			LuceneTemplate luceneTemplete) {
		this(parameters, cursorId, new TopDocs(new TotalHits(0, Relation.EQUAL_TO), new ScoreDoc[0]),
				Collections.emptyList(), rowMapper, luceneTemplete);
	}

	public SearchResults(SearchParameters parameters, ScoreDoc cursorId, TopDocs topDocs, SortField[] fields,
			List<T> list, ScoreDocMapper<T> rowMapper, LuceneTemplate luceneTemplete) {
		super(topDocs.totalHits, topDocs.scoreDocs, fields);
		this.cursorId = cursorId;
		this.parameters = parameters;
		this.luceneTemplete = luceneTemplete;
		this.list = list;
		this.rowMapper = rowMapper;
		if (hasNext()) {
			this.nextCursorId = scoreDocs[scoreDocs.length - 1];
		}
	}

	public SearchResults(SearchParameters parameters, ScoreDoc cursorId, TopDocs topDocs, List<T> rows,
			ScoreDocMapper<T> rowMapper, LuceneTemplate luceneTemplete) {
		this(parameters, cursorId, topDocs, topDocs instanceof TopFieldDocs ? ((TopFieldDocs) topDocs).fields : null,
				rows, rowMapper, luceneTemplete);
	}

	public Sort getResultSort() {
		return resultSort;
	}

	public long getTotal() {
		return totalHits.value;
	}

	public SearchParameters getParameters() {
		return parameters;
	}

	@Override
	public ScoreDoc getCursorId() {
		return cursorId;
	}

	@Override
	public long getLimit() {
		return parameters.getTop();
	}

	@Override
	public ScoreDoc getNextCursorId() {
		return nextCursorId;
	}

	@Override
	public Elements<T> getElements() {
		return Elements.of(list);
	}

	@Override
	public boolean hasNext() {
		return scoreDocs.length >= getLimit();
	}

	@Override
	public SearchResults<T> jumpTo(ScoreDoc cursorId) {
		return luceneTemplete.searchAfter(cursorId, parameters, rowMapper);
	}

	@Override
	public SearchResults<T> next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return jumpTo(getNextCursorId());
	}

	@Override
	public String toString() {
		return ReflectionUtils.toString(this);
	}

	@Override
	public Pages<ScoreDoc, T> jumpTo(ScoreDoc cursorId, long count) {
		return luceneTemplete.searchAfter(cursorId, parameters.setTop((int) count), rowMapper);
	}
}
