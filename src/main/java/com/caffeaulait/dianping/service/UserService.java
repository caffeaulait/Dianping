package com.caffeaulait.dianping.service;

import com.caffeaulait.dianping.common.BusinessException;
import com.caffeaulait.dianping.model.User;

import java.security.NoSuchAlgorithmException;

public interface UserService {

    User getUser(Integer id);

    User register(User user) throws BusinessException, NoSuchAlgorithmException;

    User login(String telephone, String password) throws BusinessException, NoSuchAlgorithmException;

    Integer countAll();
}
