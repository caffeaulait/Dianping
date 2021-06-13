package com.caffeaulait.dianping.service.impl;

import com.caffeaulait.dianping.common.BusinessError;
import com.caffeaulait.dianping.common.BusinessException;
import com.caffeaulait.dianping.common.CommonUtil;
import com.caffeaulait.dianping.dao.UserMapper;
import com.caffeaulait.dianping.model.User;
import com.caffeaulait.dianping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.NoSuchAlgorithmException;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUser(Integer id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional
    public User register(User user) throws BusinessException, NoSuchAlgorithmException {
        user.setPassword(CommonUtil.encodePassword(user.getPassword()));
        try {
            userMapper.insertSelective(user);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(BusinessError.REGISTER_DUPLICATE_ERROR);
        }
        return getUser(user.getId());
    }

    @Override
    public User login(String telephone, String password) throws BusinessException, NoSuchAlgorithmException {
        User user = userMapper.selectByPhoneAndPwd(telephone,
                CommonUtil.encodePassword(password));
        if (user == null){
            throw new BusinessException(BusinessError.LOGIN_FAIL);
        }
        return user;
    }

    @Override
    public Integer countAllUser() {
        return userMapper.countAllUser();
    }

}
