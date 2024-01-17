package com.study.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.study.train.common.resp.PageResp;
import com.study.train.common.util.SnowUtil;
import com.study.train.member.domain.Ticket;
import com.study.train.member.domain.TicketExample;
import com.study.train.member.mapper.MemberMapper;
import com.study.train.member.mapper.TicketMapper;
import com.study.train.member.req.TicketQueryReq;
import com.study.train.member.req.TicketSaveReq;
import com.study.train.member.resp.TicketQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TicketService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketService.class);

    @Resource
    private TicketMapper ticketMapper;

    @Resource
    private MemberMapper memberMapper;

    /**
     * 新增或保存
     *
     * @param req
     */
    public void save(TicketSaveReq req) {
        DateTime now = DateTime.now();
        Ticket ticket = BeanUtil.copyProperties(req, Ticket.class);
        if (ObjectUtil.isNull(ticket.getId())) {
            ticket.setId(SnowUtil.getSnowflakeNextId());
            ticket.setCreateTime(now);
            ticket.setUpdateTime(now);
            ticketMapper.insert(ticket);
        } else {
            ticket.setUpdateTime(now);
            ticketMapper.updateByPrimaryKey(ticket);
        }

    }


    public PageResp<TicketQueryResp> queryList(TicketQueryReq req) {
        TicketExample ticketExample = new TicketExample();
        TicketExample.Criteria criteria = ticketExample.createCriteria();


        Page<Object> page = PageHelper.startPage(req.getPage(), req.getSize());
        List<Ticket> ticketList = ticketMapper.selectByExample(ticketExample);

        List<TicketQueryResp> list = BeanUtil.copyToList(ticketList, TicketQueryResp.class);
        PageResp<TicketQueryResp> resp = new PageResp<>();
        resp.setTotal(page.getTotal());
        resp.setList(list);
        return resp;
    }


    public void delete(Long id) {
        ticketMapper.deleteByPrimaryKey(id);
    }

}
