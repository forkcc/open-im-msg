package forkcc.openim.broker.jms;

import cn.hutool.json.JSONUtil;
import forkcc.openim.broker.callback.ClientCallback;
import forkcc.openim.broker.websocket.ChannelPool;
import forkcc.openim.common.dto.PushMessage;
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

import java.util.UUID;
@Slf4j
@Configuration
@EnableRabbit
@RequiredArgsConstructor
public class RabbitMQConfig implements RabbitListenerConfigurer {
    public static final String QUEUE_NAME = "message.online.queue."+ UUID.randomUUID().toString().replace("-", ".");
    private final ChannelPool channelPool;
    @Bean
    public FanoutExchange messageExchange() {
        return new FanoutExchange("jms.message.list", true, false);
    }


    @Bean
    public Queue messageQueue() {
        return new Queue(QUEUE_NAME, false, false,true);
    }

    @Bean
    public Binding messageBinding(){
        return BindingBuilder.bind(messageQueue()).to(messageExchange());
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
                        channel ->pushMessage.getToUsers().contains(channel.attr(ClientCallback.CLIENT_ID).get()));
            }
        });

    }
}
