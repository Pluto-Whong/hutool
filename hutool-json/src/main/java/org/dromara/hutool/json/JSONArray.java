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
import org.dromara.hutool.core.convert.ConvertUtil;
import org.dromara.hutool.core.convert.impl.ArrayConverter;
import org.dromara.hutool.core.lang.Validator;
import org.dromara.hutool.core.lang.mutable.Mutable;
import org.dromara.hutool.core.lang.mutable.MutableEntry;
import org.dromara.hutool.core.lang.mutable.MutableObj;
import org.dromara.hutool.core.text.StrJoiner;
import org.dromara.hutool.core.util.ObjUtil;
import org.dromara.hutool.json.mapper.JSONArrayMapper;
import org.dromara.hutool.json.mapper.JSONValueMapper;
import org.dromara.hutool.json.writer.JSONWriter;

import java.util.*;
import java.util.function.Predicate;

/**
 * JSON数组<br>
 * JSON数组是表示中括号括住的数据表现形式<br>
 * 对应的JSON字符串格格式例如:
 *
 * <pre>
 * ["a", "b", "c", 12]
 * </pre>
 *
 * @author looly
 */
public class JSONArray implements JSON, JSONGetter<Integer>, List<Object>, RandomAccess {
	private static final long serialVersionUID = 2664900568717612292L;

	/**
	 * 默认初始大小
	 */
	public static final int DEFAULT_CAPACITY = 10;

	/**
	 * 持有原始数据的List
	 */
	private List<Object> rawList;
	/**
	 * 配置项
	 */
	private JSONConfig config;
	/**
	 * 对象转换和包装，用于将Java对象和值转换为JSON值
	 */
	private JSONValueMapper valueMapper;

	// region Constructors

	/**
	 * 构造<br>
	 * 默认使用{@link ArrayList} 实现
	 */
	public JSONArray() {
		this(DEFAULT_CAPACITY);
	}

	/**
	 * 构造<br>
	 * 默认使用{@link ArrayList} 实现
	 *
	 * @param initialCapacity 初始大小
	 * @since 3.2.2
	 */
	public JSONArray(final int initialCapacity) {
		this(initialCapacity, JSONConfig.of());
	}

	/**
	 * 构造<br>
	 * 默认使用{@link ArrayList} 实现
	 *
	 * @param config JSON配置项
	 * @since 4.6.5
	 */
	public JSONArray(final JSONConfig config) {
		this(DEFAULT_CAPACITY, config);
	}

	/**
	 * 构造<br>
	 * 默认使用{@link ArrayList} 实现
	 *
	 * @param initialCapacity 初始大小
	 * @param config          JSON配置项
	 * @since 4.1.19
	 */
	public JSONArray(final int initialCapacity, final JSONConfig config) {
		this.rawList = new ArrayList<>(initialCapacity);
		this.config = ObjUtil.defaultIfNull(config, JSONConfig::of);
		this.valueMapper = JSONValueMapper.of(this.config);
	}

	/**
	 * 从对象构造，忽略{@code null}的值<br>
	 * 支持以下类型的参数：
	 *
	 * <pre>
	 * 1. 数组
	 * 2. {@link Iterable}对象
	 * 3. JSON数组字符串
	 * </pre>
	 *
	 * @param object 数组或集合或JSON数组字符串
	 * @throws JSONException 非数组或集合
	 */
	public JSONArray(final Object object) throws JSONException {
		this(object, JSONConfig.of());
	}

	/**
	 * 从对象构造<br>
	 * 支持以下类型的参数：
	 *
	 * <pre>
	 * 1. 数组
	 * 2. {@link Iterable}对象
	 * 3. JSON数组字符串
	 * </pre>
	 *
	 * @param object     数组或集合或JSON数组字符串
	 * @param jsonConfig JSON选项
	 * @throws JSONException 非数组或集合
	 * @since 4.6.5
	 */
	public JSONArray(final Object object, final JSONConfig jsonConfig) throws JSONException {
		this(object, jsonConfig, null);
	}

	/**
	 * 从对象构造<br>
	 * 支持以下类型的参数：
	 *
	 * <pre>
	 * 1. 数组
	 * 2. {@link Iterable}对象
	 * 3. JSON数组字符串
	 * </pre>
	 *
	 * @param object     数组或集合或JSON数组字符串
	 * @param jsonConfig JSON选项
	 * @param predicate  键值对过滤编辑器，可以通过实现此接口，完成解析前对值的过滤和修改操作，{@code null}表示不过滤，{@link Predicate#test(Object)}为{@code true}保留
	 * @throws JSONException 非数组或集合
	 * @since 5.8.0
	 */
	public JSONArray(final Object object, final JSONConfig jsonConfig, final Predicate<MutableEntry<Object, Object>> predicate) throws JSONException {
		this(DEFAULT_CAPACITY, jsonConfig);
		JSONArrayMapper.of(object, predicate).mapTo(this);
	}
	// endregion

	@Override
	public JSONConfig config() {
		return this.config;
	}

	/**
	 * 设置转为字符串时的日期格式，默认为时间戳（null值）
	 *
	 * @param format 格式，null表示使用时间戳
	 * @return this
	 * @since 4.1.19
	 */
	public JSONArray setDateFormat(final String format) {
		this.config.setDateFormat(format);
		return this;
	}

	/**
	 * JSONArray转为以{@code separator}为分界符的字符串
	 *
	 * @param separator 分界符
	 * @return a string.
	 * @throws JSONException If the array contains an invalid number.
	 */
	public String join(final String separator) throws JSONException {
		return StrJoiner.of(separator)
				.append(this, InternalJSONUtil::valueToString).toString();
	}

	@Override
	public Object get(final int index) {
		Object value = this.rawList.get(index);
		if(value instanceof JSONPrimitive){
			value = ((JSONPrimitive) value).getValue();
		}
		return value;
	}

	@Override
	public Object getObj(final Integer index, final Object defaultValue) {
		return (index < 0 || index >= this.size()) ? defaultValue : this.rawList.get(index);
	}

	/**
	 * Append an object value. This increases the array's length by one. <br>
	 * 加入元素，数组长度+1，等同于 {@link JSONArray#add(Object)}
	 *
	 * @param value 值，可以是： Boolean, Double, Integer, JSONArray, JSONObject, Long, or String, or the JSONNull.NULL。
	 * @return this.
	 * @see #set(Object)
	 */
	public JSONArray put(final Object value) {
		return set(value);
	}

	/**
	 * Append an object value. This increases the array's length by one. <br>
	 * 加入元素，数组长度+1，等同于 {@link JSONArray#add(Object)}
	 *
	 * @param value 值，可以是： Boolean, Double, Integer, JSONArray, JSONObject, Long, or String, or the JSONNull.NULL。
	 * @return this.
	 * @since 5.2.5
	 */
	public JSONArray set(final Object value) {
		this.add(value);
		return this;
	}

	/**
	 * 加入或者替换JSONArray中指定Index的值，如果index大于JSONArray的长度，将在指定index设置值，之前的位置填充JSONNull.Null
	 *
	 * @param index 位置
	 * @param value 值对象. 可以是以下类型: Boolean, Double, Integer, JSONArray, JSONObject, Long, String, or the JSONNull.NULL.
	 * @return this.
	 * @throws JSONException index &lt; 0 或者非有限的数字
	 * @see #set(int, Object)
	 */
	public JSONArray put(final int index, final Object value) throws JSONException {
		this.set(index, value);
		return this;
	}

	/**
	 * 根据给定名列表，与其位置对应的值组成JSONObject
	 *
	 * @param names 名列表，位置与JSONArray中的值位置对应
	 * @return A JSONObject，无名或值返回null
	 * @throws JSONException 如果任何一个名为null
	 */
	public JSONObject toJSONObject(final JSONArray names) throws JSONException {
		if (names == null || names.size() == 0 || this.size() == 0) {
			return null;
		}
		final JSONObject jo = new JSONObject(this.config);
		for (int i = 0; i < names.size(); i += 1) {
			jo.set(names.getStr(i), this.getObj(i));
		}
		return jo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rawList == null) ? 0 : rawList.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final JSONArray other = (JSONArray) obj;
		if (rawList == null) {
			return other.rawList == null;
		} else {
			return rawList.equals(other.rawList);
		}
	}

	@Override
	public Iterator<Object> iterator() {
		return rawList.iterator();
	}

	/**
	 * 当此JSON列表的每个元素都是一个JSONObject时，可以调用此方法返回一个Iterable，便于使用foreach语法遍历
	 *
	 * @return Iterable
	 * @since 4.0.12
	 * @param <T> JSON类型
	 * @param type JSON类型
	 */
	public <T extends JSON> Iterable<T> jsonIter(final Class<T> type) {
		final Iterator<Object> iterator = iterator();
		return () -> new Iterator<T>() {
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public T next() {
				return type.cast(iterator.next());
			}
			@Override
			public void remove() {
				iterator.remove();
			}
		};
	}

	@Override
	public int size() {
		return rawList.size();
	}

	@Override
	public boolean isEmpty() {
		return rawList.isEmpty();
	}

	@Override
	public boolean contains(final Object o) {
		return rawList.contains(o);
	}

	@Override
	public Object[] toArray() {
		return rawList.toArray();
	}

	@Override
	@SuppressWarnings({"unchecked"})
	public <T> T[] toArray(final T[] a) {
		return (T[]) ArrayConverter.INSTANCE.convert(a.getClass().getComponentType(), this);
	}

	@Override
	public boolean add(final Object e) {
		return add(e, null);
	}

	/**
	 * 增加元素
	 *
	 * @param e         元素对象，自动根据对象类型转换为JSON中的对象
	 * @param predicate 键值对过滤编辑器，可以通过实现此接口，完成解析前对值的过滤和修改操作，{@code null}表示不过滤，{@link Predicate#test(Object)}为{@code true}保留
	 * @return 是否加入成功
	 */
	public boolean add(final Object e, final Predicate<Mutable<Object>> predicate) {
		return addRaw(valueMapper.map(e), predicate);
	}

	@Override
	public Object remove(final int index) {
		return index >= 0 && index < this.size() ? this.rawList.remove(index) : null;
	}

	@Override
	public boolean remove(final Object o) {
		return rawList.remove(o);
	}

	@SuppressWarnings({"NullableProblems", "SlowListContainsAll"})
	@Override
	public boolean containsAll(final Collection<?> c) {
		return rawList.containsAll(c);
	}

	@Override
	public boolean addAll(final Collection<?> c) {
		if (CollUtil.isEmpty(c)) {
			return false;
		}
		for (final Object obj : c) {
			this.add(obj);
		}
		return true;
	}

	@Override
	public boolean addAll(final int index, final Collection<?> c) {
		if (CollUtil.isEmpty(c)) {
			return false;
		}
		final ArrayList<Object> list = new ArrayList<>(c.size());
		for (final Object object : c) {
			if (null == object && config.isIgnoreNullValue()) {
				continue;
			}
			this.add(index);
			list.add(valueMapper.map(object));
		}
		return rawList.addAll(index, list);
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		return this.rawList.removeAll(c);
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		return this.rawList.retainAll(c);
	}

	@Override
	public void clear() {
		this.rawList.clear();

	}

	/**
	 * 加入或者替换JSONArray中指定Index的值，如果index大于JSONArray的长度，将在指定index设置值，之前的位置填充JSONNull.Null
	 *
	 * @param index   位置
	 * @param element 值对象. 可以是以下类型: Boolean, Double, Integer, JSONArray, JSONObject, Long, String, or the JSONNull.NULL.
	 * @return 替换的值，即之前的值
	 */
	@Override
	public Object set(final int index, final Object element) {
		return set(index, element, null);
	}

	/**
	 * 加入或者替换JSONArray中指定Index的值，如果index大于JSONArray的长度，将在指定index设置值，之前的位置填充JSONNull.Null
	 *
	 * @param index   位置
	 * @param element 值对象. 可以是以下类型: Boolean, Double, Integer, JSONArray, JSONObject, Long, String, or the JSONNull.NULL.
	 * @param filter  过滤器，可以修改值，key（index）无法修改，{@link Predicate#test(Object)}为{@code true}保留，null表示全部保留。
	 * @return 替换的值，即之前的值
	 * @since 5.8.0
	 */
	public Object set(final int index, Object element, final Predicate<MutableEntry<Integer, Object>> filter) {
		// 添加前置过滤，通过MutablePair实现过滤、修改键值对等
		if (null != filter) {
			final MutableEntry<Integer, Object> pair = new MutableEntry<>(index, element);
			if (filter.test(pair)) {
				// 使用修改后的值
				element = pair.getValue();
			}
		}

		// 越界则追加到指定位置
		if (index >= size()) {
			add(index, element);
			return null;
		}
		if (null == element && config.isIgnoreNullValue()) {
			return null;
		}
		return this.rawList.set(index, valueMapper.map(element));
	}

	@Override
	public void add(int index, final Object element) {
		if (null == element && config.isIgnoreNullValue()) {
			return;
		}
		if (index < this.size()) {
			if (index < 0) {
				index = 0;
			}
			this.rawList.add(index, valueMapper.map(element));
		} else {
			// issue#3286, 如果用户指定的index太大，容易造成Java heap space错误。
			if (!config.isIgnoreNullValue()) {
				// issue#3286, 增加安全检查，最多增加10倍
				Validator.checkIndexLimit(index, (this.size() + 1) * 10);
				while (index != this.size()) {
					// 非末尾，则填充null
					this.add(null);
				}
			}
			this.add(element);
		}

	}

	@Override
	public int indexOf(final Object o) {
		return this.rawList.indexOf(o);
	}

	@Override
	public int lastIndexOf(final Object o) {
		return this.rawList.lastIndexOf(o);
	}

	@Override
	public ListIterator<Object> listIterator() {
		return this.rawList.listIterator();
	}

	@Override
	public ListIterator<Object> listIterator(final int index) {
		return this.rawList.listIterator(index);
	}

	@Override
	public List<Object> subList(final int fromIndex, final int toIndex) {
		return this.rawList.subList(fromIndex, toIndex);
	}

	/**
	 * 转为Bean数组
	 *
	 * @param arrayClass 数组元素类型
	 * @return 实体类对象
	 */
	public Object toArray(final Class<?> arrayClass) {
		return ArrayConverter.INSTANCE.convert(arrayClass, this);
	}

	/**
	 * 转为{@link ArrayList}
	 *
	 * @param <T>         元素类型
	 * @param elementType 元素类型
	 * @return {@link ArrayList}
	 * @since 3.0.8
	 */
	public <T> List<T> toList(final Class<T> elementType) {
		return ConvertUtil.toList(elementType, this);
	}

	/**
	 * 转为JSON字符串，无缩进
	 *
	 * @return JSONArray字符串
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
	 * @param predicate    过滤器，可以修改值，key（index）无法修改，{@link Predicate#test(Object)}为{@code true}保留
	 * @return JSON字符串
	 * @since 5.7.15
	 */
	public String toJSONString(final int indentFactor, final Predicate<MutableEntry<Object, Object>> predicate) {
		final JSONWriter jsonWriter = JSONWriter.of(new StringBuilder(), indentFactor, 0, this.config).setPredicate(predicate);
		this.write(jsonWriter);
		return jsonWriter.toString();
	}

	@Override
	public void write(final JSONWriter writer) throws JSONException {
		final JSONWriter copyWriter = writer.copyOfSub();
		copyWriter.beginArray();
		CollUtil.forEach(this, (value, index) -> copyWriter.writeField(new MutableEntry<>(index, value)));
		copyWriter.end();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		final JSONArray clone = (JSONArray) super.clone();
		clone.config = this.config;
		clone.valueMapper = this.valueMapper;
		clone.rawList = ObjUtil.clone(this.rawList);
		return clone;
	}

	/**
	 * 原始添加，添加的对象不做任何处理
	 *
	 * @param obj       添加的对象
	 * @param predicate 键值对过滤编辑器，可以通过实现此接口，完成解析前对值的过滤和修改操作，{@code null}表示不过滤，{@link Predicate#test(Object)}为{@code true}保留
	 * @return 是否加入成功
	 * @since 5.8.0
	 */
	protected boolean addRaw(Object obj, final Predicate<Mutable<Object>> predicate) {
		// 添加前置过滤，通过MutablePair实现过滤、修改键值对等
		if (null != predicate) {
			final Mutable<Object> mutable = new MutableObj<>(obj);
			if (predicate.test(mutable)) {
				// 使用修改后的值
				obj = mutable.get();
			} else {
				// 键值对被过滤
				return false;
			}
		}
		if (null == obj && config.isIgnoreNullValue()) {
			// 忽略空则不添加
			return false;
		}
		return this.rawList.add(obj);
	}
}
