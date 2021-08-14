package scw.lucene;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import scw.lang.Nullable;

public class SearchParameters implements Cloneable {
	private Query query;
	private int top;
	@Nullable
	private Sort sort;
	private boolean doDocScores = false;

	public SearchParameters(Query query, int top) {
		this.query = query;
		this.top = top;
	}

	private SearchParameters(SearchParameters parameters) {
		this.query = parameters.query;
		this.top = parameters.top;
		this.sort = parameters.sort;
		this.doDocScores = parameters.doDocScores;
	}

	public Query getQuery() {
		return query;
	}

	public SearchParameters setQuery(Query query) {
		SearchParameters parameters = new SearchParameters(this);
		parameters.query = query;
		return parameters;
	}

	public int getTop() {
		return top;
	}

	public SearchParameters setTop(int top) {
		SearchParameters parameters = new SearchParameters(this);
		parameters.top = top;
		return parameters;
	}

	public Sort getSort() {
		return sort;
	}

	public SearchParameters setSort(Sort sort) {
		SearchParameters parameters = new SearchParameters(this);
		parameters.sort = sort;
		return parameters;
	}

	public boolean isDoDocScores() {
		return doDocScores;
	}

	public SearchParameters setDoDocScores(boolean doDocScores) {
		SearchParameters parameters = new SearchParameters(this);
		parameters.doDocScores = doDocScores;
		return parameters;
	}

	@Override
	public SearchParameters clone() {
		return new SearchParameters(this);
	}
}
