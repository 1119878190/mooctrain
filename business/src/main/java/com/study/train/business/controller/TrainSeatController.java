package com.study.train.business.controller;



import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import com.study.train.business.req.TrainSeatQueryReq;
import com.study.train.business.req.TrainSeatSaveReq;
import com.study.train.business.resp.TrainSeatQueryResp;
import com.study.train.business.service.TrainSeatService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/train-seat")
public class TrainSeatController {

    @Resource
    private TrainSeatService trainSeatService;

    /**
     * 保存乘车人信息
     *
     * @param req
     * @return
     */
    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody TrainSeatSaveReq req) {
        trainSeatService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainSeatQueryResp>> queryList(@Valid TrainSeatQueryReq req) {
        return new CommonResp<>(trainSeatService.queryList(req));
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainSeatService.delete(id);
        return new CommonResp<>();
    }

}
