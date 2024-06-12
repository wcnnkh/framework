package io.basc.framework.mapper.transfer;

import java.io.IOException;

import io.basc.framework.convert.lang.Value;
import io.basc.framework.execution.Parameter;
import io.basc.framework.execution.Parameters;
import io.basc.framework.util.Item;
import io.basc.framework.util.element.Elements;

public interface RowExporter extends RecordExporter {
	SimpleTitles getTitles();

	default boolean rewriteTitles(Elements<? extends Item> items) {
		if (items == null) {
			return false;
		}

		SimpleTitles titles = getTitles();
		if (titles == null || !titles.isEmpty()) {
			return false;
		}

		return titles.setItems(items);
	}

	/**
	 * 重新排列顺序后调用{@link #doWriteValues(Elements)}
	 * 
	 * @param record
	 * @throws IOException
	 */
	@Override
	default void doWriteParameters(Parameters record) throws IOException {
		SimpleTitles titles = getTitles();
		if (titles == null || titles.isEmpty()) {
			doWriteValues(record.getElements());
		} else {
			Elements<Value> values = titles.getElements().map((item) -> {
				Elements<Parameter> parameters = record.getElements(item.getName());
				for (String alaisName : item.getAliasNames()) {
					parameters = parameters.concat(record.getElements(alaisName));
				}

				Parameter parameter;
				if (parameters.isUnique()) {
					parameter = parameters.getUnique();
				} else {
					parameter = record.getElement(item.getPositionIndex());
				}
				return parameter;
			});
			doWriteValues(values);
		}
	}

	/**
	 * 默认由{@link #doWriteRecord(Parameters)}调用
	 * 
	 * @param values
	 * @throws IOException
	 */
	void doWriteValues(Elements<? extends Value> values) throws IOException;
}
