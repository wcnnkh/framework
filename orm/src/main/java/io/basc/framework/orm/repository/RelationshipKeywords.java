package io.basc.framework.orm.repository;

import java.io.Serializable;
import java.util.Arrays;
import java.util.function.Predicate;

import io.basc.framework.env.Sys;
import io.basc.framework.util.Keywords;
import io.basc.framework.util.Pair;

public class RelationshipKeywords implements Predicate<String>, Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	private static final Keywords AND_KEYWORDS = new Keywords(Keywords.HUMP, "And");
	private static final Keywords OR_KEYWORDS = new Keywords(Keywords.HUMP, "Or");
	private static final Keywords NOT_KEYWORDS = new Keywords(Keywords.HUMP, "Not");

	public static final RelationshipKeywords DEFAULT = Sys.getEnv().getServiceLoader(RelationshipKeywords.class)
			.findFirst().orElseGet(() -> new RelationshipKeywords());

	private final Keywords andKeywords;
	private final Keywords orKeywords;
	private final Keywords notKeywords;

	public RelationshipKeywords() {
		this(new Keywords(AND_KEYWORDS, Keywords.HUMP), new Keywords(OR_KEYWORDS, Keywords.HUMP),
				new Keywords(NOT_KEYWORDS, Keywords.HUMP));
	}

	protected RelationshipKeywords(Keywords andKeywords, Keywords orKeywords, Keywords notKeywords) {
		this.andKeywords = aware(andKeywords);
		this.orKeywords = aware(orKeywords);
		this.notKeywords = aware(notKeywords);
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
		return new RelationshipKeywords(andKeywords.clone(), orKeywords.clone(), notKeywords.clone());
	}

	@Override
	public boolean test(String key) {
		return andKeywords.test(key) && orKeywords.test(key) && notKeywords.test(key);
	}

	public Keywords getAndKeywords() {
		return andKeywords;
	}

	public Keywords getOrKeywords() {
		return orKeywords;
	}

	public Keywords getNotKeywords() {
		return notKeywords;
	}

	public Pair<String, Integer> indexOf(String express) {
		return Pair.process(Arrays.asList(andKeywords, orKeywords, notKeywords), (e) -> e.indexOf(express),
				(e) -> e.getValue() != null).map((e) -> e.getValue()).orElse(null);
	}
}
