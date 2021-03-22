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
import com.netflix.loadbalancer.AbstractServerList;
import com.netflix.loadbalancer.Server;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class WeightServerListConfig extends AbstractServerList<Server> {

    private IClientConfig clientConfig;

    /**
     * 是否为数字
     * @param str
     * @return
     */
    private static boolean isInteger(final String str) {
        final Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    @Override
    public void initWithNiwsConfig(final IClientConfig iClientConfig) {
        this.clientConfig = iClientConfig;
        //ribbon在获取list配置的时候，会将yml中的list转换为list[0]、list[1]等，后续因为需要使用collection，因此这块将其转换为list
        final Map<String,Object> properties = this.clientConfig.getProperties();
        for (final Map.Entry<String,Object> entry : properties.entrySet()){
            final String key = entry.getKey();
            if (key.contains("[") && key.contains("]") && key.indexOf("[")< key.indexOf("]")){
                final String newKey = key.substring(0,key.indexOf("["));
                final List<String> list = (List<String>) properties.computeIfAbsent(newKey, k->new ArrayList<String>());
                list.add((String) entry.getValue());
            }
        }

    }

    /**
     * 初始化服务器列表
     * @return
     */
    @Override
    public List<Server> getInitialListOfServers() {

        //获取带有权重的服务器列表
        final List<String> listServer =  this.clientConfig.get(RibbonConfigKey.weightServerStringList);
        if (CollectionUtils.isEmpty(listServer)){
            return null;
        }
        final List<WeightServer> weightServerList = new ArrayList<>();
        final List<Server> serverList = new ArrayList<>();
        int totalWeight = 0;
        for (final String weightServerString : listServer){
            final String[] serverParam = weightServerString.split(",");
            final String serverInfo = serverParam[0];
            int weight = 0;
            if (serverParam.length>1){
                final String weightStr = serverParam[1];
                if (isInteger(weightStr)){
                    weight = Integer.parseInt(weightStr);
                }
            }

            totalWeight = totalWeight+ weight;
            final WeightServer weightServer = new WeightServer(serverInfo.trim());
            weightServer.setWeight(weight);
            weightServer.setEffectiveWeight(weight);
            weightServer.setCurrentWeight(0);
            weightServerList.add(weightServer);
            serverList.add(weightServer);

        }

        if (totalWeight <= 0){
            weightServerList.forEach(k->k.setWeight(1));
        }

        this.clientConfig.set(RibbonConfigKey.weightServerList,weightServerList);
        return serverList;
    }

    @Override
    public List<Server> getUpdatedListOfServers() {
        return this.getInitialListOfServers();
    }
}
