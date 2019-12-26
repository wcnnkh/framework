package scw.orm.sql;

import scw.orm.ORMException;
import scw.orm.sql.support.GeneratorContext;


public interface GeneratorService {
	void process(GeneratorContext generatorContext) throws ORMException;
}
