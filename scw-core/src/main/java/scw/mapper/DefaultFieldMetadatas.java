package scw.mapper;

import java.util.List;

import scw.util.page.Pageable;
import scw.util.page.Pageables;

public class DefaultFieldMetadatas implements Pageables<Class<?>, FieldMetadata>{
	private final Class<?> cursorId;
	private final FieldMetadataFactory fieldMetadataFactory;
	
	public DefaultFieldMetadatas(Class<?> cursorId, FieldMetadataFactory fieldMetadataFactory) {
		this.cursorId = cursorId;
		this.fieldMetadataFactory = fieldMetadataFactory;
	}
	
	@Override
	public Class<?> getCursorId() {
		return cursorId;
	}

	@Override
	public long getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Class<?> getNextCursorId() {
		return cursorId.getSuperclass();
	}

	@Override
	public List<FieldMetadata> rows() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasNext() {
		Class<?> superClass = cursorId.getSuperclass();
		return superClass != null && superClass != Object.class;
	}

	@Override
	public Pageable<Class<?>, FieldMetadata> process(Class<?> start, long count) {
		
	}

}
