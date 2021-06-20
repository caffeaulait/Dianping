package com.caffeaulait.dianping.recommend;

import com.caffeaulait.dianping.dao.RecommendDOMapper;
import com.caffeaulait.dianping.model.RecommendDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendService implements Serializable {

    @Autowired
    private RecommendDOMapper recommendDOMapper;

    public List<Integer> recall(Integer userId) {
        RecommendDO recommendDO = recommendDOMapper.selectByPrimaryKey(userId);
        if (recommendDO == null) {
            recommendDO = recommendDOMapper.selectByPrimaryKey(999999);
        }
        List<Integer> shops = Arrays.stream(recommendDO.getShops().split(",")).map(Integer::parseInt).collect(Collectors.toList());
        return shops;
    }
}
