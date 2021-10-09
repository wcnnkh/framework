package io.basc.framework.sql;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import io.basc.framework.util.AbstractIterator;
import io.basc.framework.util.Pair;
import io.basc.framework.util.StaticSupplier;
import io.basc.framework.util.StringUtils;

/**
 * 迭代分割sql
 * 
 * @author shuchaowen
 *
 */
public class SqlSplitIterator extends AbstractIterator<Sql> {
	private final Sql sql;
	private final Collection<? extends CharSequence> filters;
	private final int endIndex;
	private int index;
	private Supplier<Pair<Integer, CharSequence>> current;

	public SqlSplitIterator(Sql sql, Collection<? extends CharSequence> filters, int beginIndex, int endIndex) {
		this.sql = sql;
		this.filters = filters;
		this.index = beginIndex;
		this.endIndex = endIndex;
	}

	@Override
	public boolean hasNext() {
		if (index >= endIndex) {
			return false;
		}

		if (current == null) {
			for (CharSequence filter : filters) {
				if (filter == null) {
					continue;
				}

				int index = StringUtils.indexOf(sql.getSql(), filter, this.index, endIndex);
				if (index != -1) {
					current = new StaticSupplier<Pair<Integer, CharSequence>>(
							new Pair<Integer, CharSequence>(index, filter));
					break;
				}
			}
		}

		if (current == null) {
			return index < endIndex;
		}
		return true;
	}

	@Override
	public Sql next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		if (current == null) {
			// 最后一次了
			Sql value;
			if (index == 0) {
				value = this.sql;
			} else {
				value = SqlUtils.sub(sql, index, endIndex);
			}
			index = endIndex;
			return value;
		}

		Sql value = SqlUtils.sub(sql, index, current.get().getKey());
		index = current.get().getKey() + current.get().getValue().length();
		current = null;
		return value;
	}
}
