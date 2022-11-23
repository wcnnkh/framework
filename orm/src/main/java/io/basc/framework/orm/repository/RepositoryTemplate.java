package io.basc.framework.orm.repository;

public class RepositoryTemplate extends CurdTemplate {

	public RepositoryTemplate(Repository repository) {
		super(repository);
	}

	@Override
	public Repository getRepository() {
		return (Repository) super.getRepository();
	}
}
