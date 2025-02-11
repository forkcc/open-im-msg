package forkcc.openim.broker.callback;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import forkcc.openim.common.exception.BizException;
import forkcc.openim.common.kit.AssertKit;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientCallback {
    /**
     * 连接前进行Token验证
     * @param token
     * @return
     */
    public void connect(String token){
        AssertKit.validState(StrUtil.isNotBlank(token), new BizException("缺少Token"));
    }
}
