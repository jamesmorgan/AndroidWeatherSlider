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
import com.morgan.design.android.domain.orm.WeatherChoice;
import com.morgan.design.android.repository.DatabaseHelper;
import com.morgan.design.android.util.DBUtils;
import com.morgan.design.android.util.Logger;

public class WeatherChoiceDao {

	private static final String LOG_TAG = "WoeidChoiceDao";
	private final Dao<WeatherChoice, Integer> weatherChoiceDao;

	public WeatherChoiceDao(final DatabaseHelper databaseHelper) {
		this.weatherChoiceDao = getWeatherChoiceDao(databaseHelper);
	}

	private Dao<WeatherChoice, Integer> getWeatherChoiceDao(final DatabaseHelper databaseHelper) {
		try {
			if (isNull(getWeatherChoiceDao())) {
				return databaseHelper.getWeatherChoiceDao();
			}
			return null;
		}
		catch (final SQLException sqlException) {
			logError(sqlException);
		}
		return null;
	}

	public Dao<WeatherChoice, Integer> getWeatherChoiceDao() {
		return this.weatherChoiceDao;
	}

	public WeatherChoice findByWoeid(final String woeidId) {
		Logger.d(LOG_TAG, "Find by woeid, woeidId=[%s]", woeidId);
		try {
			final QueryBuilder<WeatherChoice, Integer> queryBuilder = getWeatherChoiceDao().queryBuilder();
			final Where<WeatherChoice, Integer> where = queryBuilder.where();
			where.eq(WeatherChoice.WOEID_ID, new SelectArg(woeidId));
			Logger.d(LOG_TAG, queryBuilder.prepareStatementString());

			final PreparedQuery<WeatherChoice> preparedQuery = queryBuilder.prepare();

			return getWeatherChoiceDao().queryForFirst(preparedQuery);
		}
		catch (final SQLException exception) {
			logError(exception);
		}
		return null;
	}

	public WeatherChoice findByWoeidOrNewInstance(final String woeidId) {
		Logger.d(LOG_TAG, "Find by woeid or new instance, woeidId=[%s]", woeidId);
		if (isNotBlank(woeidId)) {
			return null;
		}
		try {
			WeatherChoice woeidChoice = findByWoeid(woeidId);
			if (isNull(woeidChoice)) {
				woeidChoice = new WeatherChoice();
				woeidChoice.setWoeid(woeidId);
				woeidChoice.setCreatedDateTime(new Date());
				getWeatherChoiceDao().create(woeidChoice);
			}
			return woeidChoice;
		}
		catch (final SQLException exception) {
			logError(exception);
		}
		return null;
	}

	public void create(final WeatherChoice woeidChoice) {
		Logger.d(LOG_TAG, "Create new instance, woeidChoice=[%s]", woeidChoice);
		if (isNotNull(woeidChoice)) {
			try {
				getWeatherChoiceDao().create(woeidChoice);
			}
			catch (final SQLException exception) {
				logError(exception);
			}
		}
	}

	public int update(final WeatherChoice woeidChoice) {
		Logger.d(LOG_TAG, "Updating WOEID woeid=[%s]", woeidChoice);
		return DBUtils.executeInSafety(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return getWeatherChoiceDao().update(woeidChoice);
			}
		});
	}

	/**
	 * @return {@link List} {@link WeatherChoice}, never null
	 */
	public List<WeatherChoice> findAllWoeidChoices() {
		Logger.d(LOG_TAG, "Finding all woeid");
		return DBUtils.executeInSafety(new Callable<List<WeatherChoice>>() {
			@Override
			public List<WeatherChoice> call() throws Exception {
				final List<WeatherChoice> woeidChoices = getWeatherChoiceDao().queryForAll();
				return null != woeidChoices
						? woeidChoices
						: new ArrayList<WeatherChoice>();
			}
		});
	}

	public int delete(final WeatherChoice woeidChoice) {
		Logger.d(LOG_TAG, "Deleting WOEID, woeid=[%s]", woeidChoice);
		return DBUtils.executeInSafety(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return getWeatherChoiceDao().delete(woeidChoice);
			}
		});
	}

	public List<WeatherChoice> findActive() {
		Logger.d(LOG_TAG, "Finding all ACTIVE weather choices");
		try {
			final QueryBuilder<WeatherChoice, Integer> queryBuilder = getWeatherChoiceDao().queryBuilder();
			final Where<WeatherChoice, Integer> where = queryBuilder.where();
			where.eq(WeatherChoice.ACTIVE, new SelectArg(Boolean.TRUE));
			Logger.d(LOG_TAG, queryBuilder.prepareStatementString());

			final PreparedQuery<WeatherChoice> preparedQuery = queryBuilder.prepare();

			final List<WeatherChoice> results = getWeatherChoiceDao().query(preparedQuery);
			return null != results
					? results
					: new ArrayList<WeatherChoice>();
		}
		catch (final SQLException exception) {
			logError(exception);
		}
		return new ArrayList<WeatherChoice>();
	}

	private static void logError(final Exception e) {
		Logger.e(LOG_TAG, "SQLException ", e);
		e.printStackTrace();
	}

}
