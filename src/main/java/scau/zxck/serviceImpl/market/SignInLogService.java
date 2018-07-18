package scau.zxck.serviceImpl.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scau.zxck.base.exception.BaseException;
import scau.zxck.dao.market.SignInLogDao;
import scau.zxck.entity.market.SignInLog;
import scau.zxck.service.market.ISignInLogService;
@Service
public class SignInLogService implements ISignInLogService {
    @Autowired
    private SignInLogDao signInLogDao;
    @Override
    public String add(SignInLog entity) throws BaseException {
        return signInLogDao.add(entity);
    }
}
