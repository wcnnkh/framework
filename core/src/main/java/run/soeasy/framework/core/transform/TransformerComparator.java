package run.soeasy.framework.core.transform;

import java.util.Set;

import run.soeasy.framework.core.convert.TypeMapping;
import run.soeasy.framework.core.spi.ServiceComparator;

public class TransformerComparator extends ServiceComparator<Transformer> {
	public static final TransformerComparator DEFAULT = new TransformerComparator(1);

	public TransformerComparator(int unknown) {
		super(unknown);
	}

	@Override
	public int compare(Transformer o1, Transformer o2) {
		if (o1 instanceof ConditionalTransformer && o2 instanceof ConditionalTransformer) {
			Set<TypeMapping> pairs = ((ConditionalTransformer) o1).getTransformableTypeMappings();
			Set<TypeMapping> otherPairs = ((ConditionalTransformer) o2).getTransformableTypeMappings();
			// 如果两个集合完全相同，返回0
			if (pairs.equals(otherPairs)) {
				return 0;
			}

			// 遍历所有映射对，找到第一个能确定顺序的比较结果
			for (TypeMapping thisMapping : pairs) {
				for (TypeMapping otherMapping : otherPairs) {
					int comparison = thisMapping.compareTo(otherMapping);
					if (comparison != 0) {
						return comparison;
					}
				}
			}
		}

		if (o1 instanceof ConditionalTransformer) {
			return -1;
		}

		if (o2 instanceof ConditionalTransformer) {
			return 1;
		}

		return super.compare(o1, o2);
	}

}