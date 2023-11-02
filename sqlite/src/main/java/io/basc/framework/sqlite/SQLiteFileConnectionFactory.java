package io.basc.framework.sqlite;

import java.io.File;

import org.sqlite.JDBC;
import org.sqlite.SQLiteDataSource;

import io.basc.framework.jdbc.template.DatabaseConnectionFactory;
import io.basc.framework.jdbc.template.DatabaseDialect;
import io.basc.framework.jdbc.template.DatabaseURL;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.mapper.support.Copy;
import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class SQLiteFileConnectionFactory extends SQLiteConnectionFactory {
	private final File directory;

	public SQLiteFileConnectionFactory(File directory, String databaseName) {
		this(new File(directory, databaseName));
	}

	public SQLiteFileConnectionFactory(File databaseFile) {
		super();
		Assert.isTrue(databaseFile.isFile(), "The database file path[" + databaseFile + "] type must be a file");
		directory = databaseFile.getParentFile();
		getDataSource().setUrl(JDBC.PREFIX + databaseFile.getPath());
		getDataSource().setDatabaseName(databaseFile.getName());
		setDatabaseName(databaseFile.getName());
	}

	protected SQLiteFileConnectionFactory(@NonNull SQLiteDataSource dataSource, DatabaseDialect databaseDialect,
			File directory) {
		super(dataSource, databaseDialect);
		this.directory = directory;
	}

	@Override
	public DatabaseConnectionFactory newDatabaseConnectionFactory(String databaseName) throws UnsupportedException {
		DatabaseConnectionFactory connectionFactory = getDataSourceDatabaseConnectionFactory(databaseName);
		if (connectionFactory == null) {
			SQLiteDataSource dataSource = new SQLiteDataSource();
			Copy.copy(getDataSource(), dataSource);
			dataSource.setDatabaseName(databaseName);

			DatabaseURL databaseURL = getDatabaseDialect().resolveUrl(getDataSource().getUrl());
			databaseURL.setDatabaseName(databaseName);
			dataSource.setUrl(databaseURL.getRawURL());
			connectionFactory = new SQLiteFileConnectionFactory(dataSource, getDatabaseDialect(), this.directory);
			// 需要这样吗？
			registerDataSource(databaseName, dataSource);
		}
		return connectionFactory;
	}

	@Override
	public Elements<String> getDatabaseNames() {
		Elements<String> fileNames = Elements.forArray(directory.listFiles()).filter((e) -> e.isFile())
				.map((e) -> e.getName());
		return fileNames.concat(super.getDatabaseNames()).distinct();
	}
}
