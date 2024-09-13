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

import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.func.LambdaInfo;
import org.dromara.hutool.core.func.LambdaUtil;
import org.dromara.hutool.core.func.SerFunction;
import org.dromara.hutool.core.func.SerSupplier;
import org.dromara.hutool.core.lang.mutable.MutableEntry;
import org.dromara.hutool.core.map.MapUtil;
import org.dromara.hutool.core.map.MapWrapper;
import org.dromara.hutool.core.util.ObjUtil;
import org.dromara.hutool.json.mapper.JSONObjectMapper;
import org.dromara.hutool.json.mapper.JSONValueMapper;
import org.dromara.hutool.json.writer.JSONWriter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

/**
 * JSON对象<br>
 * 例：<br>
 *
 * <pre>
 * json = new JSONObject().put(&quot;JSON&quot;, &quot;Hello, World!&quot;).toString();
 * </pre>
 *
 * @author looly
 */
public class OldJSONObject extends MapWrapper<String, Object> implements JSON, JSONGetter<String> {
	private static final long serialVersionUID = -330220388580734346L;

	/**
	 * 默认初始大小
	 */
	public static final int DEFAULT_CAPACITY = MapUtil.DEFAULT_INITIAL_CAPACITY;

	/**
	 * 配置项
	 */
	private JSONConfig config;
	/**
	 * 对象转换和包装，用于将Java对象和值转换为JSON值
	 */
	private JSONValueMapper valueMapper;

	// -------------------------------------------------------------------------------------------------------------------- Constructor start

	/**
	 * 构造，初始容量为 {@link #DEFAULT_CAPACITY}，KEY有序
	 */
	public OldJSONObject() {
		this(JSONConfig.of());
	}

	/**
	 * 构造
	 *
	 * @param config JSON配置项
	 * @since 4.6.5
	 */
	public OldJSONObject(final JSONConfig config) {
		this(DEFAULT_CAPACITY, config);
	}

	/**
	 * 构造
	 *
	 * @param capacity 初始大小
	 * @param config   JSON配置项，{@code null}则使用默认配置
	 * @since 4.1.19
	 */
	public OldJSONObject(final int capacity, final JSONConfig config) {
		super(InternalJSONUtil.createRawMapOld(capacity, ObjUtil.defaultIfNull(config, JSONConfig.of())));
		this.config = ObjUtil.defaultIfNull(config, JSONConfig.of());
		this.valueMapper = JSONValueMapper.of(this.config);
	}

	/**
	 * 构建JSONObject，JavaBean默认忽略null值，其它对象不忽略，规则如下：
	 * <ol>
	 * <li>value为Map，将键值对加入JSON对象</li>
	 * <li>value为JSON字符串（CharSequence），使用JSONTokener解析</li>
	 * <li>value为JSONTokener，直接解析</li>
	 * <li>value为普通JavaBean，如果为普通的JavaBean，调用其getters方法（getXXX或者isXXX）获得值，加入到JSON对象。
	 * 例如：如果JavaBean对象中有个方法getName()，值为"张三"，获得的键值对为：name: "张三"</li>
	 * </ol>
	 *
	 * @param source JavaBean或者Map对象或者String
	 */
	public OldJSONObject(final Object source) {
		this(source, JSONConfig.of().setIgnoreNullValue(InternalJSONUtil.defaultIgnoreNullValue(source)));
	}

	/**
	 * 构建JSONObject，规则如下：
	 * <ol>
	 * <li>value为Map，将键值对加入JSON对象</li>
	 * <li>value为JSON字符串（CharSequence），使用JSONTokener解析</li>
	 * <li>value为JSONTokener，直接解析</li>
	 * <li>value为普通JavaBean，如果为普通的JavaBean，调用其getters方法（getXXX或者isXXX）获得值，加入到JSON对象。例如：如果JavaBean对象中有个方法getName()，值为"张三"，获得的键值对为：name: "张三"</li>
	 * </ol>
	 * <p>
	 * 如果给定值为Map，将键值对加入JSON对象;<br>
	 * 如果为普通的JavaBean，调用其getters方法（getXXX或者isXXX）获得值，加入到JSON对象<br>
	 * 例如：如果JavaBean对象中有个方法getName()，值为"张三"，获得的键值对为：name: "张三"
	 *
	 * @param source JavaBean或者Map对象或者String
	 * @param config JSON配置文件，{@code null}则使用默认配置
	 * @since 4.2.2
	 */
	public OldJSONObject(final Object source, final JSONConfig config) {
		this(source, config, null);
	}

	/**
	 * 构建JSONObject，规则如下：
	 * <ol>
	 * <li>value为Map，将键值对加入JSON对象</li>
	 * <li>value为JSON字符串（CharSequence），使用JSONTokener解析</li>
	 * <li>value为JSONTokener，直接解析</li>
	 * <li>value为普通JavaBean，如果为普通的JavaBean，调用其getters方法（getXXX或者isXXX）获得值，加入到JSON对象。例如：如果JavaBean对象中有个方法getName()，值为"张三"，获得的键值对为：name: "张三"</li>
	 * </ol>
	 * <p>
	 * 如果给定值为Map，将键值对加入JSON对象;<br>
	 * 如果为普通的JavaBean，调用其getters方法（getXXX或者isXXX）获得值，加入到JSON对象<br>
	 * 例如：如果JavaBean对象中有个方法getName()，值为"张三"，获得的键值对为：name: "张三"
	 *
	 * @param source    JavaBean或者Map对象或者String
	 * @param config    JSON配置文件，{@code null}则使用默认配置
	 * @param predicate 键值对过滤编辑器，可以通过实现此接口，完成解析前对键值对的过滤和修改操作，{@code null}表示不过滤，{@link Predicate#test(Object)}为{@code true}保留
	 * @since 5.8.0
	 */
	public OldJSONObject(final Object source, final JSONConfig config, final Predicate<MutableEntry<Object, Object>> predicate) {
		this(DEFAULT_CAPACITY, config);
		JSONObjectMapper.of(source, predicate).mapTo(this);
	}
	// -------------------------------------------------------------------------------------------------------------------- Constructor end

	@Override
	public JSONConfig config() {
		return this.config;
	}

	/**
	 * 设置转为字符串时的日期格式，默认为时间戳（null值）<br>
	 * 此方法设置的日期格式仅对转换为JSON字符串有效，对解析JSON为bean无效。
	 *
	 * @param format 格式，null表示使用时间戳
	 * @return this
	 * @since 4.1.19
	 */
	public OldJSONObject setDateFormat(final String format) {
		this.config.setDateFormat(format);
		return this;
	}

	/**
	 * 将指定KEY列表的值组成新的JSONArray
	 *
	 * @param names KEY列表
	 * @return A JSONArray of values.
	 * @throws JSONException If any of the values are non-finite numbers.
	 */
	public JSONArray toJSONArray(final Collection<String> names) throws JSONException {
		if (CollUtil.isEmpty(names)) {
			return null;
		}
		final JSONArray ja = new JSONArray(this.config);
		Object value;
		for (final String name : names) {
			value = this.get(name);
			if (null != value) {
				ja.set(value);
			}
		}
		return ja;
	}

	@Override
	public Object getObj(final String key, final Object defaultValue) {
		return getOrDefault(key, defaultValue);
	}

	@Override
	public Object get(final Object key) {
		Object value = super.get(key);
		if(value instanceof JSONPrimitive){
			value = ((JSONPrimitive) value).getValue();
		}
		return value;
	}

	@Override
	public Object getOrDefault(final Object key, final Object defaultValue) {
		Object value = super.getOrDefault(key, defaultValue);
		if(value instanceof JSONPrimitive){
			value = ((JSONPrimitive) value).getValue();
		}
		return value;
	}

	/**
	 * 根据lambda的方法引用，获取
	 *
	 * @param func 方法引用
	 * @param <P>  参数类型
	 * @param <T>  返回值类型
	 * @return 获取表达式对应属性和返回的对象
	 */
	public <P, T> T get(final SerFunction<P, T> func) {
		final LambdaInfo lambdaInfo = LambdaUtil.resolve(func);
		return get(lambdaInfo.getFieldName(), lambdaInfo.getReturnType());
	}

	/**
	 * PUT 键值对到JSONObject中，在忽略null模式下，如果值为{@code null}，将此键移除
	 *
	 * @param key   键
	 * @param value 值对象. 可以是以下类型: Boolean, Double, Integer, JSONArray, JSONObject, Long, String, or the JSONNull.NULL.
	 * @return this.
	 * @throws JSONException 值是无穷数字抛出此异常
	 */
	@Override
	public Object put(final String key, final Object value) throws JSONException {
		return put(key, value, null, config().isCheckDuplicate());
	}

	/**
	 * 设置键值对到JSONObject中，在忽略null模式下，如果值为{@code null}，将此键移除
	 *
	 * @param key   键
	 * @param value 值对象. 可以是以下类型: Boolean, Double, Integer, JSONArray, JSONObject, Long, String, or the JSONNull.NULL.
	 * @return this.
	 * @throws JSONException 值是无穷数字抛出此异常
	 */
	public OldJSONObject set(final String key, final Object value) throws JSONException {
		set(key, value, null);
		return this;
	}

	/**
	 * 通过lambda批量设置值<br>
	 * 实际使用时，可以使用getXXX的方法引用来完成键值对的赋值：
	 * <pre>
	 *     User user = GenericBuilder.of(User::new).with(User::setUsername, "hutool").build();
	 *     (new JSONObject()).setFields(user::getNickname, user::getUsername);
	 * </pre>
	 *
	 * @param fields lambda,不能为空
	 * @return this
	 */
	public OldJSONObject setFields(final SerSupplier<?>... fields) {
		Arrays.stream(fields).forEach(f -> set(LambdaUtil.getFieldName(f), f.get()));
		return this;
	}

	/**
	 * 一次性Put 键值对，如果key已经存在抛出异常，如果键值中有null值，忽略
	 *
	 * @param key       键
	 * @param value     值对象，可以是以下类型: Boolean, Double, Integer, JSONArray, JSONObject, Long, String, or the JSONNull.NULL.
	 * @param predicate 键值对过滤编辑器，可以通过实现此接口，完成解析前对键值对的过滤和修改操作，{@code null}表示不过滤，{@link Predicate#test(Object)}为{@code true}保留
	 * @return this
	 * @throws JSONException 值是无穷数字、键重复抛出异常
	 * @since 5.8.0
	 */
	public OldJSONObject set(final String key, final Object value, final Predicate<MutableEntry<Object, Object>> predicate) throws JSONException {
		put(key, value, predicate, config().isCheckDuplicate());
		return this;
	}

	/**
	 * 设置键值对到JSONObject中，在忽略null模式下，如果值为{@code null}，将此键移除
	 *
	 * @param key            键
	 * @param value          值对象. 可以是以下类型: Boolean, Double, Integer, JSONArray, JSONObject, Long, String, or the JSONNull.NULL.
	 * @param predicate      键值对过滤编辑器，可以通过实现此接口，完成解析前对键值对的过滤和修改操作，{@code null}表示不过滤，{@link Predicate#test(Object)}为{@code true}保留
	 * @param checkDuplicate 是否检查重复键，如果为{@code true}，则出现重复键时抛出{@link JSONException}异常
	 * @return this.
	 * @throws JSONException 值是无穷数字抛出此异常
	 * @since 5.8.0
	 */
	public OldJSONObject set(final String key, final Object value, final Predicate<MutableEntry<Object, Object>> predicate, final boolean checkDuplicate) throws JSONException {
		put(key, value, predicate, checkDuplicate);
		return this;
	}

	/**
	 * 在键和值都为非空的情况下put到JSONObject中
	 *
	 * @param key   键
	 * @param value 值对象，可以是以下类型: Boolean, Double, Integer, JSONArray, JSONObject, Long, String, or the JSONNull.NULL.
	 * @return this.
	 * @throws JSONException 值是无穷数字
	 */
	public OldJSONObject setOpt(final String key, final Object value) throws JSONException {
		if (key != null && value != null) {
			this.set(key, value);
		}
		return this;
	}

	@Override
	public void putAll(final Map<? extends String, ?> m) {
		for (final Entry<? extends String, ?> entry : m.entrySet()) {
			this.set(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * 追加值.
	 * <ul>
	 *     <li>如果键值对不存在或对应值为{@code null}，则value为单独值</li>
	 *     <li>如果值是一个{@link JSONArray}，追加之</li>
	 *     <li>如果值是一个其他值，则和旧值共同组合为一个{@link JSONArray}</li>
	 * </ul>
	 *
	 * @param key   键
	 * @param value 值
	 * @return this.
	 * @throws JSONException 如果给定键为{@code null}或者键对应的值存在且为非JSONArray
	 */
	public OldJSONObject append(final String key, final Object value) throws JSONException {
		return append(key, value, null);
	}

	/**
	 * 追加值.
	 * <ul>
	 *     <li>如果键值对不存在或对应值为{@code null}，则value为单独值</li>
	 *     <li>如果值是一个{@link JSONArray}，追加之</li>
	 *     <li>如果值是一个其他值，则和旧值共同组合为一个{@link JSONArray}</li>
	 * </ul>
	 *
	 * @param key       键
	 * @param value     值
	 * @param predicate 键值对过滤编辑器，可以通过实现此接口，完成解析前对键值对的过滤和修改操作，{@code null}表示不过滤，{@link Predicate#test(Object)}为{@code true}保留
	 * @return this.
	 * @throws JSONException 如果给定键为{@code null}或者键对应的值存在且为非JSONArray
	 * @since 6.0.0
	 */
	public OldJSONObject append(String key, Object value, final Predicate<MutableEntry<Object, Object>> predicate) throws JSONException {
		// 添加前置过滤，通过MutablePair实现过滤、修改键值对等
		if (null != predicate) {
			final MutableEntry<Object, Object> pair = new MutableEntry<>(key, value);
			if (predicate.test(pair)) {
				// 使用修改后的键值对
				key = (String) pair.getKey();
				value = pair.getValue();
			} else {
				// 键值对被过滤
				return this;
			}
		}

		final Object object = this.getObj(key);
		if (object == null) {
			this.set(key, value);
		} else if (object instanceof JSONArray) {
			((JSONArray) object).set(value);
		} else {
			this.set(key, JSONUtil.ofArray(this.config).set(object).set(value));
		}
		return this;
	}

	/**
	 * 对值加一，如果值不存在，赋值1，如果为数字类型，做加一操作
	 *
	 * @param key A key string.
	 * @return this.
	 * @throws JSONException 如果存在值非Integer, Long, Double, 或 Float.
	 */
	public OldJSONObject increment(final String key) throws JSONException {
		final Object value = this.getObj(key);
		if (value == null) {
			this.set(key, 1);
		} else if (value instanceof BigInteger) {
			this.set(key, ((BigInteger) value).add(BigInteger.ONE));
		} else if (value instanceof BigDecimal) {
			this.set(key, ((BigDecimal) value).add(BigDecimal.ONE));
		} else if (value instanceof Integer) {
			this.set(key, (Integer) value + 1);
		} else if (value instanceof Long) {
			this.set(key, (Long) value + 1);
		} else if (value instanceof Double) {
			this.set(key, (Double) value + 1);
		} else if (value instanceof Float) {
			this.set(key, (Float) value + 1);
		} else {
			throw new JSONException("Unable to increment [" + InternalJSONUtil.quote(key) + "].");
		}
		return this;
	}

	/**
	 * 返回JSON字符串<br>
	 * 如果解析错误，返回{@code null}
	 *
	 * @return JSON字符串
	 */
	@Override
	public String toString() {
		return this.toJSONString(0);
	}

	/**
	 * 返回JSON字符串<br>
	 * 支持过滤器，即选择哪些字段或值不写出
	 *
	 * @param indentFactor 每层缩进空格数
	 * @param predicate    过滤器，同时可以修改编辑键和值，{@link Predicate#test(Object)}为{@code true}保留
	 * @return JSON字符串
	 * @since 5.7.15
	 */
	public String toJSONString(final int indentFactor, final Predicate<MutableEntry<Object, Object>> predicate) {
		final JSONWriter jsonWriter = JSONWriter.of(new StringBuilder(), indentFactor, 0, config).setPredicate(predicate);
		write(jsonWriter);
		return jsonWriter.toString();
	}

	@Override
	public void write(final JSONWriter writer) throws JSONException {
		final JSONWriter copyWriter = writer.copyOf();
		copyWriter.beginObj();
		this.forEach((key, value) -> copyWriter.writeField(new MutableEntry<>(key, value)));
		copyWriter.end();
	}

	@Override
	public OldJSONObject clone() throws CloneNotSupportedException {
		final OldJSONObject clone = (OldJSONObject) super.clone();
		clone.config = this.config;
		clone.valueMapper = this.valueMapper;
		return clone;
	}

	/**
	 * 设置键值对到JSONObject中，在忽略null模式下，如果值为{@code null}，将此键移除
	 *
	 * @param key            键
	 * @param value          值对象. 可以是以下类型: Boolean, Double, Integer, JSONArray, JSONObject, Long, String, or the JSONNull.NULL.
	 * @param predicate      键值对过滤编辑器，可以通过实现此接口，完成解析前对键值对的过滤和修改操作，{@code null}表示不过滤，{@link Predicate#test(Object)}为{@code true}保留
	 * @param checkDuplicate 是否检查重复键，如果为{@code true}，则出现重复键时抛出{@link JSONException}异常
	 * @return 旧值
	 * @throws JSONException 值是无穷数字抛出此异常
	 * @since 5.8.0
	 */
	private Object put(String key, Object value, final Predicate<MutableEntry<Object, Object>> predicate, final boolean checkDuplicate) throws JSONException {
		if (null == key) {
			return null;
		}

		// 添加前置过滤，通过MutablePair实现过滤、修改键值对等
		if (null != predicate) {
			final MutableEntry<Object, Object> pair = new MutableEntry<>(key, value);
			if (predicate.test(pair)) {
				// 使用修改后的键值对
				key = (String) pair.getKey();
				value = pair.getValue();
			} else {
				// 键值对被过滤
				return null;
			}
		}

		final boolean ignoreNullValue = this.config.isIgnoreNullValue();
		if (null == value && ignoreNullValue) {
			// 忽略值模式下如果值为空清除key
			return this.remove(key);
		} else if (checkDuplicate && containsKey(key)) {
			throw new JSONException("Duplicate key \"{}\"", key);
		}
		return super.put(key, this.valueMapper.map(value));
	}
}
