package cn.hdfk7.boot.starter.common.seata;

import cn.hutool.core.util.StrUtil;
import io.seata.core.context.RootContext;
import io.seata.core.exception.TransactionException;
import io.seata.tm.api.GlobalTransactionContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SeataRollback {
    public void rollback(Throwable e) throws TransactionException {
        if (StrUtil.isNotEmpty(RootContext.getXID())) {
            GlobalTransactionContext.reload(RootContext.getXID()).rollback();
        }
    }
}
