package io.basc.framework.orm.repository;

import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class CurdRepositoryRegistry {
	private final Map<Class<?>, CurdRepository<?>> repositoryMap = new HashMap<Class<?>, CurdRepository<?>>();
	private Repository repository;

	public <T> void register(Class<? extends T> entityClass,
			CurdRepository<T> repository) {
		synchronized (repositoryMap) {
			repositoryMap.put(entityClass, repository);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> CurdRepository<T> getCurdRepository(Class<T> entityClass) {
		Assert.requiredArgument(entityClass != null, "entityClass");
		CurdRepository<T> curdRepository = (CurdRepository<T>) repositoryMap
				.get(entityClass);
		if (curdRepository == null && repository == null) {
			throw new NotSupportedException(entityClass.getName());
		}

		synchronized (repositoryMap) {
			curdRepository = (CurdRepository<T>) repositoryMap.get(entityClass);
			if (curdRepository == null && repository == null) {
				throw new NotSupportedException(entityClass.getName());
			}

			curdRepository = new DefaultCurdRepository<T>(entityClass,
					repository);
			repositoryMap.put(entityClass, curdRepository);
		}
		return curdRepository;
	}

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}
}
