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

package org.dromara.hutool.json;

import org.dromara.hutool.core.io.IORuntimeException;
import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.lang.mutable.MutableEntry;
import org.dromara.hutool.core.reflect.TypeReference;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.core.util.ObjUtil;
import org.dromara.hutool.json.support.JSONStrFormatter;
import org.dromara.hutool.json.xml.JSONXMLUtil;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Predicate;

/**
 * JSON工具类
 *
 * @author Looly
 */
public class JSONUtil {

	// region ----- of

	/**
	 * 创建JSONObject
	 *
	 * @return JSONObject
	 */
	public static JSONObject ofObj() {
		return JSONFactory.getInstance().ofObj();
	}

	/**
	 * 创建JSONObject
	 *
	 * @param config JSON配置
	 * @return JSONObject
	 * @since 5.2.5
	 */
	public static JSONObject ofObj(final JSONConfig config) {
		return JSONFactory.of(config, null).ofObj();
	}

	/**
	 * 创建 JSONArray
	 *
	 * @return JSONArray
	 */
	public static JSONArray ofArray() {
		return JSONFactory.getInstance().ofArray();
	}

	/**
	 * 创建 JSONArray
	 *
	 * @param config JSON配置
	 * @return JSONArray
	 * @since 5.2.5
	 */
	public static JSONArray ofArray(final JSONConfig config) {
		return JSONFactory.of(config, null).ofArray();
	}

	/**
	 * 创建JSONPrimitive对象，用于创建非JSON对象，例如：
	 * <pre>{@code
	 *   JSONUtil.ofPrimitive(1);
	 *   JSONUtil.ofPrimitive(1L);
	 *   JSONUtil.ofPrimitive(1.0);
	 *   JSONUtil.ofPrimitive(true);
	 *   JSONUtil.ofPrimitive("str");
	 * }</pre>
	 *
	 * @param value 值
	 * @return JSONPrimitive对象
	 * @since 6.0.0
	 */
	public static JSONPrimitive ofPrimitive(final Object value) {
		return ofPrimitive(value, JSONConfig.of());
	}

	/**
	 * 创建JSONPrimitive对象，用于创建非JSON对象，例如：
	 * <pre>{@code
	 *   JSONUtil.ofPrimitive(1, config);
	 *   JSONUtil.ofPrimitive(1L, config);
	 *   JSONUtil.ofPrimitive(1.0, config);
	 *   JSONUtil.ofPrimitive(true, config);
	 *   JSONUtil.ofPrimitive("str", config);
	 * }</pre>
	 *
	 * @param value  值
	 * @param config 配置
	 * @return JSONPrimitive对象
	 * @since 6.0.0
	 */
	public static JSONPrimitive ofPrimitive(final Object value, final JSONConfig config) {
		return JSONFactory.of(config, null).ofPrimitive(value);
	}
	// endregion

	// region ----- parse

	/**
	 * JSON字符串转JSONObject对象<br>
	 * 此方法会忽略空值，但是对JSON字符串不影响
	 *
	 * @param obj Bean对象或者Map
	 * @return JSONObject
	 */
	public static JSONObject parseObj(final Object obj) {
		return parseObj(obj, JSONConfig.of(), null);
	}

	/**
	 * JSON字符串转JSONObject对象<br>
	 * 此方法会忽略空值，但是对JSON字符串不影响
	 *
	 * @param obj    Bean对象或者Map
	 * @param config JSON配置
	 * @return JSONObject
	 */
	public static JSONObject parseObj(final Object obj, final JSONConfig config) {
		return parseObj(obj, config, null);
	}

	/**
	 * 对象转JSONObject对象
	 *
	 * @param obj       Bean对象或者Map
	 * @param config    JSON配置
	 * @param predicate 键值对过滤编辑器，可以通过实现此接口，完成解析前对键值对的过滤和修改操作，{@link Predicate#test(Object)}为{@code true}保留
	 * @return JSONObject
	 */
	public static JSONObject parseObj(final Object obj, final JSONConfig config, final Predicate<MutableEntry<Object, Object>> predicate) {
		return JSONFactory.of(config, predicate).parseObj(obj);
	}

	/**
	 * JSON字符串转JSONArray
	 *
	 * @param obj 数组或集合对象或字符串等
	 * @return JSONArray
	 */
	public static JSONArray parseArray(final Object obj) {
		return parseArray(obj, null);
	}

	/**
	 * JSON字符串转JSONArray
	 *
	 * @param obj 数组或集合对象
	 * @param config            JSON配置
	 * @return JSONArray
	 * @since 5.3.1
	 */
	public static JSONArray parseArray(final Object obj, final JSONConfig config) {
		return parseArray(obj, config, null);
	}

	/**
	 * 对象转JSONArray
	 *
	 * @param obj 数组或集合对象
	 * @param config            JSON配置
	 * @param predicate         index和值对过滤编辑器，可以通过实现此接口，完成解析前对键值对的过滤和修改操作，{@link Predicate#test(Object)}为{@code true}保留
	 * @return JSONArray
	 * @since 5.3.1
	 */
	public static JSONArray parseArray(final Object obj, final JSONConfig config, final Predicate<MutableEntry<Object, Object>> predicate) {
		return JSONFactory.of(config, predicate).parseArray(obj);
	}

	/**
	 * 转换对象为JSON，如果用户不配置JSONConfig，则JSON的有序与否与传入对象有关。<br>
	 * 支持的对象：
	 * <ul>
	 *     <li>String: 转换为相应的对象</li>
	 *     <li>Array、Iterable、Iterator：转换为JSONArray</li>
	 *     <li>Bean对象：转为JSONObject</li>
	 * </ul>
	 *
	 * @param obj 对象
	 * @return JSON
	 */
	public static JSON parse(final Object obj) {
		return parse(obj, null);
	}

	/**
	 * 转换对象为JSON，如果用户不配置JSONConfig，则JSON的有序与否与传入对象有关。<br>
	 * 支持的对象：
	 * <ul>
	 *     <li>String: 转换为相应的对象</li>
	 *     <li>Array、Iterable、Iterator：转换为JSONArray</li>
	 *     <li>Bean对象：转为JSONObject</li>
	 * </ul>
	 *
	 * @param obj    对象
	 * @param config JSON配置，{@code null}使用默认配置
	 * @return JSON（JSONObject or JSONArray）
	 */
	public static JSON parse(final Object obj, final JSONConfig config) {
		return parse(obj, config, null);
	}

	/**
	 * 转换对象为JSON，如果用户不配置JSONConfig，则JSON的有序与否与传入对象有关。<br>
	 * 支持的对象：
	 * <ul>
	 *     <li>String: 转换为相应的对象</li>
	 *     <li>Array、Iterable、Iterator：转换为JSONArray</li>
	 *     <li>Bean对象：转为JSONObject</li>
	 * </ul>
	 *
	 * @param obj       对象
	 * @param config    JSON配置，{@code null}使用默认配置
	 * @param predicate 键值对过滤编辑器，可以通过实现此接口，完成解析前对键值对的过滤和修改操作，{@link Predicate#test(Object)}为{@code true}保留
	 * @return JSON（JSONObject or JSONArray）
	 */
	public static JSON parse(final Object obj, final JSONConfig config, final Predicate<MutableEntry<Object, Object>> predicate) {
		return JSONFactory.of(config, predicate).parse(obj);
	}

	/**
	 * XML字符串转为JSONObject
	 *
	 * @param xmlStr XML字符串
	 * @return JSONObject
	 */
	public static JSONObject parseFromXml(final String xmlStr) {
		return JSONXMLUtil.toJSONObject(xmlStr);
	}
	// endregion

	// region ----- read

	/**
	 * 读取JSON
	 *
	 * @param file    JSON文件
	 * @param charset 编码
	 * @return JSON（包括JSONObject和JSONArray）
	 * @throws IORuntimeException IO异常
	 */
	public static JSON readJSON(final File file, final Charset charset) throws IORuntimeException {
		return FileUtil.read(file, charset, JSONUtil::parse);
	}

	/**
	 * 读取JSONObject
	 *
	 * @param file    JSON文件
	 * @param charset 编码
	 * @return JSONObject
	 * @throws IORuntimeException IO异常
	 */
	public static JSONObject readJSONObject(final File file, final Charset charset) throws IORuntimeException {
		return FileUtil.read(file, charset, JSONUtil::parseObj);
	}

	/**
	 * 读取JSONArray
	 *
	 * @param file    JSON文件
	 * @param charset 编码
	 * @return JSONArray
	 * @throws IORuntimeException IO异常
	 */
	public static JSONArray readJSONArray(final File file, final Charset charset) throws IORuntimeException {
		return FileUtil.read(file, charset, JSONUtil::parseArray);
	}
	// endregion

	// region ----- toJsonStr

	/**
	 * 转换为格式化后的JSON字符串
	 *
	 * @param obj Bean对象
	 * @return JSON字符串
	 */
	public static String toJsonPrettyStr(final Object obj) {
		return parse(obj).toStringPretty();
	}

	/**
	 * 转换为JSON字符串
	 *
	 * @param obj 被转为JSON的对象
	 * @return JSON字符串
	 */
	public static String toJsonStr(final Object obj) {
		return toJsonStr(obj, (JSONConfig) null);
	}

	/**
	 * 转换为JSON字符串
	 *
	 * @param obj        被转为JSON的对象
	 * @param jsonConfig JSON配置
	 * @return JSON字符串
	 * @since 5.7.12
	 */
	public static String toJsonStr(final Object obj, final JSONConfig jsonConfig) {
		if (null == obj) {
			return null;
		}
		return parse(obj, jsonConfig).toString();
	}

	/**
	 * 转换为JSON字符串并写出到writer
	 *
	 * @param obj    被转为JSON的对象
	 * @param appendable {@link Appendable}
	 * @since 5.3.3
	 */
	public static void toJsonStr(final Object obj, final Appendable appendable) {
		Assert.notNull(appendable);
		final JSONFactory jsonFactory = JSONFactory.of(null, null);
		final JSON json = jsonFactory.parse(obj);
		json.write(jsonFactory.ofWriter(appendable, 0));
	}

	/**
	 * 转换为XML字符串
	 *
	 * @param json JSON
	 * @return XML字符串
	 */
	public static String toXmlStr(final JSON json) {
		return JSONXMLUtil.toXml(json);
	}

	/**
	 * XML转JSONObject<br>
	 * 转换过程中一些信息可能会丢失，JSON中无法区分节点和属性，相同的节点将被处理为JSONArray。
	 *
	 * @param xml XML字符串
	 * @return JSONObject
	 * @since 4.0.8
	 */
	public static JSONObject xmlToJson(final String xml) {
		return JSONXMLUtil.toJSONObject(xml);
	}
	// endregion

	// region ----- toBean

	/**
	 * 转为实体类对象
	 *
	 * @param <T>   Bean类型
	 * @param json  JSONObject
	 * @param clazz 实体类
	 * @return 实体类对象
	 * @since 4.6.2
	 */
	public static <T> T toBean(final Object json, final Class<T> clazz) {
		Assert.notNull(clazz);
		return toBean(json, (Type) clazz);
	}

	/**
	 * 转为实体类对象
	 *
	 * @param <T>  Bean类型
	 * @param json JSONObject
	 * @param type 实体类对象类型
	 * @return 实体类对象
	 * @since 4.3.2
	 */
	public static <T> T toBean(final Object json, final Type type) {
		return toBean(json, null, type);
	}

	/**
	 * 转为实体类对象
	 *
	 * @param <T>    Bean类型
	 * @param obj    JSONObject
	 * @param config JSON配置
	 * @param type   实体类对象类型
	 * @return 实体类对象
	 * @since 4.3.2
	 */
	public static <T> T toBean(final Object obj, final JSONConfig config, Type type) {
		if (null == obj) {
			return null;
		}
		final JSON json = parse(obj, config);
		if (type instanceof TypeReference) {
			type = ((TypeReference<?>) type).getType();
		}
		return json.toBean(type);
	}
	// endregion

	// region ----- toList
	/**
	 * 将JSONArray字符串转换为Bean的List，默认为ArrayList
	 *
	 * @param <T>         Bean类型
	 * @param jsonArray   JSONArray字符串
	 * @param elementType List中元素类型
	 * @return List
	 * @since 5.5.2
	 */
	public static <T> List<T> toList(final String jsonArray, final Class<T> elementType) {
		return toList(parseArray(jsonArray), elementType);
	}

	/**
	 * 将JSONArray转换为Bean的List，默认为ArrayList
	 *
	 * @param <T>         Bean类型
	 * @param jsonArray   {@link JSONArray}
	 * @param elementType List中元素类型
	 * @return List
	 * @since 4.0.7
	 */
	public static <T> List<T> toList(final JSONArray jsonArray, final Class<T> elementType) {
		return null == jsonArray ? null : jsonArray.toList(elementType);
	}
	// endregion

	// region ----- getByPath

	/**
	 * 通过表达式获取JSON中嵌套的对象<br>
	 * <ol>
	 * <li>.表达式，可以获取Bean对象中的属性（字段）值或者Map中key对应的值</li>
	 * <li>[]表达式，可以获取集合等对象中对应index的值</li>
	 * </ol>
	 * <p>
	 * 表达式栗子：
	 *
	 * <pre>
	 * persion
	 * persion.name
	 * persons[3]
	 * person.friends[5].name
	 * </pre>
	 *
	 * @param <T>        值类型
	 * @param json       {@link JSON}
	 * @param expression 表达式
	 * @return 对象
	 * @see JSON#getByPath(String)
	 */
	public static <T> T getObjByPath(final JSON json, final String expression) {
		return getByPath(json, expression, Object.class);
	}

	/**
	 * 通过表达式获取JSON中嵌套的对象<br>
	 * <ol>
	 * <li>.表达式，可以获取Bean对象中的属性（字段）值或者Map中key对应的值</li>
	 * <li>[]表达式，可以获取集合等对象中对应index的值</li>
	 * </ol>
	 * <p>
	 * 表达式栗子：
	 *
	 * <pre>
	 * persion
	 * persion.name
	 * persons[3]
	 * person.friends[5].name
	 * </pre>
	 *
	 * @param <T>        值类型
	 * @param json       {@link JSON}
	 * @param expression 表达式
	 * @param type       结果类型
	 * @return 对象
	 * @see JSON#getByPath(String)
	 */
	public static <T> T getByPath(final JSON json, final String expression, final Type type) {
		if ((null == json || StrUtil.isBlank(expression))) {
			return null;
		}

		return json.getByPath(expression, type);
	}

	/**
	 * 通过表达式获取JSON中嵌套的对象<br>
	 * <ol>
	 * <li>.表达式，可以获取Bean对象中的属性（字段）值或者Map中key对应的值</li>
	 * <li>[]表达式，可以获取集合等对象中对应index的值</li>
	 * </ol>
	 * <p>
	 * 表达式栗子：
	 *
	 * <pre>
	 * persion
	 * persion.name
	 * persons[3]
	 * person.friends[5].name
	 * </pre>
	 *
	 * @param <T>          值类型
	 * @param json         {@link JSON}
	 * @param expression   表达式
	 * @param defaultValue 默认值
	 * @return 对象
	 * @see JSON#getByPath(String)
	 * @since 5.6.0
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getByPath(final JSON json, final String expression, final T defaultValue) {
		if ((null == json || StrUtil.isBlank(expression))) {
			return defaultValue;
		}

		if (null != defaultValue) {
			final Class<T> type = (Class<T>) defaultValue.getClass();
			return ObjUtil.defaultIfNull(json.getByPath(expression, type), defaultValue);
		}
		return (T) json.getByPath(expression);
	}

	/**
	 * 通过表达式获取JSON中嵌套的对象<br>
	 * <ol>
	 * <li>.表达式，可以获取Bean对象中的属性（字段）值或者Map中key对应的值</li>
	 * <li>[]表达式，可以获取集合等对象中对应index的值</li>
	 * </ol>
	 * <p>
	 * 表达式栗子：
	 *
	 * <pre>
	 * persion
	 * persion.name
	 * persons[3]
	 * person.friends[5].name
	 * </pre>
	 *
	 * @param json       {@link JSON}
	 * @param expression 表达式
	 * @return 对象
	 * @see JSON#getByPath(String)
	 */
	public static JSON getByPath(final JSON json, final String expression) {
		if ((null == json || StrUtil.isBlank(expression))) {
			return null;
		}

		return json.getByPath(expression);
	}
	// endregion

	/**
	 * 格式化JSON字符串，此方法并不严格检查JSON的格式正确与否
	 *
	 * @param jsonStr JSON字符串
	 * @return 格式化后的字符串
	 * @since 3.1.2
	 */
	public static String formatJsonStr(final String jsonStr) {
		return JSONStrFormatter.INSTANCE.format(jsonStr);
	}

	/**
	 * JSON对象是否为空，以下情况返回true<br>
	 * <ul>
	 *     <li>null</li>
	 *     <li>{@link JSON#isEmpty()}</li>
	 * </ul>
	 *
	 * @param json JSONObject或JSONArray
	 * @return 是否为空
	 */
	public static boolean isEmpty(final JSON json) {
		if (null == json) {
			return true;
		}
		return json.isEmpty();
	}

	// region ----- isType
	/**
	 * 是否为JSON类型字符串，首尾都为大括号或中括号判定为JSON字符串
	 *
	 * @param str 字符串
	 * @return 是否为JSON类型字符串
	 * @since 5.7.22
	 */
	public static boolean isTypeJSON(final String str) {
		return isTypeJSONObject(str) || isTypeJSONArray(str);
	}

	/**
	 * 是否为JSONObject类型字符串，首尾都为大括号判定为JSONObject字符串
	 *
	 * @param str 字符串
	 * @return 是否为JSON字符串
	 * @since 5.7.22
	 */
	public static boolean isTypeJSONObject(final String str) {
		if (StrUtil.isBlank(str)) {
			return false;
		}
		return StrUtil.isWrap(StrUtil.trim(str), '{', '}');
	}

	/**
	 * 是否为JSONArray类型的字符串，首尾都为中括号判定为JSONArray字符串
	 *
	 * @param str 字符串
	 * @return 是否为JSONArray类型字符串
	 * @since 5.7.22
	 */
	public static boolean isTypeJSONArray(final String str) {
		if (StrUtil.isBlank(str)) {
			return false;
		}
		return StrUtil.isWrap(StrUtil.trim(str), '[', ']');
	}
	// endregion
}
