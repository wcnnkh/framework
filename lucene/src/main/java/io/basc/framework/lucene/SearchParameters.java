package io.basc.framework.lucene;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import io.basc.framework.lang.Nullable;
import lombok.Data;

@Data
public class SearchParameters implements Cloneable {
	private final Query query;
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

	@Override
	public SearchParameters clone() {
		return new SearchParameters(this);
	}

	public static SearchParameters top(Query query, int top) {
		return new SearchParameters(query, top);
	}
}
