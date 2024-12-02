package forkcc.msg.websocket;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import forkcc.msg.common.Constant;
import forkcc.msg.common.Error;
import forkcc.msg.common.util.SessionUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Data
@ChannelHandler.Sharable
public class IMMessageHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    @Value("${auth.timeout}")
    private int authTimeout;


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.executor().schedule(()->{
            if(!SessionUtil.isAuthenticated(ctx.channel())){
                log.info("客户端断开连接 -> 因为{}秒内未完成认证", authTimeout);
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
        },authTimeout, TimeUnit.SECONDS);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        if(msg instanceof TextWebSocketFrame frame){
            doTextMessage(ctx,frame);
            return;
        }
        ctx.fireChannelRead(msg);
    }

    private void doTextMessage(ChannelHandlerContext ctx, TextWebSocketFrame msg){
        JSONObject object;
        try{
            object = JSONUtil.parseObj(msg.text());
        }catch (Exception e){
            ctx.writeAndFlush(new TextWebSocketFrame(Error.BAD_DATA.getErrorStr()));
            return;
        }
        if(!object.containsKey(Constant.CMD)){
            ctx.writeAndFlush(new TextWebSocketFrame(Error.JSON_MISS_CMD.getErrorStr()));
            return;
        }
    }
}
