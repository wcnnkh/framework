package io.basc.framework.orm.repository;

import java.util.Arrays;
import java.util.function.Predicate;

import io.basc.framework.util.Keywords;
import io.basc.framework.util.Pair;

public class OperationKeywords implements Predicate<String> {
	private static final Keywords QUERY_KEYWORDS = new Keywords(Keywords.ORIGINAL, "select", "query", "find");
	private static final Keywords UPDATE_KEYWORDS = new Keywords(Keywords.ORIGINAL, "update");
	private static final Keywords DELETE_KEYWORDS = new Keywords(Keywords.ORIGINAL, "delete", "del");
	private static final Keywords SAVE_KEYWORDS = new Keywords(Keywords.ORIGINAL, "save");

	private final Keywords queryKeywords = new Keywords(QUERY_KEYWORDS, Keywords.ORIGINAL).setPredicate(this);
	private final Keywords updateKeywords = new Keywords(UPDATE_KEYWORDS, Keywords.ORIGINAL).setPredicate(this);
	private final Keywords deleteKeywords = new Keywords(DELETE_KEYWORDS, Keywords.ORIGINAL).setPredicate(this);
	private final Keywords saveKeywords = new Keywords(SAVE_KEYWORDS, Keywords.ORIGINAL).setPredicate(this);

	@Override
	public boolean test(String key) {
		return queryKeywords.test(key) && updateKeywords.test(key) && deleteKeywords.test(key)
				&& saveKeywords.test(key);
	}

	public Keywords getQueryKeywords() {
		return queryKeywords;
	}

	public Keywords getUpdateKeywords() {
		return updateKeywords;
	}

	public Keywords getDeleteKeywords() {
		return deleteKeywords;
	}

	public Keywords getSaveKeywords() {
		return saveKeywords;
	}

	private String startsWith(Keywords keywords, String express) {
		return keywords.streamAll().filter((e) -> express.startsWith(e)).findFirst().orElse(null);
	}

	public String startsWith(String express) {
		return Pair
				.process(Arrays.asList(queryKeywords, updateKeywords, deleteKeywords, saveKeywords),
						(e) -> startsWith(e, express), (e) -> e.getValue() != null)
				.map((e) -> e.getValue()).orElse(null);
	}
}
