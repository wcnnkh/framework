package io.basc.framework.convert.lang;

import java.util.Comparator;
import java.util.Set;

import io.basc.framework.util.comparator.ComparableComparator;

public class ConversionComparator<T> implements Comparator<T> {
	public static final Comparator<Object> INSTANCE = new ConversionComparator<Object>();

	public int compare(T o1, T o2) {
		if (o1 instanceof ConvertibleConditional && o2 instanceof ConvertibleConditional) {
			Set<ConvertiblePair> pairs = ((ConvertibleConditional) o1).getConvertibleTypes();
			Set<ConvertiblePair> otherPairs = ((ConvertibleConditional) o2).getConvertibleTypes();
			for (ConvertiblePair pair : pairs) {
				for (ConvertiblePair other : otherPairs) {
					if (pair.compareTo(other) == -1) {
						return -1;
					}
				}
			}

			return ComparableComparator.INSTANCE.compare(o1, o2) == 1 ? -1 : 1;
		}

		if (o1 instanceof ConvertibleConditional) {
			return -1;
		}

		if (o2 instanceof ConvertibleConditional) {
			return 1;
		}

		return ComparableComparator.INSTANCE.compare(o1, o2) == -1 ? -1 : 1;
	}
}
