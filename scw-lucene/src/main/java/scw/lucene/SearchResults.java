package scw.lucene;

import java.util.List;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;

import scw.util.page.Pageables;

public class SearchResults<T> extends TopFieldDocs implements Pageables<ScoreDoc, T> {
	private final SearchParameters parameters;
	private final List<T> rows;
	private Sort resultSort;
	private final LuceneTemplete luceneTemplete;
	private final ScoreDocMapper<T> rowMapper;
	private final ScoreDoc cursorId;
	private ScoreDoc nextCursorId;

	public SearchResults(SearchParameters parameters, ScoreDoc cursorId, TopDocs topDocs, SortField[] fields,
			List<T> rows, ScoreDocMapper<T> rowMapper, LuceneTemplete luceneTemplete) {
		super(topDocs.totalHits, topDocs.scoreDocs, fields);
		this.cursorId = cursorId;
		this.parameters = parameters;
		this.luceneTemplete = luceneTemplete;
		this.rows = rows;
		this.rowMapper = rowMapper;
		if (hasNext()) {
			this.nextCursorId = scoreDocs[scoreDocs.length - 1];
		}
	}

	public SearchResults(SearchParameters parameters, ScoreDoc cursorId, TopDocs topDocs, List<T> rows,
			ScoreDocMapper<T> rowMapper, LuceneTemplete luceneTemplete) {
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
	public long getCount() {
		return parameters.getTop();
	}

	@Override
	public ScoreDoc getNextCursorId() {
		return nextCursorId;
	}

	@Override
	public List<T> rows() {
		return rows;
	}

	@Override
	public boolean hasNext() {
		return scoreDocs.length >= getCount();
	}

	@Override
	public SearchResults<T> process(ScoreDoc start, long count) {
		return luceneTemplete.searchAfter(start, parameters.setTop((int) count), rowMapper);
	}
}
