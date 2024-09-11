/*
 * Copyright (c) 2013-2024 Hutool Team and hutool.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.hutool.json.serializer.impl;

import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.math.NumberUtil;
import org.dromara.hutool.json.JSON;
import org.dromara.hutool.json.JSONException;
import org.dromara.hutool.json.JSONObject;
import org.dromara.hutool.json.serializer.JSONContext;
import org.dromara.hutool.json.serializer.JSONDeserializer;
import org.dromara.hutool.json.serializer.JSONSerializer;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.TemporalAccessor;

/**
 * {@link TemporalAccessor}的JSON自定义序列化实现，支持包括：<br>
 * <ul>
 *     <li>LocalDate</li>
 *     <li>LocalDateTime</li>
 *     <li>LocalTime</li>
 * </ul>
 *
 * @author looly
 * @since 5.7.22
 */
public class TemporalAccessorSerializer implements JSONSerializer<TemporalAccessor>, JSONDeserializer<TemporalAccessor> {

	private static final String YEAR_KEY = "year";
	private static final String MONTH_KEY = "month";
	private static final String DAY_KEY = "day";
	private static final String HOUR_KEY = "hour";
	private static final String MINUTE_KEY = "minute";
	private static final String SECOND_KEY = "second";
	private static final String NANO_KEY = "nano";

	private final Class<? extends TemporalAccessor> temporalAccessorClass;

	/**
	 * 构造
	 *
	 * @param temporalAccessorClass TemporalAccessor实现类型
	 */
	public TemporalAccessorSerializer(final Class<? extends TemporalAccessor> temporalAccessorClass) {
		this.temporalAccessorClass = temporalAccessorClass;
	}

	@Override
	public JSON serialize(final TemporalAccessor bean, final JSONContext context) {
		final JSONObject json;
		final JSON contextJson = context.getContextJson();
		if(contextJson instanceof JSONObject){
			json = (JSONObject) contextJson;
		}else {
			json = new JSONObject(7F / 0.75F + 1F, context.config());
		}
		if (bean instanceof LocalDate) {
			final LocalDate localDate = (LocalDate) bean;
			json.set(YEAR_KEY, localDate.getYear());
			json.set(MONTH_KEY, localDate.getMonthValue());
			json.set(DAY_KEY, localDate.getDayOfMonth());
		} else if (bean instanceof LocalDateTime) {
			final LocalDateTime localDateTime = (LocalDateTime) bean;
			json.set(YEAR_KEY, localDateTime.getYear());
			json.set(MONTH_KEY, localDateTime.getMonthValue());
			json.set(DAY_KEY, localDateTime.getDayOfMonth());
			json.set(HOUR_KEY, localDateTime.getHour());
			json.set(MINUTE_KEY, localDateTime.getMinute());
			json.set(SECOND_KEY, localDateTime.getSecond());
			json.set(NANO_KEY, localDateTime.getNano());
		} else if (bean instanceof LocalTime) {
			final LocalTime localTime = (LocalTime) bean;
			json.set(HOUR_KEY, localTime.getHour());
			json.set(MINUTE_KEY, localTime.getMinute());
			json.set(SECOND_KEY, localTime.getSecond());
			json.set(NANO_KEY, localTime.getNano());
		} else {
			throw new JSONException("Unsupported TemporalAccessor type to JSON: {}", bean.getClass().getName());
		}
		return json;
	}

	@Override
	public TemporalAccessor deserialize(final JSON json, final Type deserializeType) {
		final JSONObject jsonObject = (JSONObject) json;
		if (LocalDate.class.equals(this.temporalAccessorClass) || LocalDateTime.class.equals(this.temporalAccessorClass)) {
			// 年
			final Integer year = jsonObject.getInt(YEAR_KEY);
			Assert.notNull(year, "Field 'year' must be not null");

			// 月
			final String monthStr = jsonObject.getStr(MONTH_KEY);
			Assert.notNull(monthStr, "Field 'month' must be not null");
			Integer month = NumberUtil.parseInt(monthStr, null);
			if (null == month) {
				final Month monthEnum = Month.valueOf(monthStr);
				month = monthEnum.getValue();
			}

			// 日
			Integer day = jsonObject.getInt(DAY_KEY);
			if (null == day) {
				day = jsonObject.getInt("dayOfMonth");
				Assert.notNull(day, "Field 'day' or 'dayOfMonth' must be not null");
			}

			final LocalDate localDate = LocalDate.of(year, month, day);
			if (LocalDate.class.equals(this.temporalAccessorClass)) {
				return localDate;
			}

			// 时分秒毫秒
			final LocalTime localTime = LocalTime.of(
				jsonObject.getInt(HOUR_KEY, 0),
				jsonObject.getInt(MINUTE_KEY, 0),
				jsonObject.getInt(SECOND_KEY, 0),
				jsonObject.getInt(NANO_KEY, 0));

			return LocalDateTime.of(localDate, localTime);
		} else if (LocalTime.class.equals(this.temporalAccessorClass)) {
			return LocalTime.of(
				jsonObject.getInt(HOUR_KEY),
				jsonObject.getInt(MINUTE_KEY),
				jsonObject.getInt(SECOND_KEY),
				jsonObject.getInt(NANO_KEY));
		}

		throw new JSONException("Unsupported type from JSON: {}", this.temporalAccessorClass);
	}
}
