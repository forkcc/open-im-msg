package forkcc.openim.broker.websocket;

import forkcc.openim.broker.callback.ClientCallback;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class FullHttpRequestServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final ClientCallback callback;
    private final String contextPath;
    private final ChannelPool channelPool;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        String uri = msg.uri();
        if(!uri.startsWith(contextPath)){
            ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND)).addListener(ChannelFutureListener.CLOSE);
            return;
        }
        String token = uri.replaceAll(contextPath, "");
        if(token.startsWith("/")){
            token = token.substring(1);
        }
        callback.connect(token, ctx);
        FullHttpRequest request =
                new DefaultFullHttpRequest(msg.protocolVersion(), msg.method(), uri, msg.content().copy(), msg.headers().copy(), msg.trailingHeaders().copy());
        ctx.fireChannelRead(request);
        callback.online(ctx);
        channelPool.addChannel(ctx.channel());
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        callback.offline(ctx);
        super.channelInactive(ctx);
    }
}
