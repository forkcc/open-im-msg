package forkcc.openim.courier.jms;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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
    public static final String QUEUE_NAME = "message.queue."+ UUID.randomUUID().toString().replace("-", ".");
    private final PushMessageListener pushMessageListener;
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
        endpoint.setMessageListener(pushMessageListener);
        rabbitListenerEndpointRegistrar.registerEndpoint(endpoint);
    }
}
