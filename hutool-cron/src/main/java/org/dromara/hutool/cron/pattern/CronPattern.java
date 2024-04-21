/*
 * Copyright (c) 2023 looly(loolly@aliyun.com)
 * Hutool is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          https://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.dromara.hutool.cron.pattern;

import org.dromara.hutool.core.comparator.CompareUtil;
import org.dromara.hutool.core.date.CalendarUtil;
import org.dromara.hutool.cron.pattern.matcher.PatternMatcher;
import org.dromara.hutool.cron.pattern.parser.PatternParser;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 定时任务表达式<br>
 * 表达式类似于Linux的crontab表达式，表达式使用空格分成5个部分，按顺序依次为：
 * <ol>
 * <li><strong>分</strong> ：范围：0~59</li>
 * <li><strong>时</strong> ：范围：0~23</li>
 * <li><strong>日</strong> ：范围：1~31，<strong>"L"</strong> 表示月的最后一天</li>
 * <li><strong>月</strong> ：范围：1~12，同时支持不区分大小写的别名："jan","feb", "mar", "apr", "may","jun", "jul", "aug", "sep","oct", "nov", "dec"</li>
 * <li><strong>周</strong> ：范围：0 (Sunday)~6(Saturday)，7也可以表示周日，同时支持不区分大小写的别名："sun","mon", "tue", "wed", "thu","fri", "sat"，<strong>"L"</strong> 表示周六</li>
 * </ol>
 * <p>
 * 为了兼容Quartz表达式，同时支持6位和7位表达式，其中：<br>
 *
 * <pre>
 * 当为6位时，第一位表示<strong>秒</strong> ，范围0~59，但是第一位不做匹配
 * 当为7位时，最后一位表示<strong>年</strong> ，范围1970~2099，但是第7位不做解析，也不做匹配
 * </pre>
 * <p>
 * 当定时任务运行到的时间匹配这些表达式后，任务被启动。<br>
 * 注意：
 *
 * <pre>
 * 当isMatchSecond为{@code true}时才会匹配秒部分
 * 默认都是关闭的
 * </pre>
 * <p>
 * 对于每一个子表达式，同样支持以下形式：
 * <ul>
 * <li><strong>*</strong> ：表示匹配这个位置所有的时间</li>
 * <li><strong>?</strong> ：表示匹配这个位置任意的时间（与"*"作用一致）</li>
 * <li><strong>*&#47;2</strong> ：表示间隔时间，例如在分上，表示每两分钟，同样*可以使用数字列表代替，逗号分隔</li>
 * <li><strong>2-8</strong> ：表示连续区间，例如在分上，表示2,3,4,5,6,7,8分</li>
 * <li><strong>2,3,5,8</strong> ：表示列表</li>
 * <li><strong>cronA | cronB</strong> ：表示多个定时表达式</li>
 * </ul>
 * 注意：在每一个子表达式中优先级：
 *
 * <pre>
 * 间隔（/） &gt; 区间（-） &gt; 列表（,）
 * </pre>
 * <p>
 * 例如 2,3,6/3中，由于“/”优先级高，因此相当于2,3,(6/3)，结果与 2,3,6等价<br>
 * <br>
 * <p>
 * 一些例子：
 * <ul>
 * <li><strong>5 * * * *</strong> ：每个点钟的5分执行，00:05,01:05……</li>
 * <li><strong>* * * * *</strong> ：每分钟执行</li>
 * <li><strong>*&#47;2 * * * *</strong> ：每两分钟执行</li>
 * <li><strong>* 12 * * *</strong> ：12点的每分钟执行</li>
 * <li><strong>59 11 * * 1,2</strong> ：每周一和周二的11:59执行</li>
 * <li><strong>3-18&#47;5 * * * *</strong> ：3~18分，每5分钟执行一次，即0:03, 0:08, 0:13, 0:18, 1:03, 1:08……</li>
 * </ul>
 *
 * @author Looly
 */
public class CronPattern {

	private final String pattern;
	private final List<PatternMatcher> matchers;

	/**
	 * 解析表达式为 CronPattern
	 *
	 * @param pattern 表达式
	 * @return CronPattern
	 * @since 5.8.0
	 */
	public static CronPattern of(final String pattern) {
		return new CronPattern(pattern);
	}

	/**
	 * 构造
	 *
	 * @param pattern 表达式
	 */
	public CronPattern(final String pattern) {
		this.pattern = pattern;
		this.matchers = PatternParser.parse(pattern);
	}

	/**
	 * 给定时间是否匹配定时任务表达式
	 *
	 * @param millis        时间毫秒数
	 * @param isMatchSecond 是否匹配秒
	 * @return 如果匹配返回 {@code true}, 否则返回 {@code false}
	 */
	public boolean match(final long millis, final boolean isMatchSecond) {
		return match(TimeZone.getDefault(), millis, isMatchSecond);
	}

	/**
	 * 给定时间是否匹配定时任务表达式
	 *
	 * @param timezone      时区 {@link TimeZone}
	 * @param millis        时间毫秒数
	 * @param isMatchSecond 是否匹配秒
	 * @return 如果匹配返回 {@code true}, 否则返回 {@code false}
	 */
	public boolean match(final TimeZone timezone, final long millis, final boolean isMatchSecond) {
		final GregorianCalendar calendar = new GregorianCalendar(timezone);
		calendar.setTimeInMillis(millis);
		return match(calendar, isMatchSecond);
	}

	/**
	 * 给定时间是否匹配定时任务表达式
	 *
	 * @param calendar      时间
	 * @param isMatchSecond 是否匹配秒
	 * @return 如果匹配返回 {@code true}, 否则返回 {@code false}
	 */
	public boolean match(final Calendar calendar, final boolean isMatchSecond) {
		return match(PatternUtil.getFields(calendar, isMatchSecond));
	}

	/**
	 * 给定时间是否匹配定时任务表达式
	 *
	 * @param dateTime      时间
	 * @param isMatchSecond 是否匹配秒
	 * @return 如果匹配返回 {@code true}, 否则返回 {@code false}
	 * @since 5.8.0
	 */
	public boolean match(final LocalDateTime dateTime, final boolean isMatchSecond) {
		return match(PatternUtil.getFields(dateTime, isMatchSecond));
	}

	/**
	 * 返回匹配到的下一个时间
	 *
	 * @param calendar 时间
	 * @return 匹配到的下一个时间
	 */
	public Calendar nextMatchAfter(Calendar calendar) {
		// issue#I9FQUA，当提供的时间已经匹配表达式时，增加1秒以匹配下一个时间
		if(match(calendar, true)){
			final Calendar newCalendar = Calendar.getInstance(calendar.getTimeZone());
			newCalendar.setTimeInMillis(calendar.getTimeInMillis() + 1000);
			calendar = newCalendar;
		}

		Calendar next = nextMatchAfter(PatternUtil.getFields(calendar, true), calendar.getTimeZone());
		if (!match(next, true)) {
			next.set(Calendar.DAY_OF_MONTH, next.get(Calendar.DAY_OF_MONTH) + 1);
			next = CalendarUtil.beginOfDay(next);
			return nextMatchAfter(next);
		}
		return next;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final CronPattern that = (CronPattern) o;
		return Objects.equals(pattern, that.pattern);
	}

	@Override
	public int hashCode() {
		return Objects.hash(pattern);
	}

	@Override
	public String toString() {
		return this.pattern;
	}

	/**
	 * 给定时间是否匹配定时任务表达式
	 *
	 * @param fields 时间字段值，{second, minute, hour, dayOfMonth, monthBase1, dayOfWeekBase0, year}
	 * @return 如果匹配返回 {@code true}, 否则返回 {@code false}
	 */
	private boolean match(final int[] fields) {
		for (final PatternMatcher matcher : matchers) {
			if (matcher.match(fields)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取下一个最近的匹配日期时间
	 *
	 * @param values 时间字段值，{second, minute, hour, dayOfMonth, monthBase1, dayOfWeekBase0, year}
	 * @param zone   时区
	 * @return {@link Calendar}，毫秒数为0
	 */
	private Calendar nextMatchAfter(final int[] values, final TimeZone zone) {
		Calendar minMatch = null;
		for (final PatternMatcher matcher : matchers) {
			if(null == minMatch){
				minMatch = matcher.nextMatchAfter(values, zone);
			}else{
				minMatch = CompareUtil.min(minMatch, matcher.nextMatchAfter(values, zone));
			}
		}
		// 返回匹配到的最早日期
		return minMatch;
	}
}
