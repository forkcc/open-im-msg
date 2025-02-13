package forkcc.openim.courier.jms;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class PushIosMessageListener implements MessageListener {
    @Override
    public void onMessage(Message message) {

    }
}
