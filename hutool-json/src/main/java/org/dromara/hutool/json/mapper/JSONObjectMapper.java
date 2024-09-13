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

package org.dromara.hutool.json.mapper;

import org.dromara.hutool.core.bean.BeanUtil;
import org.dromara.hutool.core.bean.RecordUtil;
import org.dromara.hutool.core.bean.copier.CopyOptions;
import org.dromara.hutool.core.convert.ConvertUtil;
import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.core.lang.mutable.MutableEntry;
import org.dromara.hutool.core.reflect.method.MethodUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.json.*;
import org.dromara.hutool.json.reader.JSONParser;
import org.dromara.hutool.json.reader.JSONTokener;
import org.dromara.hutool.json.serializer.JSONSerializer;
import org.dromara.hutool.json.serializer.SerializerManager;
import org.dromara.hutool.json.serializer.SimpleJSONContext;
import org.dromara.hutool.json.xml.JSONXMLParser;
import org.dromara.hutool.json.xml.ParseConfig;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * 对象和JSONObject映射器，用于转换对象为JSONObject，支持：
 * <ul>
 *     <li>Map 转 JSONObject，将键值对加入JSON对象</li>
 *     <li>Map.Entry 转 JSONObject</li>
 *     <li>CharSequence 转 JSONObject，使用JSONTokener解析</li>
 *     <li>{@link Reader} 转 JSONObject，使用JSONTokener解析</li>
 *     <li>{@link InputStream} 转 JSONObject，使用JSONTokener解析</li>
 *     <li>JSONTokener 转 JSONObject，直接解析</li>
 *     <li>ResourceBundle 转 JSONObject</li>
 *     <li>Bean 转 JSONObject，调用其getters方法（getXXX或者isXXX）获得值，加入到JSON对象。例如：如果JavaBean对象中有个方法getName()，值为"张三"，获得的键值对为：name: "张三"</li>
 * </ul>
 *
 * @author looly
 * @since 5.8.0
 */
public class JSONObjectMapper {

	/**
	 * 创建ObjectMapper
	 *
	 * @param source    来源对象
	 * @param predicate 键值对过滤编辑器，可以通过实现此接口，完成解析前对键值对的过滤和修改操作，{@link Predicate#test(Object)}为{@code true}保留
	 * @return ObjectMapper
	 */
	public static JSONObjectMapper of(final Object source, final Predicate<MutableEntry<Object, Object>> predicate) {
		return new JSONObjectMapper(source, predicate);
	}

	private final Object source;
	private final Predicate<MutableEntry<Object, Object>> predicate;

	/**
	 * 构造
	 *
	 * @param source    来源对象
	 * @param predicate 键值对过滤编辑器，可以通过实现此接口，完成解析前对键值对的过滤和修改操作，{@link Predicate#test(Object)}为{@code true}保留
	 */
	public JSONObjectMapper(final Object source, final Predicate<MutableEntry<Object, Object>> predicate) {
		this.source = source;
		this.predicate = predicate;
	}

	/**
	 * 将给定对象转换为{@link OldJSONObject}
	 *
	 * @param jsonObject 目标{@link OldJSONObject}
	 */
	@SuppressWarnings("rawtypes")
	public void mapTo(final OldJSONObject jsonObject) {
		final Object source = this.source;
		if (null == source) {
			return;
		}

		// 自定义序列化
		final JSONSerializer<Object> serializer = SerializerManager.getInstance().getSerializer(source.getClass());
		if (null != serializer) {
			serializer.serialize(source, new SimpleJSONContext(jsonObject));
			return;
		}

		if (source instanceof JSONArray) {
			// 不支持集合类型转换为JSONObject
			throw new JSONException("Unsupported type [{}] to JSONObject!", source.getClass());
		}

		if (source instanceof JSONTokener) {
			// JSONTokener
			mapFromTokener((JSONTokener) source, jsonObject.config(), jsonObject);
		}if (source instanceof JSONParser) {
			// JSONParser
			((JSONParser) source).parseTo(jsonObject);
		} else if (source instanceof Map) {
			// Map
			for (final Map.Entry<?, ?> e : ((Map<?, ?>) source).entrySet()) {
				jsonObject.set(ConvertUtil.toStr(e.getKey()), e.getValue(), predicate, false);
			}
		} else if (source instanceof Map.Entry) {
			final Map.Entry entry = (Map.Entry) source;
			jsonObject.set(ConvertUtil.toStr(entry.getKey()), entry.getValue(), predicate, false);
		} else if (source instanceof CharSequence) {
			// 可能为JSON字符串
			mapFromStr((CharSequence) source, jsonObject);
		} else if (source instanceof Reader) {
			mapFromTokener(new JSONTokener((Reader) source), jsonObject.config(), jsonObject);
		} else if (source instanceof InputStream) {
			mapFromTokener(new JSONTokener((InputStream) source), jsonObject.config(), jsonObject);
		} else if (source instanceof byte[]) {
			mapFromTokener(new JSONTokener(IoUtil.toStream((byte[]) source)), jsonObject.config(), jsonObject);
		} else if (source instanceof ResourceBundle) {
			// ResourceBundle
			mapFromResourceBundle((ResourceBundle) source, jsonObject);
		} else if (RecordUtil.isRecord(source.getClass())) {
			// since 6.0.0
			mapFromRecord(source, jsonObject);
		} else if (BeanUtil.isReadableBean(source.getClass())) {
			// 普通Bean
			mapFromBean(source, jsonObject);
		} else {
			if (!jsonObject.config().isIgnoreError()) {
				// 不支持对象类型转换为JSONObject
				throw new JSONException("Unsupported type [{}] to JSONObject!", source.getClass());
			}
		}
		// 如果用户选择跳过异常，则跳过此值转换
	}

	/**
	 * 从{@link ResourceBundle}转换
	 *
	 * @param bundle     ResourceBundle
	 * @param jsonObject {@link OldJSONObject}
	 * @since 5.3.1
	 */
	private void mapFromResourceBundle(final ResourceBundle bundle, final OldJSONObject jsonObject) {
		final Enumeration<String> keys = bundle.getKeys();
		while (keys.hasMoreElements()) {
			final String key = keys.nextElement();
			if (key != null) {
				InternalJSONUtil.propertyPut(jsonObject, key, bundle.getString(key), this.predicate);
			}
		}
	}

	/**
	 * 从字符串转换
	 *
	 * @param source     JSON字符串
	 * @param jsonObject {@link OldJSONObject}
	 */
	private void mapFromStr(final CharSequence source, final OldJSONObject jsonObject) {
		final String jsonStr = StrUtil.trim(source);
		if (StrUtil.startWith(jsonStr, '<')) {
			// 可能为XML
			//JSONXMLUtil.toJSONObject(jsonStr, jsonObject, ParseConfig.of());
			JSONXMLParser.of(ParseConfig.of(), this.predicate).parseJSONObject(jsonStr, jsonObject);
			return;
		}
		mapFromTokener(new JSONTokener(source), jsonObject.config(), jsonObject);
	}

	/**
	 * 从{@link JSONTokener}转换
	 *
	 * @param x          JSONTokener
	 * @param config     JSON配置
	 * @param jsonObject {@link OldJSONObject}
	 */
	private void mapFromTokener(final JSONTokener x, final JSONConfig config, final OldJSONObject jsonObject) {
		JSONParser.of(x, config).setPredicate(this.predicate).parseTo(jsonObject);
	}

	/**
	 * 从Record转换
	 *
	 * @param record     Record对象
	 * @param jsonObject {@link OldJSONObject}
	 */
	private void mapFromRecord(final Object record, final OldJSONObject jsonObject) {
		final Map.Entry<String, Type>[] components = RecordUtil.getRecordComponents(record.getClass());

		String key;
		for (final Map.Entry<String, Type> entry : components) {
			key = entry.getKey();
			jsonObject.set(key, MethodUtil.invoke(record, key));
		}
	}

	/**
	 * 从Bean转换
	 *
	 * @param bean       Bean对象
	 * @param jsonObject {@link OldJSONObject}
	 */
	private void mapFromBean(final Object bean, final OldJSONObject jsonObject) {
		final CopyOptions copyOptions = InternalJSONUtil.toCopyOptions(jsonObject.config());
		if (null != this.predicate) {
			copyOptions.setFieldEditor((entry -> this.predicate.test(
				MutableEntry.of(StrUtil.toStringOrNull(entry.getKey()), entry.getValue())) ?
				entry : null));
		}
		BeanUtil.beanToMap(bean, jsonObject, copyOptions);
	}
}
