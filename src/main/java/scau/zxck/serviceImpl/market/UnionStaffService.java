package scau.zxck.serviceImpl.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scau.zxck.base.dao.mybatis.Conditions;
import scau.zxck.base.exception.BaseException;
import scau.zxck.dao.market.UnionStaffDao;
import scau.zxck.entity.market.UnionStaff;
import scau.zxck.service.market.IUnionStaffService;

import java.util.List;


/**
 * Created by suruijia on 2016/1/29.
 */
@Service
public class UnionStaffService implements IUnionStaffService {
  @Autowired
  private UnionStaffDao unionStaffDao;

    @Override
    public <V> List<V> list(Conditions conditions) throws BaseException {
        return unionStaffDao.list(conditions);
    }
}
