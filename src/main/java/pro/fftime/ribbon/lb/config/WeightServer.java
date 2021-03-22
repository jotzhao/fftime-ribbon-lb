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

import com.netflix.loadbalancer.Server;

public class WeightServer extends Server {

    private Integer weight;

    private Integer effectiveWeight;

    private Integer currentWeight;

    public WeightServer(final String host, final int port) {
        super(host, port);
    }

    public WeightServer(final String scheme, final String host, final int port) {
        super(scheme, host, port);
    }

    public WeightServer(final String id) {
        super(id);
    }

    public Integer getCurrentWeight() {
        return this.currentWeight;
    }

    public void setCurrentWeight(final Integer currentWeight) {
        this.currentWeight = currentWeight;
    }

    public Integer getEffectiveWeight() {
        return this.effectiveWeight;
    }

    public void setEffectiveWeight(final Integer effectiveWeight) {
        this.effectiveWeight = effectiveWeight;
    }

    public Integer getWeight() {
        return this.weight;
    }

    public void setWeight(final Integer weight) {
        this.weight = weight;
    }
}
