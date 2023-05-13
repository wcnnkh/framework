package io.basc.framework.data.repository;

import java.util.OptionalLong;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.Query;
import io.basc.framework.util.Elements;

/**
 * 对存储库的操作
 * 
 * @author wcnnkh
 *
 */
public interface RepositoryOperations {
	/**
	 * 删除操作
	 * 
	 * @param operation
	 * @return
	 */
	default OptionalLong delete(DeleteOperation operation) throws RepositoryException {
		return batchDelete(Elements.forArray(operation)).first();
	}

	Elements<OptionalLong> batchDelete(Elements<? extends DeleteOperation> operations) throws RepositoryException;

	/**
	 * 保存操作
	 * 
	 * @param operation
	 * @return
	 */
	default OptionalLong insert(InsertOperation operation) throws RepositoryException {
		return batchInsert(Elements.forArray(operation)).first();
	}

	Elements<OptionalLong> batchInsert(Elements<? extends InsertOperation> operations) throws RepositoryException;

	/**
	 * 不存在就插入
	 * 
	 * @param operation
	 * @return {@link OptionalLong#isPresent()}为false则说明数据已经存在
	 */
	default OptionalLong insertIfAbsent(InsertOperation operation) throws RepositoryException {
		SelectOperation selectOperation = new SelectOperation(operation);
		Query<Object> query = select(TypeDescriptor.valueOf(Object.class), selectOperation);
		if (query.getElements().isEmpty()) {
			// 如果不存在就进行插入
			InsertOperation insertOperation = new InsertOperation(InsertOperationSymbol.INSERT_IF_ABSENT, operation);
			return insertIfAbsent(insertOperation);
		}
		return OptionalLong.empty();
	}

	/**
	 * 默认调用{@link RepositoryOperations#insertIfAbsent(InsertOperation)}
	 * 
	 * @param operations
	 * @return
	 */
	default Elements<OptionalLong> batchInsertIfAbsent(Elements<? extends InsertOperation> operations)
			throws RepositoryException {
		return operations.map((e) -> insertIfAbsent(e));
	}

	/**
	 * 插入或更新
	 * 
	 * @param operation
	 * @return
	 */
	default OptionalLong insertOrUpdate(InsertOperation operation) throws RepositoryException {
		SelectOperation selectOperation = new SelectOperation(operation);
		Query<Object> query = select(TypeDescriptor.valueOf(Object.class), selectOperation);
		if (query.getElements().isEmpty()) {
			// 如果不存在就进行插入
			InsertOperation insertOperation = new InsertOperation(InsertOperationSymbol.INSERT_IF_ABSENT, operation);
			return insertIfAbsent(insertOperation);
		} else {
			// 如果存在就更新
			UpdateOperation updateOperation = new UpdateOperation(UpdateOperationSymbol.INSERT_OR_UPDATE, operation);
			return update(updateOperation);
		}
	}

	/**
	 * 默认调用{@link RepositoryOperations#insertOrUpdate(InsertOperation)}
	 * 
	 * @param operations
	 * @return
	 */
	default Elements<OptionalLong> batchInsertOrUpdate(Elements<? extends InsertOperation> operations)
			throws RepositoryException {
		return operations.map((e) -> insertOrUpdate(e));
	}

	/**
	 * 查询操作
	 * 
	 * @param <T>
	 * @param resultTypeDescriptor
	 * @param select
	 * @return
	 */
	<T> Query<T> select(TypeDescriptor resultTypeDescriptor, SelectOperation operation) throws RepositoryException;

	/**
	 * 更新操作
	 * 
	 * @param operation
	 * @return
	 */
	default OptionalLong update(UpdateOperation operation) throws RepositoryException {
		return batchUpdate(Elements.singleton(operation)).first();
	}

	Elements<OptionalLong> batchUpdate(Elements<? extends UpdateOperation> operations);
}
