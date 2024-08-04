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

package org.dromara.hutool.core.lang.range;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * test for {@link BoundType}
 */
public class BoundTypeTest {

	@Test
	public void testIsDislocated() {
		Assertions.assertTrue(BoundType.CLOSE_LOWER_BOUND.isDislocated(BoundType.CLOSE_UPPER_BOUND));
		Assertions.assertTrue(BoundType.CLOSE_LOWER_BOUND.isDislocated(BoundType.OPEN_UPPER_BOUND));
		Assertions.assertFalse(BoundType.CLOSE_LOWER_BOUND.isDislocated(BoundType.CLOSE_LOWER_BOUND));
		Assertions.assertFalse(BoundType.CLOSE_LOWER_BOUND.isDislocated(BoundType.OPEN_LOWER_BOUND));
	}

	@Test
	public void testIsLowerBound() {
		Assertions.assertFalse(BoundType.CLOSE_UPPER_BOUND.isLowerBound());
		Assertions.assertFalse(BoundType.OPEN_UPPER_BOUND.isLowerBound());
		Assertions.assertTrue(BoundType.CLOSE_LOWER_BOUND.isLowerBound());
		Assertions.assertTrue(BoundType.OPEN_LOWER_BOUND.isLowerBound());
	}

	@Test
	public void testIsUpperBound() {
		Assertions.assertTrue(BoundType.CLOSE_UPPER_BOUND.isUpperBound());
		Assertions.assertTrue(BoundType.OPEN_UPPER_BOUND.isUpperBound());
		Assertions.assertFalse(BoundType.CLOSE_LOWER_BOUND.isUpperBound());
		Assertions.assertFalse(BoundType.OPEN_LOWER_BOUND.isUpperBound());
	}

	@Test
	public void testIsOpen() {
		Assertions.assertFalse(BoundType.CLOSE_UPPER_BOUND.isOpen());
		Assertions.assertTrue(BoundType.OPEN_UPPER_BOUND.isOpen());
		Assertions.assertFalse(BoundType.CLOSE_LOWER_BOUND.isOpen());
		Assertions.assertTrue(BoundType.OPEN_LOWER_BOUND.isOpen());
	}

	@Test
	public void testIsClose() {
		Assertions.assertTrue(BoundType.CLOSE_UPPER_BOUND.isClose());
		Assertions.assertFalse(BoundType.OPEN_UPPER_BOUND.isClose());
		Assertions.assertTrue(BoundType.CLOSE_LOWER_BOUND.isClose());
		Assertions.assertFalse(BoundType.OPEN_LOWER_BOUND.isClose());
	}

	@Test
	public void testNegate() {
		Assertions.assertEquals(BoundType.CLOSE_UPPER_BOUND, BoundType.OPEN_LOWER_BOUND.negate());
		Assertions.assertEquals(BoundType.OPEN_UPPER_BOUND, BoundType.CLOSE_LOWER_BOUND.negate());
		Assertions.assertEquals(BoundType.OPEN_LOWER_BOUND, BoundType.CLOSE_UPPER_BOUND.negate());
		Assertions.assertEquals(BoundType.CLOSE_LOWER_BOUND, BoundType.OPEN_UPPER_BOUND.negate());
	}

	@Test
	public void testGetSymbol() {
		Assertions.assertEquals("]", BoundType.CLOSE_UPPER_BOUND.getSymbol());
		Assertions.assertEquals(")", BoundType.OPEN_UPPER_BOUND.getSymbol());
		Assertions.assertEquals("[", BoundType.CLOSE_LOWER_BOUND.getSymbol());
		Assertions.assertEquals("(", BoundType.OPEN_LOWER_BOUND.getSymbol());
	}

	@Test
	public void testGetCode() {
		Assertions.assertEquals(2, BoundType.CLOSE_UPPER_BOUND.getCode());
		Assertions.assertEquals(1, BoundType.OPEN_UPPER_BOUND.getCode());
		Assertions.assertEquals(-1, BoundType.OPEN_LOWER_BOUND.getCode());
		Assertions.assertEquals(-2, BoundType.CLOSE_LOWER_BOUND.getCode());
	}

	@Test
	public void testGetOperator() {
		Assertions.assertEquals("<=", BoundType.CLOSE_UPPER_BOUND.getOperator());
		Assertions.assertEquals("<", BoundType.OPEN_UPPER_BOUND.getOperator());
		Assertions.assertEquals(">", BoundType.OPEN_LOWER_BOUND.getOperator());
		Assertions.assertEquals(">=", BoundType.CLOSE_LOWER_BOUND.getOperator());
	}

}
