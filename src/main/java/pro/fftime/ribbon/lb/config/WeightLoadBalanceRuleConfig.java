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

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeightLoadBalanceRuleConfig extends AbstractLoadBalancerRule {

    private IClientConfig clientConfig;

    @Override
    public Server choose(final Object o) {
        final ILoadBalancer loadBalancer = this.getLoadBalancer();
        final List<Server> serverList = loadBalancer.getAllServers();

        if (CollectionUtils.isEmpty(serverList)){
            return null;
        }

        final Map<String,Server> serverMap = new HashMap<>();
        for (final Server server : serverList){
            serverMap.put(server.getHostPort(),server);
        }
        final List<WeightServer> weightServerList = this.clientConfig.get(RibbonConfigKey.weightServerList);

        if (CollectionUtils.isEmpty(weightServerList)){
            throw new RuntimeException("cannot find serverlist");
        }

        int total = 0;
        WeightServer serverOfMaxWeight = null;
        for (final WeightServer weightServer : weightServerList){
            final Server server = serverMap.get(weightServer.getHostPort());
            if (!server.isAlive()){
                weightServer.setEffectiveWeight(0);
            } else {
                weightServer.setEffectiveWeight(weightServer.getWeight());
            }
            total = total + weightServer.getEffectiveWeight();
            weightServer.setCurrentWeight(weightServer.getCurrentWeight() + weightServer.getEffectiveWeight());
            if (serverOfMaxWeight ==null){
                serverOfMaxWeight = weightServer;
            } else {
                serverOfMaxWeight = serverOfMaxWeight.getCurrentWeight() > weightServer.getCurrentWeight() ? serverOfMaxWeight : weightServer;
            }
        }
        serverOfMaxWeight.setCurrentWeight(serverOfMaxWeight.getCurrentWeight() - total);
        return  serverMap.get(serverOfMaxWeight.getHostPort());

    }

    @Override
    public void initWithNiwsConfig(final IClientConfig iClientConfig) {
        this.clientConfig = iClientConfig;
    }
}
