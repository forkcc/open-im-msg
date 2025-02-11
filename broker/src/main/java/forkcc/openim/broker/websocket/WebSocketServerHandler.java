package forkcc.openim.broker.websocket;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import forkcc.openim.broker.cmd.AbstractCMD;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final List<AbstractCMD<?>> cmds;
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame webSocketFrame) throws Exception {
        String json = webSocketFrame.text();
        if(StrUtil.isBlank(json) || !JSONUtil.isTypeJSON(json) || JSONUtil.isTypeJSONArray(json)){
            return;
        }
        log.info("收到消息 {}", json);
        JSONObject object = JSONUtil.parseObj(json);
        for (AbstractCMD<?> cmd : cmds.parallelStream().filter(cmd -> Objects.equals(object.getStr("cmd"), cmd.getName())).toList()) {
           try{
               cmd.execute(object.getJSONObject("data"));
           }catch (Exception e){
               log.error("执行命令失败", e);
               channelHandlerContext.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonPrettyStr(JSONUtil.createObj().putOnce("code", 500).putOnce("message", "服务器未知异常"))));
           }
        }
    }
}
