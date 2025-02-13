package forkcc.openim.courier.jms;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class PushAndroidMessageListener implements MessageListener {
    @Override
    public void onMessage(Message message) {

    }
}
