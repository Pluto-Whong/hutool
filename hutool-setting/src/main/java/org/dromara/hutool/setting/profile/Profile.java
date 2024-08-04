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

package org.dromara.hutool.setting.profile;

import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.map.concurrent.SafeConcurrentHashMap;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.setting.Setting;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Profile可以让我们定义一系列的配置信息，然后指定其激活条件。<br>
 * 此类中我们规范一套规则如下：<br>
 * 默认的，我们读取${classpath}/default下的配置文件(*.setting文件)，当调用setProfile方法时，指定一个profile，即可读取其目录下的配置文件。<br>
 * 比如我们定义几个profile：test，develop，production，分别代表测试环境、开发环境和线上环境，我希望读取数据库配置文件db.setting，那么：
 * <ol>
 * <li>test =》 ${classpath}/test/db.setting</li>
 * <li>develop =》 ${classpath}/develop/db.setting</li>
 * <li>production =》 ${classpath}/production/db.setting</li>
 * </ol>
 *
 * @author Looly
 *
 */
public class Profile implements Serializable {
	private static final long serialVersionUID = -4189955219454008744L;

	/** 默认环境 */
	public static final String DEFAULT_PROFILE = "default";

	/** 条件 */
	private String profile;
	/** 编码 */
	private Charset charset;
	/** 是否使用变量 */
	private boolean useVar;
	/** 配置文件缓存 */
	private final Map<String, Setting> settingMap = new SafeConcurrentHashMap<>();

	// -------------------------------------------------------------------------------- Constructor start
	/**
	 * 默认构造，环境使用默认的：default，编码UTF-8，不使用变量
	 */
	public Profile() {
		this(DEFAULT_PROFILE);
	}

	/**
	 * 构造，编码UTF-8，不使用变量
	 *
	 * @param profile 环境
	 */
	public Profile(final String profile) {
		this(profile, Setting.DEFAULT_CHARSET, false);
	}

	/**
	 * 构造
	 *
	 * @param profile 环境
	 * @param charset 编码
	 * @param useVar 是否使用变量
	 */
	public Profile(final String profile, final Charset charset, final boolean useVar) {
		this.profile = profile;
		this.charset = charset;
		this.useVar = useVar;
	}
	// -------------------------------------------------------------------------------- Constructor end

	/**
	 * 获取当前环境下的配置文件
	 *
	 * @param name 文件名，如果没有扩展名，默认为.setting
	 * @return 当前环境下配置文件
	 */
	public Setting getSetting(final String name) {
		final String nameForProfile = fixNameForProfile(name);
		Setting setting = settingMap.get(nameForProfile);
		if (null == setting) {
			setting = new Setting(nameForProfile, this.charset, this.useVar);
			settingMap.put(nameForProfile, setting);
		}
		return setting;
	}

	/**
	 * 设置环境
	 *
	 * @param profile 环境
	 * @return 自身
	 */
	public Profile setProfile(final String profile) {
		this.profile = profile;
		return this;
	}

	/**
	 * 设置编码
	 *
	 * @param charset 编码
	 * @return 自身
	 */
	public Profile setCharset(final Charset charset) {
		this.charset = charset;
		return this;
	}

	/**
	 * 设置是否使用变量
	 *
	 * @param useVar 变量
	 * @return 自身
	 */
	public Profile setUseVar(final boolean useVar) {
		this.useVar = useVar;
		return this;
	}

	/**
	 * 清空所有环境的配置文件
	 *
	 * @return 自身
	 */
	public Profile clear() {
		this.settingMap.clear();
		return this;
	}

	// -------------------------------------------------------------------------------- Private method start
	/**
	 * 修正文件名
	 *
	 * @param name 文件名
	 * @return 修正后的文件名
	 */
	private String fixNameForProfile(final String name) {
		Assert.notBlank(name, "Setting name must be not blank !");
		final String actralProfile = StrUtil.toStringOrEmpty(this.profile);
		if (!name.contains(StrUtil.DOT)) {
			return StrUtil.format("{}/{}.setting", actralProfile, name);
		}
		return StrUtil.format("{}/{}", actralProfile, name);
	}
	// -------------------------------------------------------------------------------- Private method end
}
