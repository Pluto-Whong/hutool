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

package org.dromara.hutool.core.util;

import org.dromara.hutool.core.convert.Convert;
import org.dromara.hutool.core.lang.Console;

import java.util.Properties;

/**
 * 系统属性工具<br>
 * 此工具用于读取系统属性或环境变量信息，封装包括：
 * <ul>
 *     <li>{@link System#getProperty(String)}</li>
 *     <li>{@link System#getenv(String)}</li>
 * </ul>
 *
 * @author looly
 * @since 5.7.16
 */
public class SystemUtil {

	/**
	 * Hutool自定义系统属性：是否解析日期字符串采用严格模式
	 */
	public static final String HUTOOL_DATE_LENIENT = "hutool.date.lenient";

	/**
	 * 取得系统属性，如果因为Java安全的限制而失败，则将错误打在Log中，然后返回 defaultValue
	 *
	 * @param name         属性名
	 * @param defaultValue 默认值
	 * @return 属性值或defaultValue
	 * @see System#getProperty(String)
	 * @see System#getenv(String)
	 */
	public static String get(final String name, final String defaultValue) {
		return ObjUtil.defaultIfNull(get(name), defaultValue);
	}

	/**
	 * 获得System属性
	 *
	 * @param key 键
	 * @return 属性值
	 * @see System#getProperty(String)
	 * @see System#getenv(String)
	 */
	public static String get(final String key) {
		return get(key, false);
	}

	/**
	 * 获得System属性，忽略无权限问题
	 *
	 * @param key 键
	 * @return 属性值
	 * @see System#getProperty(String)
	 * @see System#getenv(String)
	 */
	public static String getQuietly(final String key) {
		return get(key, true);
	}

	/**
	 * 取得系统属性，如果因为Java安全的限制而失败，则将错误打在Log中，然后返回 {@code null}
	 *
	 * @param name  属性名
	 * @param quiet 安静模式，不将出错信息打在{@code System.err}中
	 * @return 属性值或{@code null}
	 * @see System#getProperty(String)
	 * @see System#getenv(String)
	 */
	public static String get(final String name, final boolean quiet) {
		String value = null;
		try {
			value = System.getProperty(name);
		} catch (final SecurityException e) {
			if (!quiet) {
				Console.error("Caught a SecurityException reading the system property '{}'; " +
					"the SystemUtil property value will default to null.", name);
			}
		}

		if (null == value) {
			try {
				value = System.getenv(name);
			} catch (final SecurityException e) {
				if (!quiet) {
					Console.error("Caught a SecurityException reading the system env '{}'; " +
						"the SystemUtil env value will default to null.", name);
				}
			}
		}

		return value;
	}

	/**
	 * 获得boolean类型值
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return 值
	 */
	public static boolean getBoolean(final String key, final boolean defaultValue) {
		final String value = get(key);
		if (value == null) {
			return defaultValue;
		}

		return BooleanUtil.toBoolean(value);
	}

	/**
	 * 获得int类型值
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return 值
	 */
	public static int getInt(final String key, final int defaultValue) {
		return Convert.toInt(get(key), defaultValue);
	}

	/**
	 * 获得long类型值
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return 值
	 */
	public static long getLong(final String key, final long defaultValue) {
		return Convert.toLong(get(key), defaultValue);
	}

	/**
	 * @return 属性列表
	 */
	public static Properties getProps() {
		return System.getProperties();
	}

	/**
	 * 设置系统属性，value为{@code null}表示移除此属性
	 *
	 * @param key   属性名
	 * @param value 属性值，{@code null}表示移除此属性
	 */
	public static void set(final String key, final String value) {
		if (null == value) {
			System.clearProperty(key);
		} else {
			System.setProperty(key, value);
		}
	}

	/**
	 * 获得Java ClassPath路径，不包括 jre
	 *
	 * @return Java ClassPath路径，不包括 jre
	 */
	public static String[] getJavaClassPaths() {
		return get("java.class.path").split(get("path.separator"));
	}

	/**
	 * 获取用户路径（绝对路径）
	 *
	 * @return 用户路径
	 */
	public static String getUserHomePath() {
		return get("user.home");
	}

	/**
	 * 获取临时文件路径（绝对路径）
	 *
	 * @return 临时文件路径
	 * @since 4.0.6
	 */
	public static String getTmpDirPath() {
		return get("java.io.tmpdir");
	}
}
