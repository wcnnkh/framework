package io.basc.framework.data.repository;

import io.basc.framework.value.Value;

public interface RepositoryResolver {
	InsertOperation resolveInsertOperation(Value source);

	DeleteOperation resolveDeleteOperation(Value source);

	UpdateOperation resolveUpdateOperation(Value source);

	UpdateOperation resolveSelectOperation(Value source);
}
