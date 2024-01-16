package com.study.train.business.service;

import com.study.train.business.domain.ConfirmOrder;
import com.study.train.business.domain.DailyTrainSeat;
import com.study.train.business.domain.DailyTrainTicket;
import com.study.train.business.enums.ConfirmOrderStatusEnum;
import com.study.train.business.mapper.ConfirmOrderMapper;
import com.study.train.business.mapper.DailyTrainSeatMapper;
import com.study.train.business.req.ConfirmOrderTicketReq;
import com.study.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AfterConfirmOrderService {


    private static final Logger LOG = LoggerFactory.getLogger(AfterConfirmOrderService.class);

    @Resource
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    /**
     * 选中座位后事务处理：
     * 座位表修改售卖情况sell；
     * 余票详情表修改余票；
     * 为会员增加购票记录
     * 更新确认订单为成功
     */
    @Transactional
    public void afterDoConfirm( List<DailyTrainSeat> finalSeatList)  {
        // LOG.info("seata全局事务ID: {}", RootContext.getXID());
        for (int j = 0; j < finalSeatList.size(); j++) {
            DailyTrainSeat dailyTrainSeat = finalSeatList.get(j);
            DailyTrainSeat seatForUpdate = new DailyTrainSeat();
            seatForUpdate.setId(dailyTrainSeat.getId());
            seatForUpdate.setSell(dailyTrainSeat.getSell());
            seatForUpdate.setUpdateTime(new Date());
            dailyTrainSeatMapper.updateByPrimaryKeySelective(seatForUpdate);
        }
    }
}
