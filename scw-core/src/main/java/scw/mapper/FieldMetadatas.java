package scw.mapper;

import scw.util.page.Pageables;

public interface FieldMetadatas extends Pageables<Class<?>, FieldMetadata> {
	
	/**
	 * 数量
	 */
	@Override
	default long getCount() {
		return rows().size();
	}
}
