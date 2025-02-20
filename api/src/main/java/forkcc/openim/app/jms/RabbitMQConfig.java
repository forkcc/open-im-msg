package forkcc.openim.app.jms;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableRabbit
@RequiredArgsConstructor
public class RabbitMQConfig {
    public static final String JMS_MESSAGE = "jms.message.list";
}
