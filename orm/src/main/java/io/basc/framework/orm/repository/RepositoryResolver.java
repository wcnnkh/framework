package io.basc.framework.orm.repository;

import io.basc.framework.data.repository.DeleteOperation;
import io.basc.framework.data.repository.InsertOperation;
import io.basc.framework.data.repository.UpdateOperation;
import io.basc.framework.value.Value;

public interface RepositoryResolver {
	InsertOperation resolveInsertOperation(Value source);

	DeleteOperation resolveDeleteOperation(Value source);

	UpdateOperation resolveUpdateOperation(Value source);

	UpdateOperation resolveSelectOperation(Value source);
}
