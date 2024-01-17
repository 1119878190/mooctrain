package com.study.train.member.controller;



import com.study.train.common.req.MemberTicketReq;
import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import com.study.train.member.req.TicketQueryReq;
import com.study.train.member.req.TicketSaveReq;
import com.study.train.member.resp.TicketQueryResp;
import com.study.train.member.service.TicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ticket")
public class TicketController {

    @Resource
    private TicketService ticketService;


    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody MemberTicketReq req) throws Exception {
        ticketService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TicketQueryResp>> queryList(@Valid TicketQueryReq req) {
        return new CommonResp<>(ticketService.queryList(req));
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        ticketService.delete(id);
        return new CommonResp<>();
    }

}
