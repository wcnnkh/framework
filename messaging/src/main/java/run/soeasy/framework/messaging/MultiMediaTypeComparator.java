package run.soeasy.framework.messaging;

import java.util.Comparator;
import java.util.List;

import run.soeasy.framework.core.streaming.Streamable;

public class MultiMediaTypeComparator<T extends MediaType> implements Comparator<Streamable<T>> {

	@Override
	public int compare(Streamable<T> leftMediaTypes, Streamable<T> rightMediaTypes) {
		if (leftMediaTypes.equals(rightMediaTypes)) {
			return 0;
		}

		// 遍历两个集合的元素（已按特异性排序），比较首个不相等的元素
		List<T> thisList = leftMediaTypes.sorted(MediaType.SPECIFICITY_COMPARATOR).toList();
		List<T> otherList = rightMediaTypes.sorted(MediaType.SPECIFICITY_COMPARATOR).toList();
		int minSize = Math.min(thisList.size(), otherList.size());
		for (int i = 0; i < minSize; i++) {
			MediaType thisMediaType = thisList.get(i);
			MediaType otherMediaType = otherList.get(i);
			int compare = thisMediaType.compareTo(otherMediaType);
			if (compare != 0) {
				return compare;
			}
		}
		// 若前缀元素均相等，元素少的集合视为较小
		return Integer.compare(thisList.size(), otherList.size());
	}
}
