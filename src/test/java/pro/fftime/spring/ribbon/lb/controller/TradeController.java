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

package pro.fftime.spring.ribbon.lb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;

@Controller
@RequestMapping("/example")
public class TradeController {

    private final Logger logger = LoggerFactory.getLogger(TradeController.class);

    @Autowired
    private LoadBalancerClient client;

    @Autowired
    private SpringClientFactory clientFactory;


    @RequestMapping("/lb")
    @ResponseBody
    public String lb(){
        final ServiceInstance instance = this.client.choose("example");
        this.logger.info("ribbon instance:{}",instance);

        return instance.getHost();
    }
}
