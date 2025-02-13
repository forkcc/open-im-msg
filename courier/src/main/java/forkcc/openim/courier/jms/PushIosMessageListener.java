package forkcc.openim.courier.jms;

import forkcc.openim.common.exception.BizException;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class PushIosMessageListener implements MessageListener {
    private final RateLimiter rateLimiter;
    public PushIosMessageListener(){
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(1)) // 每秒刷新令牌
                .limitForPeriod(1000) // 每秒允许 1000 个请求
                .timeoutDuration(Duration.ofMillis(1500)) // 请求超时 1500 毫秒
                .build();
        rateLimiter =  RateLimiter.of("PushIosMessageListener", config);

    }
    @Override
    public void onMessage(Message message) {
        if(!rateLimiter.acquirePermission()){
            throw new BizException("离线消息限流");
        }
    }
}
