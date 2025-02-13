package forkcc.openim.broker.websocket;

import cn.hutool.json.JSONUtil;
import forkcc.openim.broker.callback.ClientCallback;
import forkcc.openim.common.dto.PushMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 连接池
 */
@Component
public class ChannelPool implements MessageListener {
    private static final DefaultChannelGroup GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public void addChannel(Channel channel){
        GROUP.add(channel);
    }

    /**
     * 接收js的消息分发到客户端
     * @param message the message.
     */
    @Override
    public void onMessage(Message message) {
        PushMessage pushMessage = JSONUtil.toBean(new String(message.getBody()), PushMessage.class);
        GROUP.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonPrettyStr(message)), channel -> Objects.equals(channel.attr(ClientCallback.CLIENT_ID).get(), pushMessage.getToUser()))
                .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }
}
