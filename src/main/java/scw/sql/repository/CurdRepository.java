package scw.sql.repository;

import java.util.Iterator;

public interface CurdRepository<T, ID> extends Repository<T, ID>{
	long count();
	
	boolean delete(T entity);
	
	boolean deleteAll();
	
	boolean deleteById(ID id);
	
	boolean existsById(ID id);
	
	Iterator<T> findAll();
	
	Iterable<T> findAllById(Iterable<ID> ids);
	
	T findById(ID id);
	
	<S extends T> S save(S entity);
	
	<S extends T> Iterable<S> saveAll(Iterable<S> entities);
}
