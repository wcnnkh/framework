package scw.db;

import scw.db.database.DataBase;
import scw.transaction.sql.ConnectionFactory;

public interface DBConfig extends ConnectionFactory {

	DataBase getDataBase();
}
