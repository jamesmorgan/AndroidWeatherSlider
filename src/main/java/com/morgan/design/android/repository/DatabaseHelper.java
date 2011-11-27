package com.morgan.design.android.repository;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.morgan.design.android.domain.orm.WoeidChoice;
import com.morgan.design.android.util.DBUtils;
import com.morgan.design.android.util.Logger;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String LOG_TAG = DatabaseHelper.class.getSimpleName();
	private static final String DATABASE_NAME = "tea_round.db";

	// TODO reset to 1 for release
	private static final int DATABASE_VERSION = 1;

	private final Context context;

	public DatabaseHelper(final Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	private Dao<WoeidChoice, Integer> woeIdDao = null;

	@Override
	public void onCreate(final SQLiteDatabase db, final ConnectionSource connectionSource) {
		Logger.i(LOG_TAG, "onCreate");
		try {
			dropTablesIfExists(connectionSource);
			TableUtils.createTableIfNotExists(connectionSource, WoeidChoice.class);
		}
		catch (final SQLException e) {
			Logger.e(LOG_TAG, "Can't create database", e);
		}
	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust the various data to
	 * match the new version number.
	 */
	@Override
	public void onUpgrade(final SQLiteDatabase db, final ConnectionSource connectionSource, int oldVersion, final int newVersion) {
		Logger.i(LOG_TAG, "onUpgrade, oldVersion=[%s], newVersion=[%s]", oldVersion, newVersion);
		try {

			if (newVersion == 1) {
				onCreate(db, connectionSource);
				return;
			}

			while (++oldVersion <= newVersion) {
				switch (oldVersion) {
					case 2: {
						break;
					}
					case 3: {
						break;
					}
				}
			}

			final List<String> availableUpdates = UpgradeHelper.availableUpdates(this.context.getResources());
			Logger.d(LOG_TAG, "Found a total of %s update statements", availableUpdates.size());

			for (final String statement : availableUpdates) {
				DBUtils.executeInTransaction(db, new Runnable() {
					@Override
					public void run() {
						Logger.d(LOG_TAG, "Executing statement: %s", statement);
						db.execSQL(statement);
					}
				});
			}
		}
		catch (final android.database.SQLException e) {
			Logger.e(LOG_TAG, "Can't migrate databases, bootstrap database, data will be lost", e);
			onCreate(db, connectionSource);
		}
	}

	private void dropTablesIfExists(final ConnectionSource connectionSource) throws SQLException {
		TableUtils.dropTable(connectionSource, WoeidChoice.class, true);
	}

	@Override
	public void close() {
		super.close();
		this.woeIdDao = null;
	}

	public Dao<WoeidChoice, Integer> getWeoidChoiceDao() throws SQLException {
		return loadDao(this.woeIdDao, WoeidChoice.class);
	}

	private <T> Dao<T, Integer> loadDao(Dao<T, Integer> t, final Class<T> clazz) throws SQLException {
		if (t == null) {
			t = getDao(clazz);
		}
		return t;
	}

}
