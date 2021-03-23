package pro.fftime.spring.ribbon.lb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerClient;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class RibbonInitialize implements SmartLifecycle {

    @Autowired
    private LoadBalancerClient loadBalancerClient ;

    @Override
    public void start() {
        loadBalancerClient.choose("example");
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {

    }

    @Override
    public int getPhase() {
        return 0;
    }
}
