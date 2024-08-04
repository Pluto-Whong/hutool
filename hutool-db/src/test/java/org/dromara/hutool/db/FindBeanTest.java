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

package org.dromara.hutool.db;

import org.dromara.hutool.db.pojo.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Entity测试
 *
 * @author looly
 *
 */
public class FindBeanTest {

	Db db;

	@BeforeEach
	public void init() {
		db = Db.of("test");
	}

	@Test
	public void findAllBeanTest() {
		final List<User> results = db.findAll(Entity.of("user"), User.class);

		Assertions.assertEquals(4, results.size());
		Assertions.assertEquals(Integer.valueOf(1), results.get(0).getId());
		Assertions.assertEquals("张三", results.get(0).getName());
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void findAllListTest() {
		final List<List> results = db.findAll(Entity.of("user"), List.class);

		Assertions.assertEquals(4, results.size());
		Assertions.assertEquals(1, results.get(0).get(0));
		Assertions.assertEquals("张三", results.get(0).get(1));
	}

	@Test
	public void findAllArrayTest() {
		final List<Object[]> results = db.findAll(Entity.of("user"), Object[].class);

		Assertions.assertEquals(4, results.size());
		Assertions.assertEquals(1, results.get(0)[0]);
		Assertions.assertEquals("张三", results.get(0)[1]);
	}

	@Test
	public void findAllStringTest() {
		final List<String> results = db.findAll(Entity.of("user"), String.class);
		Assertions.assertEquals(4, results.size());
	}

	@Test
	public void findAllStringArrayTest() {
		final List<String[]> results = db.findAll(Entity.of("user"), String[].class);

		Assertions.assertEquals(4, results.size());
		Assertions.assertEquals("1", results.get(0)[0]);
		Assertions.assertEquals("张三", results.get(0)[1]);
	}
}
