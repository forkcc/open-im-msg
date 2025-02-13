package forkcc.openim.broker.websocket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Component;

/**
 * 连接池
 */
@Component
public class ChannelPool{
    private static final DefaultChannelGroup GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public void addChannel(Channel channel){
        GROUP.add(channel);
    }

    public void writeMessage(WebSocketFrame frame, ChannelMatcher matcher){
        GROUP.writeAndFlush(frame, matcher);
    }
}
