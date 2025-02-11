package forkcc.openim.common.kit;

/**
 * 断言工具
 */
public class AssertKit {
    /**
     * 校验规则
     */
    public static void validState(boolean condition, RuntimeException e){
        if(!condition){
            throw e;
        }
    }
}
