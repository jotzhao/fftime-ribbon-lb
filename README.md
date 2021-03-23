# fftime-ribbon-lb
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html) 
[![GitHub release](https://img.shields.io/github/v/release/jotzhao/fftime-ribbon-lb.svg)](https://github.com/jotzhao/fftime-ribbon-lb/releases)

fftime-ribbon-lb是Spring Cloud Ribbon插件，提供了按照权重的负载均衡的功能。 
## 配置

| 配置项                           | 类型      |  说明   |
| -----------------------------   | :-----   | :---- |
| xxx.ribbon.listOfWeightServer   | array    | url列表 |
| xxx.ribbon.NFLoadBalancerPingClassName | className      |   pro.fftime.ribbon.lb.config.ServerPingConfig    |
| xxx.ribbon.NFLoadBalancerRuleClassName | className      |   pro.fftime.ribbon.lb.config.WeightLoadBalanceRuleConfig    |
| xxx.ribbon.NIWSServerListClassName | className      |   pro.fftime.ribbon.lb.config.WeightServerListConfig    |

Example:
```properties
ribbon:
  eureka:
    enable: false
example:
  ribbon:
    listOfWeightServer:
      - http://www.baidu.com,3
      - http://www.baidu33443.com,4
      - http://www.baidu444.com,1
    NFLoadBalancerPingClassName: pro.fftime.ribbon.lb.config.ServerPingConfig
    NFLoadBalancerPingInterval: 1
    NFLoadBalancerRuleClassName: pro.fftime.ribbon.lb.config.WeightLoadBalanceRuleConfig
    NIWSServerListClassName: pro.fftime.ribbon.lb.config.WeightServerListConfig
```
listOfWeightServer格式：`url,权重`
## 负载均衡算法
负载均衡参考Nginx的负载均衡算法。该算法的原理如下：

每个服务器都有两个权重变量：
1. weight，配置文件中指定的该服务器的权重，这个值是固定不变的；  
2. current_weight，服务器目前的权重。一开始为0，之后会动态调整。  

每次当请求到来，选取服务器时，会遍历数组中所有服务器。对于每个服务器，让它的current_weight增加它的weight；同时累加所有服务器的weight，并保存为total。
遍历完所有服务器之后，如果该服务器的current_weight是最大的，就选择这个服务器处理本次请求。最后把该服务器的current_weight减去total。

##注意事项
ribbon loadbalance的初始化是在第一次使用`loadBalanceClient.choose`时候进行，这时候将会加载相关配置文件进行初始化。这个过程一般比较慢。如果应用对请求时长比较敏感，建议参考test中的`RibbonInitialize`增加`lifecycle`来提前初始化。

##使用方法

```java
public class TradeController {

    private final Logger logger = LoggerFactory.getLogger(TradeController.class);

    @Autowired
    private LoadBalancerClient client;


    @RequestMapping("/lb")
    @ResponseBody
    public String lb(){
        final ServiceInstance instance = this.client.choose("example");
        this.logger.info("ribbon instance:{}",instance);

        return instance.getHost();
    }
}
```
