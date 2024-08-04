/*
 * Copyright (c) 2024 looly(loolly@aliyun.com)
 * Hutool is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          https://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.dromara.hutool.core.date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

public class TemporalAccessorUtilTest {

	@Test
	public void formatLocalDateTest(){
		final String format = TemporalAccessorUtil.format(LocalDate.of(2020, 12, 7), DatePattern.NORM_DATETIME_PATTERN);
		Assertions.assertEquals("2020-12-07 00:00:00", format);
	}

	@Test
	public void formatLocalTimeTest(){
		final String today = TemporalAccessorUtil.format(LocalDate.now(), DatePattern.NORM_DATE_PATTERN);
		final String format = TemporalAccessorUtil.format(LocalTime.MIN, DatePattern.NORM_DATETIME_PATTERN);
		Assertions.assertEquals(today + " 00:00:00", format);
	}

	@Test
	public void formatCustomTest(){
		final String today = TemporalAccessorUtil.format(
				LocalDate.of(2021, 6, 26), "#sss");
		Assertions.assertEquals("1624636800", today);

		final String today2 = TemporalAccessorUtil.format(
				LocalDate.of(2021, 6, 26), "#SSS");
		Assertions.assertEquals("1624636800000", today2);
	}

	@Test
	public void isInTest(){
		final String sourceStr = "2022-04-19 00:00:00";
		final String startTimeStr = "2022-04-19 00:00:00";
		final String endTimeStr = "2022-04-19 23:59:59";
		final boolean between = TimeUtil.isIn(
				TimeUtil.parse(sourceStr, DatePattern.NORM_DATETIME_FORMATTER),
				TimeUtil.parse(startTimeStr, DatePattern.NORM_DATETIME_FORMATTER),
				TimeUtil.parse(endTimeStr, DatePattern.NORM_DATETIME_FORMATTER));
		Assertions.assertTrue(between);
	}
}
