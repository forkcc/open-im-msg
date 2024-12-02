package forkcc.msg.websocket;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class IMMessageHandler extends SimpleChannelInboundHandler<WebSocketFrame> {



    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        if(msg instanceof PongWebSocketFrame){
            log.info("{} 心跳反馈", ctx.channel().remoteAddress());
            return;
        }
        if(msg instanceof TextWebSocketFrame frame){
            doTextMessage(ctx,frame);
            return;
        }
        ctx.fireChannelRead(msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            ctx.write(new PingWebSocketFrame());
        }
    }
    private void doTextMessage(ChannelHandlerContext ctx, TextWebSocketFrame msg){
        JSONObject object;
        try{
            object = JSONUtil.parseObj(msg.text());
        }catch (Exception e){
            ctx.writeAndFlush(new TextWebSocketFrame("只接受JSON数据")).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
