package run.soeasy.framework.util.match;

import java.util.Comparator;

public interface Matcher<T> extends Comparator<T> {
	boolean isPattern(T source);

	boolean match(T pattern, T source);

	@Override
	default int compare(T o1, T o2) {
		if (isPattern(o1) && isPattern(o2)) {
			if (match(o1, o2)) {
				return 1;
			} else if (match(o2, o1)) {
				return -1;
			} else {
				return -1;
			}
		} else if (isPattern(o1)) {
			return 1;
		} else if (isPattern(o2)) {
			return -1;
		}
		return o1.equals(o1) ? 0 : -1;
	}
}
