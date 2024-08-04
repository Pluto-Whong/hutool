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

import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateBetweenTest {

	@Test
	public void betweenYearTest() {
		final Date start = DateUtil.parse("2017-02-01 12:23:46");
		final Date end = DateUtil.parse("2018-02-01 12:23:46");
		final long betweenYear = new DateBetween(start, end).betweenYear(false);
		Assertions.assertEquals(1, betweenYear);

		final Date start1 = DateUtil.parse("2017-02-01 12:23:46");
		final Date end1 = DateUtil.parse("2018-03-01 12:23:46");
		final long betweenYear1 = new DateBetween(start1, end1).betweenYear(false);
		Assertions.assertEquals(1, betweenYear1);

		// 不足1年
		final Date start2 = DateUtil.parse("2017-02-01 12:23:46");
		final Date end2 = DateUtil.parse("2018-02-01 11:23:46");
		final long betweenYear2 = new DateBetween(start2, end2).betweenYear(false);
		Assertions.assertEquals(0, betweenYear2);
	}

	@Test
	public void betweenYearTest2() {
		final Date start = DateUtil.parse("2000-02-29");
		final Date end = DateUtil.parse("2018-02-28");
		final long betweenYear = new DateBetween(start, end).betweenYear(false);
		Assertions.assertEquals(18, betweenYear);
	}

	@Test
	public void betweenYearTest3(){
		final String dateStr1 = "2023-02-28 00:00:01";
		final Date sdate = DateUtil.parse(dateStr1);
		final String dateStr2 = "2024-02-29 00:00:00";
		final Date edate = DateUtil.parse(dateStr2);

		final long result = DateUtil.betweenYear(sdate, edate, false);
		Assertions.assertEquals(0, result);
	}

	@Test
	public void betweenYearTest4(){
		final String dateStr1 = "2024-02-29 00:00:00";
		final Date sdate = DateUtil.parse(dateStr1);
		final String dateStr2 = "2025-02-28 00:00:00";
		final Date edate = DateUtil.parse(dateStr2);

		final long result = DateUtil.betweenYear(sdate, edate, false);
		Assertions.assertEquals(1, result);
	}

	@Test
	public void issueI97U3JTest(){
		final String dateStr1 = "2024-02-29 23:59:59";
		final Date sdate = DateUtil.parse(dateStr1);

		final String dateStr2 = "2023-03-01 00:00:00";
		final Date edate = DateUtil.parse(dateStr2);

		final long result = DateUtil.betweenYear(sdate, edate, false);
		Assertions.assertEquals(0, result);
	}

	@Test
	public void betweenMonthTest() {
		final Date start = DateUtil.parse("2017-02-01 12:23:46");
		final Date end = DateUtil.parse("2018-02-01 12:23:46");
		final long betweenMonth = new DateBetween(start, end).betweenMonth(false);
		Assertions.assertEquals(12, betweenMonth);

		final Date start1 = DateUtil.parse("2017-02-01 12:23:46");
		final Date end1 = DateUtil.parse("2018-03-01 12:23:46");
		final long betweenMonth1 = new DateBetween(start1, end1).betweenMonth(false);
		Assertions.assertEquals(13, betweenMonth1);

		// 不足
		final Date start2 = DateUtil.parse("2017-02-01 12:23:46");
		final Date end2 = DateUtil.parse("2018-02-01 11:23:46");
		final long betweenMonth2 = new DateBetween(start2, end2).betweenMonth(false);
		Assertions.assertEquals(11, betweenMonth2);
	}

	@Test
	public void betweenMinuteTest() {
		final Date date1 = DateUtil.parse("2017-03-01 20:33:23");
		final Date date2 = DateUtil.parse("2017-03-01 23:33:23");
		final String formatBetween = DateUtil.formatBetween(date1, date2, BetweenFormatter.Level.SECOND);
		Assertions.assertEquals("3小时", formatBetween);
	}

	@Test
	public void betweenWeeksTest(){
		final long betweenWeek = DateUtil.betweenWeek(
				DateUtil.parse("2020-11-21"),
				DateUtil.parse("2020-11-23"), false);

		final long betweenWeek2 = TimeUtil.between(
				TimeUtil.parse("2020-11-21", "yyy-MM-dd"),
				TimeUtil.parse("2020-11-23", "yyy-MM-dd"),
				ChronoUnit.WEEKS);
		Assertions.assertEquals(betweenWeek, betweenWeek2);
	}
}
