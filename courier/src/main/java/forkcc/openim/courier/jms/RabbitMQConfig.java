package forkcc.openim.courier.jms;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@EnableRabbit
@RequiredArgsConstructor
public class RabbitMQConfig implements RabbitListenerConfigurer {
    public static final String QUEUE_IOS_NAME = "message.ios.offline.queue";
    public static final String QUEUE_ANDROID_NAME = "message.android.offline.queue";
    private final PushIosMessageListener pushIosMessageListener;
    private final PushAndroidMessageListener pushAndroidMessageListener;
    @Bean
    public FanoutExchange messageExchange() {
        return new FanoutExchange("jms.message.list", true, false);
    }

    @Bean
    public Queue messageIosQueue() {
        return new Queue(QUEUE_IOS_NAME, true, false,false);
    }
    @Bean
    public Queue messageAndroidQueue() {
        return new Queue(QUEUE_ANDROID_NAME, true, false,false);
    }
    @Bean
    public Binding messageIosBinding(){
        return BindingBuilder.bind(messageIosQueue()).to(messageExchange());
    }
    @Bean
    public Binding messageAndroidBinding(){
        return BindingBuilder.bind(messageAndroidQueue()).to(messageExchange());
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar rabbitListenerEndpointRegistrar) {
        SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
        endpoint.setId("messageIosListener");
        endpoint.setQueueNames(QUEUE_IOS_NAME);
        endpoint.setMessageListener(pushIosMessageListener);
        rabbitListenerEndpointRegistrar.registerEndpoint(endpoint);
        endpoint = new SimpleRabbitListenerEndpoint();
        endpoint.setId("messageAndroidListener");
        endpoint.setQueueNames(QUEUE_ANDROID_NAME);
        endpoint.setMessageListener(pushAndroidMessageListener);
        rabbitListenerEndpointRegistrar.registerEndpoint(endpoint);
    }
}
