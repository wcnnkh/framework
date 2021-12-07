package io.basc.framework.mapper;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultFields implements Fields {
	private FieldMetadatas fieldMetadatas;
	private final Field parentField;

	public DefaultFields(Field parentField, FieldMetadatas fieldMetadatas) {
		this.parentField = parentField;
		this.fieldMetadatas = fieldMetadatas;
	}

	public Iterator<Field> iterator() {
		return fieldMetadatas.stream().map((metadata) -> createField(metadata))
				.iterator();
	}

	protected Field createField(FieldMetadata fieldMetadata) {
		return new Field(parentField, fieldMetadata);
	}

	@Override
	public Class<?> getCursorId() {
		return fieldMetadatas.getCursorId();
	}

	@Override
	public Class<?> getNextCursorId() {
		return fieldMetadatas.getNextCursorId();
	}

	@Override
	public List<Field> getList() {
		return fieldMetadatas.getList().stream()
				.map((metadata) -> createField(metadata))
				.collect(Collectors.toList());
	}

	@Override
	public boolean hasNext() {
		return fieldMetadatas.hasNext();
	}
	
	public Fields jumpTo(Class<?> cursorId) {
		FieldMetadatas fieldMetadatas = this.fieldMetadatas.jumpTo(cursorId);
		return new DefaultFields(parentField, fieldMetadatas);
	}
}