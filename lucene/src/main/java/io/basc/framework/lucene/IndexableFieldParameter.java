package io.basc.framework.lucene;

import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.param.Parameter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * TODO 字段类型还未完善
 * 
 * @author shuchaowen
 *
 */
@RequiredArgsConstructor
@Getter
@Setter
public class IndexableFieldParameter implements Parameter {
	private final IndexableField indexableField;
	private int positionIndex = -1;

	@Override
	public String getName() {
		return indexableField.name();
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		IndexableFieldType fieldType = indexableField.fieldType();
		DocValuesType docValuesType = fieldType.docValuesType();
		switch (docValuesType) {
		case BINARY:
			return TypeDescriptor.valueOf(byte[].class);
		case NUMERIC:
			return TypeDescriptor.valueOf(Number.class);
		default:
			return TypeDescriptor.valueOf(Object.class);
		}
	}

	@Override
	public Object getValue() {
		IndexableFieldType fieldType = indexableField.fieldType();
		DocValuesType docValuesType = fieldType.docValuesType();
		switch (docValuesType) {
		case BINARY:
			return indexableField.binaryValue().bytes;
		case NUMERIC:
			return indexableField.numericValue();
		default:
			return indexableField.getCharSequenceValue();
		}
	}

}
