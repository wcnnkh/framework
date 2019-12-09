package scw.orm.sql;

import scw.orm.ORMException;


public interface GeneratorService {
	void process(GeneratorContext generatorContext) throws ORMException;
}
