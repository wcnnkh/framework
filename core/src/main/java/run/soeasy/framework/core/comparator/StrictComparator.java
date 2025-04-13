package run.soeasy.framework.core.comparator;

import java.util.Comparator;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.ObjectUtils;

@RequiredArgsConstructor
@Getter
public class StrictComparator<T> implements Comparator<T> {
	@NonNull
	private final Comparator<? super T> comparator;

	@Override
	public int compare(T o1, T o2) {
		int order = comparator.compare(o1, o2);
		if (order == 0) {
			// 当排序认为相等时并不代表对象是相同的
			if (o1 == o2 || ObjectUtils.equals(o1, o2)) {
				return 0;
			}

			// 返回1,后添加的放在后面
			return 1;
		}
		return order;
	}

}
