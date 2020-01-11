package learn.account_annoioc_withoutxml.service;

import learn.account_annoioc_withoutxml.domain.Account;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 账户的业务层接口
 */

public interface IAccountService {

    /**
     * 查询所有
     * @return
     */
    List<Account> findAllAccount();

    /**
     * 查询一个
     * @return
     */
    Account findAccountById(Integer accountId);

    /**
     * 保存
     * @param account
     */
    void saveAccount(Account account);

    /**
     * 更新
     * @param account
     */
    void updateAccount(Account account);

    /**
     * 删除
     * @param acccountId
     */
    void deleteAccount(Integer acccountId);


}
