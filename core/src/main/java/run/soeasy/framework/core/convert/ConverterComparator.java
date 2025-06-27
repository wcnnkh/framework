package run.soeasy.framework.core.convert;

import java.util.Set;

import run.soeasy.framework.core.spi.ServiceComparator;

public class ConverterComparator extends ServiceComparator<Converter> {
	public static final ConverterComparator DEFAULT = new ConverterComparator(1);

	public ConverterComparator(int unknown) {
		super(unknown);
	}

	@Override
	public int compare(Converter o1, Converter o2) {
		if (o1 instanceof ConditionalConverter && o2 instanceof ConditionalConverter) {
			Set<TypeMapping> pairs = ((ConditionalConverter) o1).getConvertibleTypeMappings();
			Set<TypeMapping> otherPairs = ((ConditionalConverter) o2).getConvertibleTypeMappings();
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

		if (o1 instanceof ConditionalConverter) {
			return -1;
		}

		if (o2 instanceof ConditionalConverter) {
			return 1;
		}
		return super.compare(o1, o2);
	}

}
