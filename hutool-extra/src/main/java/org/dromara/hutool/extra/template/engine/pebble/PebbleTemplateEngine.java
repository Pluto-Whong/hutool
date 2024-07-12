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

package org.dromara.hutool.extra.template.engine.pebble;

import org.dromara.hutool.core.collection.ListUtil;
import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.extra.template.Template;
import org.dromara.hutool.extra.template.TemplateConfig;
import org.dromara.hutool.extra.template.engine.TemplateEngine;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.*;

/**
 * Pebble(https://pebbletemplates.io/) 模板引擎
 *
 * @author zooooooooy
 * @since 6.0.0
 */
public class PebbleTemplateEngine implements TemplateEngine {

	private PebbleEngine engine;

	/**
	 * 构造，不初始化
	 */
	public PebbleTemplateEngine() {
		// SPI方式加载时检查库是否引入
		Assert.notNull(PebbleEngine.class);
	}

	/**
	 * 构造
	 *
	 * @param config 配置
	 */
	public PebbleTemplateEngine(final TemplateConfig config) {
		init(config);
	}

	@Override
	public TemplateEngine init(final TemplateConfig config) {
		init(createEngine(config));
		return this;
	}

	/**
	 * 初始化引擎
	 *
	 * @param engine 引擎
	 */
	private void init(final PebbleEngine engine) {
		this.engine = engine;
	}

	/**
	 * 创建引擎
	 *
	 * @param config 模板配置
	 * @return PebbleEngine
	 */
	private static PebbleEngine createEngine(TemplateConfig config) {
		if (null == config) {
			config = TemplateConfig.DEFAULT;
		}

		Loader<?> loader = null;
		switch (config.getResourceMode()) {
			case CLASSPATH:
				loader = new ClasspathLoader();
				loader.setPrefix(StrUtil.addSuffixIfNot(config.getPath(), StrUtil.SLASH));
				break;
			case FILE:
				loader = new FileLoader();
				loader.setPrefix(StrUtil.addSuffixIfNot(config.getPath(), StrUtil.SLASH));
				break;
			case WEB_ROOT:
				loader = new FileLoader();
				loader.setPrefix(StrUtil.addSuffixIfNot(
					FileUtil.getAbsolutePath(FileUtil.file(FileUtil.getWebRoot(), config.getPath())), StrUtil.SLASH));
				break;
			case STRING:
				loader = new StringLoader();
				break;
			case COMPOSITE:
				loader = new DelegatingLoader(ListUtil.of(
					new ClasspathLoader(),
					new FileLoader(),
					new StringLoader()
				));
			default:
				// 默认null表示使用DelegatingLoader
				break;
		}

		return new PebbleEngine.Builder()
			.loader(loader)
			.autoEscaping(false)
			.build();
	}

	/**
	 * 通过路径获取对应模板操作类
	 *
	 * @param resource 资源，根据实现不同，此资源可以是模板本身，也可以是模板的相对路径
	 * @return {@link Template}
	 */
	@Override
	public Template getTemplate(final String resource) {

		if (null == this.engine) {
			init(TemplateConfig.DEFAULT);
		}

		return PebbleTemplate.wrap(engine.getTemplate(resource));
	}

	@Override
	public PebbleEngine getRaw() {
		return this.engine;
	}

}
