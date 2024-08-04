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

package org.dromara.hutool.crypto.digest;

import org.dromara.hutool.crypto.KeyUtil;
import org.dromara.hutool.crypto.digest.mac.Mac;
import org.dromara.hutool.crypto.digest.mac.SM4MacEngine;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CBCBlockCipherMacEngineTest {

	@Test
	public void SM4CMACTest(){
		// https://github.com/dromara/hutool/issues/2206
		final byte[] key = new byte[16];
		final CipherParameters parameter = new KeyParameter(KeyUtil.generateKey("SM4", key).getEncoded());
		final Mac mac = new Mac(new SM4MacEngine(parameter));

		// 原文
		final String testStr = "test中文";

		final String macHex1 = mac.digestHex(testStr);
		Assertions.assertEquals("3212e848db7f816a4bd591ad9948debf", macHex1);
	}

	@Test
	public void SM4CMACWithIVTest(){
		// https://github.com/dromara/hutool/issues/2206
		final byte[] key = new byte[16];
		final byte[] iv = new byte[16];
		CipherParameters parameter = new KeyParameter(KeyUtil.generateKey("SM4", key).getEncoded());
		parameter = new ParametersWithIV(parameter, iv);
		final Mac mac = new Mac(new SM4MacEngine(parameter));

		// 原文
		final String testStr = "test中文";

		final String macHex1 = mac.digestHex(testStr);
		Assertions.assertEquals("3212e848db7f816a4bd591ad9948debf", macHex1);
	}
}
