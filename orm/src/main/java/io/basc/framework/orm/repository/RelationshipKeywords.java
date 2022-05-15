package io.basc.framework.orm.repository;

import io.basc.framework.env.Sys;
import io.basc.framework.util.Keywords;
import io.basc.framework.util.Pair;
import io.basc.framework.util.stream.StreamProcessorSupport;

import java.io.Serializable;
import java.util.Arrays;
import java.util.function.Predicate;

public class RelationshipKeywords implements Predicate<String>, Cloneable,
		Serializable {
	private static final long serialVersionUID = 1L;

	public static final RelationshipKeywords DEFAULT = Sys.env
			.getServiceLoader(RelationshipKeywords.class).first(
					() -> new RelationshipKeywords());

	private static final Keywords AND_KEYWORDS = new Keywords(Keywords.HUMP,
			"And");
	private static final Keywords OR_KEYWORDS = new Keywords(Keywords.HUMP,
			"Or");

	private final Keywords andKeywords;
	private final Keywords orKeywords;

	public RelationshipKeywords() {
		this(new Keywords(AND_KEYWORDS, Keywords.HUMP), new Keywords(
				OR_KEYWORDS, Keywords.HUMP));
	}

	protected RelationshipKeywords(Keywords andKeywords, Keywords orKeywords) {
		this.andKeywords = aware(andKeywords);
		this.orKeywords = aware(orKeywords);
	}

	private Keywords aware(Keywords keywords) {
		if (keywords.getPredicate() == null) {
			return keywords.setPredicate(this);
		}

		if (keywords.getPredicate() == this) {
			return keywords;
		}

		return keywords.setPredicate(keywords.getPredicate().and(this));
	}

	@Override
	public RelationshipKeywords clone() {
		return new RelationshipKeywords(andKeywords.clone(), orKeywords.clone());
	}

	@Override
	public boolean test(String key) {
		return andKeywords.test(key) && orKeywords.test(key);
	}

	public Keywords getAndKeywords() {
		return andKeywords;
	}

	public Keywords getOrKeywords() {
		return orKeywords;
	}

	public Pair<String, Integer> indexOf(String express) {
		return StreamProcessorSupport
				.process(Arrays.asList(andKeywords, orKeywords),
						(e) -> e.indexOf(express), (e) -> e.getValue() != null)
				.map((e) -> e.getValue()).orElse(null);
	}

	public String getKey(RepositoryColumn column) {
		return getAndKeywords().getFirst();
	}
}
