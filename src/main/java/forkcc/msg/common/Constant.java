package forkcc.msg.common;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public interface Constant {
    String CMD = "cmd";
    AttributeKey<String> DEVICE_ID = AttributeKey.newInstance("device_id");
    AttributeKey<String> USER_TOKEN = AttributeKey.newInstance("user_token");
    AttributeKey<String> USER_ID = AttributeKey.newInstance("user_id");
}
