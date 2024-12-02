package forkcc.msg.common.util;

import forkcc.msg.common.Constant;
import io.netty.channel.Channel;

public class SessionUtil {
    /**
     * 是否已经认证
     * @param channel
     * @return
     */
    public static boolean isAuthenticated(Channel channel){
        return channel.hasAttr(Constant.DEVICE_ID) && channel.hasAttr(Constant.USER_TOKEN) && channel.hasAttr(Constant.USER_ID);
    }
}
