package com.morgan.design.android.dao;

import static com.morgan.design.android.util.ObjectUtils.isNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.morgan.design.android.domain.YahooWeatherInfo;
import com.morgan.design.android.domain.orm.WoeidChoice;
import com.morgan.design.android.repository.DatabaseHelper;
import com.morgan.design.android.util.DBUtils;

public class WoeidChoiceDao {

	private final Dao<WoeidChoice, Integer> woeidDao;

	public WoeidChoiceDao(final DatabaseHelper databaseHelper) {
		this.woeidDao = getWoeidChoiceDao(databaseHelper);
	}

	private Dao<WoeidChoice, Integer> getWoeidChoiceDao(final DatabaseHelper databaseHelper) {
		return DBUtils.executeInSafety(new Callable<Dao<WoeidChoice, Integer>>() {
			@Override
			public Dao<WoeidChoice, Integer> call() throws Exception {
				if (isNull(getWoeidDao())) {
					return databaseHelper.getWeoidChoiceDao();
				}
				return null;
			}
		});
	}

	public Dao<WoeidChoice, Integer> getWoeidDao() {
		return this.woeidDao;
	}

	public WoeidChoice findByWoeidOrNewInstance(final String woeidId) {
		return DBUtils.executeInSafety(new Callable<WoeidChoice>() {
			@Override
			public WoeidChoice call() throws Exception {
				if (null == woeidId) {
					return null;
				}
				final PreparedQuery<WoeidChoice> preparedQuery =
						getWoeidDao().queryBuilder().where().eq(WoeidChoice.WOEID_ID, woeidId).prepare();
				WoeidChoice woeidChoice = getWoeidDao().queryForFirst(preparedQuery);

				if (null == woeidChoice) {
					woeidChoice = new WoeidChoice();
					woeidChoice.setWoeid(woeidId);
					woeidChoice.setCreatedDateTime(new Date());
					getWoeidDao().create(woeidChoice);
				}
				return woeidChoice;
			}
		});
	}

	public int update(final WoeidChoice woeidChoice) {
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

	public int update(final YahooWeatherInfo currentWeather, final WoeidChoice woeidChoice) {
		return DBUtils.executeInSafety(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return getWoeidDao().update(woeidChoice);
			}
		});
	}

	public int delete(final WoeidChoice woeidChoice) {
		return DBUtils.executeInSafety(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return getWoeidDao().delete(woeidChoice);
			}
		});

	}
}
