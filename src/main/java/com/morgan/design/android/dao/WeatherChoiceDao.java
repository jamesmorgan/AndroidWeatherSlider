package com.morgan.design.android.dao;

import static com.morgan.design.android.util.ObjectUtils.isNotBlank;
import static com.morgan.design.android.util.ObjectUtils.isNotNull;
import static com.morgan.design.android.util.ObjectUtils.isNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.morgan.design.Logger;
import com.morgan.design.android.dao.orm.WeatherChoice;
import com.morgan.design.android.repository.DatabaseHelper;
import com.morgan.design.android.util.DBUtils;

public class WeatherChoiceDao extends AbstractDao<WeatherChoice, Integer> {

	private static final String LOG_TAG = "WeatherChoiceDao";

	public WeatherChoiceDao(final DatabaseHelper databaseHelper) {
		super(databaseHelper);
	}

	public WeatherChoice findByWoeid(final String woeidId) {
		Logger.d(LOG_TAG, "Find by woeid, woeidId=[%s]", woeidId);
		try {
			final QueryBuilder<WeatherChoice, Integer> queryBuilder = this.dao.queryBuilder();
			final Where<WeatherChoice, Integer> where = queryBuilder.where();
			where.eq(WeatherChoice.WOEID_ID, new SelectArg(woeidId));
			Logger.d(LOG_TAG, queryBuilder.prepareStatementString());

			final PreparedQuery<WeatherChoice> preparedQuery = queryBuilder.prepare();

			return this.dao.queryForFirst(preparedQuery);
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
				this.dao.create(woeidChoice);
			}
			return woeidChoice;
		}
		catch (final SQLException exception) {
			logError(exception);
		}
		return null;
	}

	public void create(final WeatherChoice weatherChoice) {
		Logger.d(LOG_TAG, "Create new instance, weatherChoice=[%s]", weatherChoice);
		if (isNotNull(weatherChoice)) {
			try {
				this.dao.create(weatherChoice);
			}
			catch (final SQLException exception) {
				logError(exception);
			}
		}
	}

	public int update(final WeatherChoice weatherChoice) {
		Logger.d(LOG_TAG, "Updating WOEID woeid=[%s]", weatherChoice);
		return DBUtils.executeInSafety(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return WeatherChoiceDao.this.dao.update(weatherChoice);
			}
		});
	}

	/**
	 * @return {@link List} {@link WeatherChoice}, order by active, location name, never null
	 */
	public List<WeatherChoice> findAllWeathers() {
		Logger.d(LOG_TAG, "Finding all weathers");
		return DBUtils.executeInSafety(new Callable<List<WeatherChoice>>() {
			@Override
			public List<WeatherChoice> call() throws Exception {

				final PreparedQuery<WeatherChoice> orderBy = WeatherChoiceDao.this.dao.queryBuilder()
					.orderBy(WeatherChoice.VALID, false)
					.orderBy(WeatherChoice.ACTIVE, false)
					.orderBy(WeatherChoice.ROAMING, false)
					.orderBy(WeatherChoice.WEATHER_LOCATION, true)
					.prepare();

				Logger.d(LOG_TAG, orderBy.getStatement());

				final List<WeatherChoice> woeidChoices = WeatherChoiceDao.this.dao.query(orderBy);

				return null != woeidChoices
						? woeidChoices
						: new ArrayList<WeatherChoice>();
			}
		});
	}

	public int delete(final WeatherChoice weatherChoice) {
		Logger.d(LOG_TAG, "Deleting WOEID, woeid=[%s]", weatherChoice);
		return DBUtils.executeInSafety(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return WeatherChoiceDao.this.dao.delete(weatherChoice);
			}
		});
	}

	public List<WeatherChoice> getActiveStaticLocations() {
		Logger.d(LOG_TAG, "Finding all ACTIVE weather choices");
		try {
			final QueryBuilder<WeatherChoice, Integer> queryBuilder = this.dao.queryBuilder();
			final Where<WeatherChoice, Integer> where = queryBuilder.where();

			where.eq(WeatherChoice.ACTIVE, new SelectArg(Boolean.TRUE))
				.and()
				.eq(WeatherChoice.ROAMING, new SelectArg(Boolean.FALSE));

			Logger.d(LOG_TAG, queryBuilder.prepareStatementString());

			final List<WeatherChoice> results = this.dao.query(queryBuilder.prepare());
			return null != results
					? results
					: new ArrayList<WeatherChoice>();
		}
		catch (final SQLException exception) {
			logError(exception);
		}
		return new ArrayList<WeatherChoice>();
	}

	public WeatherChoice getRoamingLocation() {
		try {
			final QueryBuilder<WeatherChoice, Integer> queryBuilder = this.dao.queryBuilder();
			final Where<WeatherChoice, Integer> where = queryBuilder.where();

			where.eq(WeatherChoice.ROAMING, new SelectArg(Boolean.TRUE));

			Logger.d(LOG_TAG, queryBuilder.prepareStatementString());

			return this.dao.queryForFirst(queryBuilder.prepare());
		}
		catch (final SQLException exception) {
			logError(exception);
		}
		return null;
	}

	public WeatherChoice getActiveRoamingLocation() {
		try {
			final QueryBuilder<WeatherChoice, Integer> queryBuilder = this.dao.queryBuilder();
			final Where<WeatherChoice, Integer> where = queryBuilder.where();

			where.eq(WeatherChoice.ACTIVE, new SelectArg(Boolean.TRUE))
				.and()
				.eq(WeatherChoice.ROAMING, new SelectArg(Boolean.TRUE));

			Logger.d(LOG_TAG, queryBuilder.prepareStatementString());

			return this.dao.queryForFirst(queryBuilder.prepare());
		}
		catch (final SQLException exception) {
			logError(exception);
		}
		return null;
	}

	public WeatherChoice getById(final int weatherId) {
		try {
			return this.dao.queryForId(weatherId);
		}
		catch (final SQLException exception) {
			logError(exception);
		}
		return null;
	}

	public boolean hasActiveNotifications() {
		try {
			final PreparedQuery<WeatherChoice> preparedQuery = this.dao.queryBuilder()
				.setCountOf(true)
				.where()
				.eq(WeatherChoice.ACTIVE, new SelectArg(Boolean.TRUE))
				.prepare();

			Logger.d(LOG_TAG, preparedQuery.getStatement());

			return 0 != this.dao.countOf(preparedQuery);
		}
		catch (final SQLException exception) {
			logError(exception);
		}
		return false;
	}
}
