package com.morgan.design.android.dao;

import static com.morgan.design.android.util.ObjectUtils.isNotBlank;
import static com.morgan.design.android.util.ObjectUtils.isNotNull;
import static com.morgan.design.android.util.ObjectUtils.isNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.morgan.design.android.domain.orm.WoeidChoice;
import com.morgan.design.android.repository.DatabaseHelper;
import com.morgan.design.android.util.DBUtils;
import com.morgan.design.android.util.Logger;

public class WoeidChoiceDao {

	private static final String LOG_TAG = "WoeidChoiceDao";
	private final Dao<WoeidChoice, Integer> woeidDao;

	public WoeidChoiceDao(final DatabaseHelper databaseHelper) {
		this.woeidDao = getWoeidChoiceDao(databaseHelper);
	}

	private Dao<WoeidChoice, Integer> getWoeidChoiceDao(final DatabaseHelper databaseHelper) {
		try {
			if (isNull(getWoeidDao())) {
				return databaseHelper.getWeoidChoiceDao();
			}
			return null;
		}
		catch (final SQLException sqlException) {
			logError(sqlException);
		}
		return null;
	}

	public Dao<WoeidChoice, Integer> getWoeidDao() {
		return this.woeidDao;
	}

	public WoeidChoice findByWoeid(final String woeidId) {
		Logger.d(LOG_TAG, "Find by woeid, woeidId=[%s]", woeidId);
		try {
			final QueryBuilder<WoeidChoice, Integer> queryBuilder = getWoeidDao().queryBuilder();
			final Where<WoeidChoice, Integer> where = queryBuilder.where();
			where.eq(WoeidChoice.WOEID_ID, new SelectArg(woeidId));
			Logger.d(LOG_TAG, queryBuilder.prepareStatementString());

			final PreparedQuery<WoeidChoice> preparedQuery = queryBuilder.prepare();

			return getWoeidDao().queryForFirst(preparedQuery);
		}
		catch (final SQLException exception) {
			logError(exception);
		}
		return null;
	}

	public WoeidChoice findByWoeidOrNewInstance(final String woeidId) {
		Logger.d(LOG_TAG, "Find by woeid or new instance, woeidId=[%s]", woeidId);
		if (isNotBlank(woeidId)) {
			return null;
		}
		try {
			WoeidChoice woeidChoice = findByWoeid(woeidId);
			if (isNull(woeidChoice)) {
				woeidChoice = new WoeidChoice();
				woeidChoice.setWoeid(woeidId);
				woeidChoice.setCreatedDateTime(new Date());
				getWoeidDao().create(woeidChoice);
			}
			return woeidChoice;
		}
		catch (final SQLException exception) {
			logError(exception);
		}
		return null;
	}

	public void create(final WoeidChoice woeidChoice) {
		Logger.d(LOG_TAG, "Create new instance, woeidChoice=[%s]", woeidChoice);
		if (isNotNull(woeidChoice)) {
			try {
				getWoeidDao().create(woeidChoice);
			}
			catch (final SQLException exception) {
				logError(exception);
			}
		}
	}

	public int update(final WoeidChoice woeidChoice) {
		Logger.d(LOG_TAG, "Updating WOEID woeid=[%s]", woeidChoice);
		return DBUtils.executeInSafety(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return getWoeidDao().update(woeidChoice);
			}
		});
	}

	/**
	 * @return {@link List} {@link WoeidChoice}, never null
	 */
	public List<WoeidChoice> findAllWoeidChoices() {
		Logger.d(LOG_TAG, "Finding all woeid");
		return DBUtils.executeInSafety(new Callable<List<WoeidChoice>>() {
			@Override
			public List<WoeidChoice> call() throws Exception {
				final List<WoeidChoice> woeidChoices = getWoeidDao().queryForAll();
				return null != woeidChoices
						? woeidChoices
						: new ArrayList<WoeidChoice>();
			}
		});
	}

	public int delete(final WoeidChoice woeidChoice) {
		Logger.d(LOG_TAG, "Deleting WOEID, woeid=[%s]", woeidChoice);
		return DBUtils.executeInSafety(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return getWoeidDao().delete(woeidChoice);
			}
		});

	}

	private static void logError(final Exception e) {
		Logger.e(LOG_TAG, "SQLException ", e);
		e.printStackTrace();
	}
}
