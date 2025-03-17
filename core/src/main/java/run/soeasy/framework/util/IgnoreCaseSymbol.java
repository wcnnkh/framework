package run.soeasy.framework.util;

import java.util.function.Supplier;

/**
 * 不区分大小写
 * 
 * @author shuchaowen
 *
 */
public class IgnoreCaseSymbol extends Symbol {
	private static final long serialVersionUID = 1L;

	public IgnoreCaseSymbol(String name) {
		super(name);
	}

	@Override
	public int hashCode() {
		return this.getName().toUpperCase().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}

		if (o instanceof IgnoreCaseSymbol) {
			IgnoreCaseSymbol ignoreCaseSymbol = (IgnoreCaseSymbol) o;
			return StringUtils.equals(ignoreCaseSymbol.getName(), this.getName());
		}
		return false;
	}

	public static <T extends IgnoreCaseSymbol> T forName(String name, Class<T> type, Supplier<? extends T> creator) {
		Assert.requiredArgument(name != null, "name");
		return getOrCreate(
				() -> Symbol.getSymbols(type).filter((e) -> StringUtils.equals(e.getName(), name, true)).first(),
				creator);
	}
}
