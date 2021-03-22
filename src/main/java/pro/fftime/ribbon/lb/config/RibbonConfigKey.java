/*
 * Copyright [2021] [jot.zhao]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pro.fftime.ribbon.lb.config;

import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.IClientConfigKey;

import java.util.List;

public class RibbonConfigKey {

    /**
     * 带权重的服务器列表
     * 例子：
     *
     */
    public static final IClientConfigKey<List<String>> weightServerStringList = new CommonClientConfigKey<List<String>>("listOfWeightServer") {
    };

    /**
     * 权重服务器列表，不对外使用
     */
    public static final IClientConfigKey<List<WeightServer>> weightServerList = new CommonClientConfigKey<List<WeightServer>>("weightServerList") {
    };

    /**
     * 两次ping的间隔时间
     */
    public static final IClientConfigKey<Integer> pingTimeSpan = new CommonClientConfigKey<Integer>("ping-time-span") {
    };

    /**
     * 一次周琪内连续ping的次数
     */
    public static final IClientConfigKey<Integer> pingTimes = new CommonClientConfigKey<Integer>("ping-times") {
    };
}
