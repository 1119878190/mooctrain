package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.study.train.common.resp.PageResp;
import com.study.train.common.util.SnowUtil;
import com.study.train.business.domain.TrainSeat;
import com.study.train.business.domain.TrainSeatExample;
import com.study.train.business.mapper.TrainSeatMapper;
import com.study.train.business.req.TrainSeatQueryReq;
import com.study.train.business.req.TrainSeatSaveReq;
import com.study.train.business.resp.TrainSeatQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 乘车人 业务层
 */
@Service
public class TrainSeatService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainSeatService.class);

    @Resource
    private TrainSeatMapper trainSeatMapper;


    /**
     * 新增或保存
     *
     * @param req
     */
    public void save(TrainSeatSaveReq req) {
        DateTime now = DateTime.now();
        TrainSeat trainSeat = BeanUtil.copyProperties(req, TrainSeat.class);
        if (ObjectUtil.isNull(trainSeat.getId())) {
            trainSeat.setId(SnowUtil.getSnowflakeNextId());
            trainSeat.setCreateTime(now);
            trainSeat.setUpdateTime(now);
            trainSeatMapper.insert(trainSeat);
        } else {
            trainSeat.setUpdateTime(now);
            trainSeatMapper.updateByPrimaryKey(trainSeat);
        }

    }


    public PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq req) {
        TrainSeatExample trainSeatExample = new TrainSeatExample();
        TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();


        Page<Object> page = PageHelper.startPage(req.getPage(), req.getSize());
        List<TrainSeat> trainSeatList = trainSeatMapper.selectByExample(trainSeatExample);

        List<TrainSeatQueryResp> list = BeanUtil.copyToList(trainSeatList, TrainSeatQueryResp.class);
        PageResp<TrainSeatQueryResp> resp = new PageResp<>();
        resp.setTotal(page.getTotal());
        resp.setList(list);
        return resp;
    }


    public void delete(Long id) {
        trainSeatMapper.deleteByPrimaryKey(id);
    }

}
