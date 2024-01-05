package com.study.train.business.controller;



import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import com.study.train.business.req.DailyTrainSeatQueryReq;
import com.study.train.business.req.DailyTrainSeatSaveReq;
import com.study.train.business.resp.DailyTrainSeatQueryResp;
import com.study.train.business.service.DailyTrainSeatService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/daily-train-seat")
public class DailyTrainSeatController {

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    /**
     * 保存乘车人信息
     *
     * @param req
     * @return
     */
    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainSeatSaveReq req) {
        dailyTrainSeatService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainSeatQueryResp>> queryList(@Valid DailyTrainSeatQueryReq req) {
        return new CommonResp<>(dailyTrainSeatService.queryList(req));
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainSeatService.delete(id);
        return new CommonResp<>();
    }

}
