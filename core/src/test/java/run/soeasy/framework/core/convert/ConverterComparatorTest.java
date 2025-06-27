package run.soeasy.framework.core.convert;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import run.soeasy.framework.core.comparator.Ordered;

public class ConverterComparatorTest {

	@Test
	public void test() {
		List<Converter> list = new ArrayList<>();
		list.add(Converter.assignable());
		Converter temp = (a, b, c) -> a;
		list.add(temp);

		Converter LOWEST_PRECEDENCE = new TempConverter(Ordered.LOWEST_PRECEDENCE);
		Converter HIGHEST_PRECEDENCE = new TempConverter(Ordered.HIGHEST_PRECEDENCE);
		Converter DEFAULT_PRECEDENCE = new TempConverter(Ordered.DEFAULT_PRECEDENCE);
		list.add(LOWEST_PRECEDENCE);
		list.add(HIGHEST_PRECEDENCE);
		list.add(DEFAULT_PRECEDENCE);

		list.sort(ConverterComparator.DEFAULT);
		list.forEach((e) -> System.out.println(e));

		assert list.get(0) == HIGHEST_PRECEDENCE;
		assert list.get(1) == Converter.assignable();
		assert list.get(2) == temp;
		assert list.get(3) == DEFAULT_PRECEDENCE;
		assert list.get(4) == LOWEST_PRECEDENCE;
	}

	@RequiredArgsConstructor
	@ToString
	private static class TempConverter implements Converter, Ordered {
		private final int order;

		@Override
		public int getOrder() {
			return order;
		}

		@Override
		public Object convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
				@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
