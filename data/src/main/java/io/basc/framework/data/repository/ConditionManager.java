package io.basc.framework.data.repository;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import io.basc.framework.lang.AlreadyExistsException;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.Pair;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.stream.StreamProcessorSupport;

/**
 * 条件类型
 * 
 * @author alisa
 *
 */
public class ConditionManager {
	private static final Set<String> AND_KEYS = new HashSet<String>(Arrays.asList("And"));
	private static final Set<String> OR_KEYS = new HashSet<String>(Arrays.asList("Or"));
	private static final Set<String> EQU_KEYS = new HashSet<String>(Arrays.asList("Equ"));
	private static final Set<String> NEQ_KEYS = new HashSet<String>(Arrays.asList("Neq"));
	private static final Set<String> LSS_KEYS = new HashSet<String>(Arrays.asList("Lss"));
	private static final Set<String> LEQ_KEYS = new HashSet<String>(Arrays.asList("Leq"));
	private static final Set<String> GTR_KEYS = new HashSet<String>(Arrays.asList("Gtr"));
	private static final Set<String> GEQ_KEYS = new HashSet<String>(Arrays.asList("Geq"));

	private final Set<String> andKeys = new HashSet<String>();
	private final Set<String> orKeys = new HashSet<String>();
	private final Set<String> equKeys = new HashSet<String>();
	private final Set<String> neqKeys = new HashSet<String>();
	private final Set<String> lssKeys = new HashSet<String>();
	private final Set<String> leqKeys = new HashSet<String>();
	private final Set<String> gtrKeys = new HashSet<String>();
	private final Set<String> geqKeys = new HashSet<String>();

	public ConditionManager() {
		reset();
	}

	public void reset() {
		andKeys.addAll(AND_KEYS);
		orKeys.addAll(OR_KEYS);
		equKeys.addAll(EQU_KEYS);
		neqKeys.addAll(NEQ_KEYS);
		lssKeys.addAll(LSS_KEYS);
		leqKeys.addAll(LEQ_KEYS);
		gtrKeys.addAll(GTR_KEYS);
		geqKeys.addAll(GEQ_KEYS);
	}

	private boolean check(Collection<Collection<String>> keys, String key) {
		for (Collection<String> k : keys) {
			if (contains(k, key, true)) {
				return true;
			}
		}
		return false;
	}

	private void addKey(Collection<String> keys, String key) {
		if (check(Arrays.asList(andKeys, orKeys, equKeys, neqKeys, lssKeys, leqKeys, gtrKeys, geqKeys), key)) {
			throw new AlreadyExistsException(key);
		}
		keys.add(StringUtils.toUpperCase(key, 0, 1));
	}
	
	public boolean isAnd(String key) {
		return contains(andKeys, key, false);
	}

	public boolean isOr(String key) {
		return contains(orKeys, key, false);
	}

	public boolean isEqual(String key) {
		return contains(equKeys, key, false);
	}

	public boolean isNotEqual(String key) {
		return contains(neqKeys, key, false);
	}

	public boolean isLessThan(String key) {
		return contains(lssKeys, key, false);
	}

	public boolean isEqualOrLessThan(String key) {
		return contains(leqKeys, key, false);
	}

	public boolean isGreaterThan(String key) {
		return contains(gtrKeys, key, false);
	}

	public boolean isEqualOrGreaterThan(String key) {
		return contains(geqKeys, key, false);
	}

	private boolean contains(Collection<String> keys, String key, boolean match) {
		for (String k : keys) {
			if (match ? StringUtils.contains(k, key, true) : StringUtils.equals(k, key, true)) {
				return true;
			}
		}
		return false;
	}

	public Pair<String, Integer> indexOf(String express) {
		return indexOf(express, Arrays.asList(andKeys, orKeys, equKeys, neqKeys, lssKeys, leqKeys, gtrKeys, geqKeys));
	}

	private Pair<String, Integer> indexOf(String express, Collection<? extends Collection<String>> keys) {
		for (Collection<String> key : keys) {
			Pair<String, Integer> pair = StreamProcessorSupport.process(key, (e) -> express.indexOf(e),
					(e) -> e.getValue() != -1);
			if (pair != null && pair.getValue() != -1) {
				return pair;
			}
		}
		return null;
	}
}
