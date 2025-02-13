package forkcc.openim.broker.jms;
import cn.hutool.json.JSONUtil;
import forkcc.openim.broker.callback.ClientCallback;
import forkcc.openim.broker.websocket.ChannelPool;
import forkcc.openim.common.dto.KillConn;
import forkcc.openim.common.dto.PushMessage;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelMatcher;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.UUID;
@Slf4j
@Configuration
@EnableRabbit
@RequiredArgsConstructor
public class RabbitMQConfig implements RabbitListenerConfigurer {
    public static final String QUEUE_NAME = "message.online.queue."+ UUID.randomUUID().toString().replace("-", ".");
    public static final String QUEUE_NAME2 = "connect.online.queue."+ UUID.randomUUID().toString().replace("-", ".");
    private final ChannelPool channelPool;
    @Bean
    public FanoutExchange messageExchange() {
        return new FanoutExchange("jms.message.list", true, false);
    }

    @Bean
    public FanoutExchange connectExchange() {
        return new FanoutExchange("jms.connect.list", true, false);
    }

    @Bean
    public Queue messageQueue() {
        return new Queue(QUEUE_NAME, false, false,true);
    }

    @Bean
    public Queue connectQueue() {
        return new Queue(QUEUE_NAME2, false, false,true);
    }
    @Bean
    public Binding messageBinding(){
        return BindingBuilder.bind(messageQueue()).to(messageExchange());
    }

    @Bean
    public Binding connectBinding(){
        return BindingBuilder.bind(connectQueue()).to(connectExchange());
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar rabbitListenerEndpointRegistrar) {
        SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
        endpoint.setId("messageListener");
        endpoint.setQueueNames(QUEUE_NAME);
        endpoint.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                log.info("收到推送消息 {}", new String(message.getBody()));
                PushMessage pushMessage = JSONUtil.toBean(new String(message.getBody()), PushMessage.class);
                channelPool.writeMessage(new TextWebSocketFrame(JSONUtil.toJsonPrettyStr(pushMessage)),
                        channel ->
                                Objects.equals(channel.attr(ClientCallback.CLIENT_ID).get(), pushMessage.getToUser()));
            }
        });
        rabbitListenerEndpointRegistrar.registerEndpoint(endpoint);
        endpoint = new SimpleRabbitListenerEndpoint();
        endpoint.setId("connectListener");
        endpoint.setQueueNames(QUEUE_NAME2);
        endpoint.setMessageListener(message -> {
            log.info("收到下线通知 {}", new String(message.getBody()));
            KillConn killConn = JSONUtil.toBean(new String(message.getBody()), KillConn.class);
            channelPool.writeMessage(new CloseWebSocketFrame(),
                    channel ->
                            Objects.equals(channel.attr(ClientCallback.CLIENT_ID).get(), killConn.getToUser()) &&
                            Objects.equals(channel.attr(ClientCallback.DEVICE_TYPE).get(), killConn.getDeviceType())
            );
        });
        rabbitListenerEndpointRegistrar.registerEndpoint(endpoint);
    }
}
