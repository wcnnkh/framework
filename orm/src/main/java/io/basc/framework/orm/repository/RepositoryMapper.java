package io.basc.framework.orm.repository;

import io.basc.framework.orm.ObjectRelationalMapper;

public interface RepositoryMapper<S, E extends Throwable> extends RepositoryResolver, ObjectRelationalMapper<S, E> {

}
