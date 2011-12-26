package com.morgan.design.android.dao.orm;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "notification")
public class Notification {

	public static final String FK_WEATHER_CHOICE_ID = "fk_weather_choice_id";

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(columnName = FK_WEATHER_CHOICE_ID)
	private int fkWeatherChoiceId;

	@DatabaseField
	private int serviceId;

	public final int getId() {
		return this.id;
	}

	public final void setId(final int id) {
		this.id = id;
	}

	public final int getFkWeatherChoiceId() {
		return this.fkWeatherChoiceId;
	}

	public final void setFkWeatherChoiceId(final int fkWeatherChoiceId) {
		this.fkWeatherChoiceId = fkWeatherChoiceId;
	}

	public final int getServiceId() {
		return this.serviceId;
	}

	public final void setServiceId(final int serviceId) {
		this.serviceId = serviceId;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Notification [id=")
			.append(this.id)
			.append(", fkWeatherChoiceId=")
			.append(this.fkWeatherChoiceId)
			.append(", serviceId=")
			.append(this.serviceId)
			.append("]");
		return builder.toString();
	}

}
