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
import com.netflix.loadbalancer.AbstractLoadBalancerPing;
import com.netflix.loadbalancer.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerPingConfig extends AbstractLoadBalancerPing {

    private static final Logger logger = LoggerFactory.getLogger(ServerPingConfig.class);

    private IClientConfig clientConfig;

    @Override
    public boolean isAlive(final Server server) {

        Map<String,Integer> ipAliveMapCount =clientConfig.get(RibbonConfigKey.ipAliveCount);

        if (ipAliveMapCount==null){
            ipAliveMapCount = new HashMap<>();
        }

        int failedCount = ipAliveMapCount.getOrDefault(server.getHostPort(),0);


        Integer pingTimes = this.clientConfig.get(RibbonConfigKey.pingTimes);
        if (pingTimes==null){
            pingTimes = 3;
        }

        final boolean pingResult = this.isServerDead(server);

        if (pingResult){
            failedCount = 0;
        } else {
            failedCount++;
        }
        ipAliveMapCount.put(server.getHostPort(),failedCount);
        clientConfig.set(RibbonConfigKey.ipAliveCount,ipAliveMapCount);
        return (failedCount>=pingTimes);
    }

    private boolean isServerDead(final Server server){
        final String host = server.getHost();
        final int port = server.getPort();

        final long startTime = System.currentTimeMillis();
        try {
            final Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host,port),200);
            socket.close();
            return false;
        } catch (final Exception e) {
            logger.error("can't connect to {}:{},exception:{},error:{}", host, port, e.getClass().getName(),e.getMessage());
            return true;
        }
    }

    @Override
    public void initWithNiwsConfig(final IClientConfig iClientConfig) {
        this.clientConfig = iClientConfig;
    }
}
