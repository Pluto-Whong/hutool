#!/bin/bash

#
# Copyright (c) 2024 looly(loolly@aliyun.com)
# Hutool is licensed under Mulan PSL v2.
# You can use this software according to the terms and conditions of the Mulan PSL v2.
# You may obtain a copy of Mulan PSL v2 at:
#          https://license.coscl.org.cn/MulanPSL2
# THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
# EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
# MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
# See the Mulan PSL v2 for more details.
#

echo -e "\033[32mCheckout to v6-dev\033[0m"
git checkout v6-dev

echo -e "\033[32mPush to origin v6-dev\033[0m"
git push origin v6-dev
echo -e "\033[32mPush to osc v6-dev\033[0m"
git push osc v6-dev
