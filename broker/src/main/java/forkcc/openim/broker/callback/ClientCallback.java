package forkcc.openim.broker.callback;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import forkcc.openim.common.exception.BizException;
import forkcc.openim.common.kit.AssertKit;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClientCallback {
    public static final AttributeKey<String> TOKEN = AttributeKey.newInstance("user.token");
    public static final AttributeKey<String> CLIENT_ID = AttributeKey.newInstance("user.client.id");
    public static final AttributeKey<String> DEVICE_TYPE = AttributeKey.newInstance("device.type");
    /**
     * 连接前进行Token验证
     */
    public void connect(String token, ChannelHandlerContext ctx){
        AssertKit.validState(StrUtil.isNotBlank(token), new BizException("缺少Token"));
        log.info("触发连接验证 {}", token);
        ctx.channel().attr(TOKEN).set(token);
    }

    /**
     * 在线回调
     */
    public void online(ChannelHandlerContext ctx){
        String token = ctx.channel().attr(TOKEN).get();
        if(StrUtil.isBlank(token)){
            return;
        }
        log.info("触发上线 {}", token);
    }

    /**
     * 离线回调
     */
    public void offline(ChannelHandlerContext ctx){
        String token = ctx.channel().attr(TOKEN).get();
        if(StrUtil.isBlank(token)){
            return;
        }
        log.info("触发离线 {}", token);
    }
}
