package io.basc.framework.orm.repository;

import io.basc.framework.env.Sys;
import io.basc.framework.util.Keywords;
import io.basc.framework.util.Pair;
import io.basc.framework.util.stream.StreamProcessorSupport;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * 条件类型
 * 
 * @author wcnnkh
 *
 */
public class ConditionKeywords implements Predicate<String>, Cloneable {
	public static final ConditionKeywords DEFAULT = Sys.env.getServiceLoader(
			ConditionKeywords.class).first(() -> new ConditionKeywords());

	private static final Keywords EQU_KEYWORDS = new Keywords(Keywords.HUMP,
			"Equ");
	private static final Keywords NEQ_KEYWORDS = new Keywords(Keywords.HUMP,
			"Neq");
	private static final Keywords LSS_KEYWORDS = new Keywords(Keywords.HUMP,
			"Lss");
	private static final Keywords LEQ_KEYWORDS = new Keywords(Keywords.HUMP,
			"Leq");
	private static final Keywords GTR_KEYWORDS = new Keywords(Keywords.HUMP,
			"Gtr");
	private static final Keywords GEQ_KEYWORDS = new Keywords(Keywords.HUMP,
			"Geq");

	private static final Keywords IN_KEYWORDS = new Keywords(Keywords.HUMP,
			"In");

	private final Keywords equalKeywords;
	private final Keywords notEqualKeywords;
	private final Keywords lessThanKeywords;
	private final Keywords equalOrLessThanKeywords;
	private final Keywords greaterThanKeywords;
	private final Keywords equalOrGreaterThanKeywords;
	private final Keywords inKeywords;

	public ConditionKeywords() {
		this(new Keywords(EQU_KEYWORDS, Keywords.HUMP), new Keywords(
				NEQ_KEYWORDS, Keywords.HUMP), new Keywords(LSS_KEYWORDS,
				Keywords.HUMP), new Keywords(LEQ_KEYWORDS, Keywords.HUMP),
				new Keywords(GTR_KEYWORDS, Keywords.HUMP), new Keywords(
						GEQ_KEYWORDS, Keywords.HUMP), new Keywords(IN_KEYWORDS,
						Keywords.HUMP));
	}

	public ConditionKeywords(Keywords equalKeywords, Keywords notEqualKeywords,
			Keywords lessThanKeywords, Keywords equalOrLessThanKeywords,
			Keywords greaterThanKeywords, Keywords equalOrGreaterThanKeywords,
			Keywords inKeywords) {
		this.equalKeywords = aware(equalKeywords);
		this.notEqualKeywords = aware(notEqualKeywords);
		this.lessThanKeywords = aware(lessThanKeywords);
		this.equalOrLessThanKeywords = aware(equalOrLessThanKeywords);
		this.greaterThanKeywords = aware(greaterThanKeywords);
		this.equalOrGreaterThanKeywords = aware(equalOrGreaterThanKeywords);
		this.inKeywords = aware(inKeywords);
	}

	@Override
	public ConditionKeywords clone() {
		return new ConditionKeywords(equalKeywords.clone(),
				notEqualKeywords.clone(), lessThanKeywords.clone(),
				equalOrLessThanKeywords.clone(), greaterThanKeywords.clone(),
				equalOrGreaterThanKeywords.clone(), inKeywords.clone());
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
	public boolean test(String key) {
		return equalKeywords.test(key) && notEqualKeywords.test(key)
				&& lessThanKeywords.test(key)
				&& equalOrLessThanKeywords.test(key)
				&& greaterThanKeywords.test(key)
				&& equalOrGreaterThanKeywords.test(key) && inKeywords.test(key);
	}

	public Pair<String, Integer> indexOf(String express) {
		return StreamProcessorSupport
				.process(
						Arrays.asList(equalKeywords, notEqualKeywords,
								lessThanKeywords, equalOrLessThanKeywords,
								greaterThanKeywords,
								equalOrGreaterThanKeywords, inKeywords),
						(e) -> e.indexOf(express), (e) -> e.getValue() != null)
				.map((e) -> e.getValue()).orElse(null);
	}

	public Keywords getEqualKeywords() {
		return equalKeywords;
	}

	public Keywords getNotEqualKeywords() {
		return notEqualKeywords;
	}

	public Keywords getLessThanKeywords() {
		return lessThanKeywords;
	}

	public Keywords getEqualOrLessThanKeywords() {
		return equalOrLessThanKeywords;
	}

	public Keywords getGreaterThanKeywords() {
		return greaterThanKeywords;
	}

	public Keywords getEqualOrGreaterThanKeywords() {
		return equalOrGreaterThanKeywords;
	}

	public Keywords getInKeywords() {
		return inKeywords;
	}

	public String getKey(RepositoryColumn column) {
		return getEqualKeywords().getFirst();
	}
}
