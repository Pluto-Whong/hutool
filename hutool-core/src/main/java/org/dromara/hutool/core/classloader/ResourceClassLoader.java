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

package org.dromara.hutool.core.classloader;

import org.dromara.hutool.core.io.resource.Resource;
import org.dromara.hutool.core.util.ObjUtil;

import java.security.SecureClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * 资源类加载器，可以加载任意类型的资源类
 *
 * @param <T> {@link Resource}接口实现类
 * @author looly, lzpeng
 * @since 5.5.2
 */
public class ResourceClassLoader<T extends Resource> extends SecureClassLoader {

	private final Map<String, T> resourceMap;
	/**
	 * 缓存已经加载的类
	 */
	private final Map<String, Class<?>> cacheClassMap;

	/**
	 * 构造
	 *
	 * @param parentClassLoader 父类加载器，null表示默认当前上下文加载器
	 * @param resourceMap       资源map
	 */
	public ResourceClassLoader(final ClassLoader parentClassLoader, final Map<String, T> resourceMap) {
		super(ObjUtil.defaultIfNull(parentClassLoader, ClassLoaderUtil::getClassLoader));
		this.resourceMap = ObjUtil.defaultIfNull(resourceMap, HashMap::new);
		this.cacheClassMap = new HashMap<>();
	}

	/**
	 * 增加需要加载的类资源
	 *
	 * @param resource 资源，可以是文件、流或者字符串
	 * @return this
	 */
	public ResourceClassLoader<T> addResource(final T resource) {
		this.resourceMap.put(resource.getName(), resource);
		return this;
	}

	@Override
	protected Class<?> findClass(final String name) throws ClassNotFoundException {
		final Class<?> clazz = cacheClassMap.computeIfAbsent(name, this::defineByName);
		if (clazz == null) {
			return super.findClass(name);
		}
		return clazz;
	}

	/**
	 * 从给定资源中读取class的二进制流，然后生成类<br>
	 * 如果这个类资源不存在，返回{@code null}
	 *
	 * @param name 类名
	 * @return 定义的类
	 */
	private Class<?> defineByName(final String name) {
		final Resource resource = resourceMap.get(name);
		if (null != resource) {
			final byte[] bytes = resource.readBytes();
			return defineClass(name, bytes, 0, bytes.length);
		}
		return null;
	}
}
