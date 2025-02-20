package forkcc.openim.app.service;

import forkcc.openim.common.dto.message.Transparent;

/**
 * 消息发送
 */
public interface MessageService {
    /**
     * 发送透传消息
     *    消息不一定送到，主要是一些不重要的通知
     * @param toUser 接收用户
     * @param transparent 透传消息类型
     * @param extra 附加信息
     */
    void pushTransparentMessage(String toUser, Transparent transparent, String extra);
}
