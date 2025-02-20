package forkcc.openim.app.service.impl;

import cn.hutool.json.JSONUtil;
import forkcc.openim.app.jms.RabbitMQConfig;
import forkcc.openim.app.service.MessageService;
import forkcc.openim.common.dto.PushMessage;
import forkcc.openim.common.dto.message.MessageType;
import forkcc.openim.common.dto.message.Transparent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final RabbitTemplate rabbitTemplate;
    @Override
    public void pushTransparentMessage(String toUser, Transparent transparent, String extra) {
        PushMessage pushMessage = PushMessage.builder()
                .toUsers(Collections.singletonList(toUser))
                .messageType(MessageType.Transparent)
                .persistence(false)
                .extra(extra)
                .build();
        rabbitTemplate.convertAndSend(RabbitMQConfig.JMS_MESSAGE, JSONUtil.toJsonPrettyStr(pushMessage));
    }
}
