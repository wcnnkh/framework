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
	private static final Keywords END_WITH_KEYWORDS = new Keywords(Keywords.HUMP, "endWith");
	private static final Keywords EQU_KEYWORDS = new Keywords(Keywords.HUMP, "equ");
	private static final Keywords GEQ_KEYWORDS = new Keywords(Keywords.HUMP, "geq");
	private static final Keywords GTR_KEYWORDS = new Keywords(Keywords.HUMP, "gtr");
	private static final Keywords IN_KEYWORDS = new Keywords(Keywords.HUMP, "in");
	private static final Keywords LEQ_KEYWORDS = new Keywords(Keywords.HUMP, "leq");

	private static final Keywords LIKE_KEYWORDS = new Keywords(Keywords.HUMP, "like");

	private static final Keywords LSS_KEYWORDS = new Keywords(Keywords.HUMP, "lss");
	private static final Keywords NEQ_KEYWORDS = new Keywords(Keywords.HUMP, "neq");
	private static final Keywords SEARCH_KEYWORDS = new Keywords(Keywords.HUMP, "search");
	private static final Keywords START_WITH_KEYWORDS = new Keywords(Keywords.HUMP, "startWith");

	public static final ConditionKeywords DEFAULT = Sys.getEnv().getServiceLoader(ConditionKeywords.class)
			.first(() -> new ConditionKeywords());

	private final Keywords endWithKeywords;
	private final Keywords equalKeywords;
	private final Keywords equalOrGreaterThanKeywords;
	private final Keywords equalOrLessThanKeywords;
	private final Keywords greaterThanKeywords;
	private final Keywords inKeywords;
	private final Keywords lessThanKeywords;
	private final Keywords likeKeywords;
	private final Keywords notEqualKeywords;
	private final Keywords searchKeywords;
	private final Keywords startWithKeywords;

	public ConditionKeywords() {
		this(new Keywords(EQU_KEYWORDS, Keywords.HUMP), new Keywords(NEQ_KEYWORDS, Keywords.HUMP),
				new Keywords(LSS_KEYWORDS, Keywords.HUMP), new Keywords(LEQ_KEYWORDS, Keywords.HUMP),
				new Keywords(GTR_KEYWORDS, Keywords.HUMP), new Keywords(GEQ_KEYWORDS, Keywords.HUMP),
				new Keywords(IN_KEYWORDS, Keywords.HUMP), new Keywords(SEARCH_KEYWORDS, Keywords.HUMP),
				new Keywords(START_WITH_KEYWORDS, Keywords.HUMP), new Keywords(END_WITH_KEYWORDS, Keywords.HUMP),
				new Keywords(LIKE_KEYWORDS, Keywords.HUMP));
	}

	public ConditionKeywords(Keywords equalKeywords, Keywords notEqualKeywords, Keywords lessThanKeywords,
			Keywords equalOrLessThanKeywords, Keywords greaterThanKeywords, Keywords equalOrGreaterThanKeywords,
			Keywords inKeywords, Keywords searchKeywords, Keywords startWithKeywords, Keywords endWithKeywords,
			Keywords likeKeywords) {
		this.equalKeywords = aware(equalKeywords);
		this.notEqualKeywords = aware(notEqualKeywords);
		this.lessThanKeywords = aware(lessThanKeywords);
		this.equalOrLessThanKeywords = aware(equalOrLessThanKeywords);
		this.greaterThanKeywords = aware(greaterThanKeywords);
		this.equalOrGreaterThanKeywords = aware(equalOrGreaterThanKeywords);
		this.inKeywords = aware(inKeywords);
		this.searchKeywords = aware(searchKeywords);
		this.startWithKeywords = aware(startWithKeywords);
		this.endWithKeywords = aware(endWithKeywords);
		this.likeKeywords = aware(likeKeywords);
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
	public ConditionKeywords clone() {
		return new ConditionKeywords(equalKeywords.clone(), notEqualKeywords.clone(), lessThanKeywords.clone(),
				equalOrLessThanKeywords.clone(), greaterThanKeywords.clone(), equalOrGreaterThanKeywords.clone(),
				inKeywords.clone(), searchKeywords.clone(), startWithKeywords.clone(), endWithKeywords.clone(),
				likeKeywords.clone());
	}

	public Keywords getEndWithKeywords() {
		return endWithKeywords;
	}

	public Keywords getEqualKeywords() {
		return equalKeywords;
	}

	public Keywords getEqualOrGreaterThanKeywords() {
		return equalOrGreaterThanKeywords;
	}

	public Keywords getEqualOrLessThanKeywords() {
		return equalOrLessThanKeywords;
	}

	public Keywords getGreaterThanKeywords() {
		return greaterThanKeywords;
	}

	public Keywords getInKeywords() {
		return inKeywords;
	}

	public Keywords getLessThanKeywords() {
		return lessThanKeywords;
	}

	public Keywords getLikeKeywords() {
		return likeKeywords;
	}

	public Keywords getNotEqualKeywords() {
		return notEqualKeywords;
	}

	public Keywords getSearchKeywords() {
		return searchKeywords;
	}

	public Keywords getStartWithKeywords() {
		return startWithKeywords;
	}

	public Pair<String, Integer> indexOf(String express) {
		return StreamProcessorSupport
				.process(
						Arrays.asList(equalKeywords, notEqualKeywords, lessThanKeywords, equalOrLessThanKeywords,
								greaterThanKeywords, equalOrGreaterThanKeywords, inKeywords, searchKeywords,
								startWithKeywords, endWithKeywords, likeKeywords),
						(e) -> e.indexOf(express), (e) -> e.getValue() != null)
				.map((e) -> e.getValue()).orElse(null);
	}

	@Override
	public boolean test(String key) {
		return equalKeywords.test(key) && notEqualKeywords.test(key) && lessThanKeywords.test(key)
				&& equalOrLessThanKeywords.test(key) && greaterThanKeywords.test(key)
				&& equalOrGreaterThanKeywords.test(key) && inKeywords.test(key) && searchKeywords.test(key)
				&& startWithKeywords.test(key) && endWithKeywords.test(key) && likeKeywords.test(key);
	}
}
