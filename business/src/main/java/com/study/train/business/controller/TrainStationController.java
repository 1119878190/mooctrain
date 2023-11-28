package com.study.train.business.controller;



import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import com.study.train.business.req.TrainStationQueryReq;
import com.study.train.business.req.TrainStationSaveReq;
import com.study.train.business.resp.TrainStationQueryResp;
import com.study.train.business.service.TrainStationService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/train-station")
public class TrainStationController {

    @Resource
    private TrainStationService trainStationService;

    /**
     * 保存乘车人信息
     *
     * @param req
     * @return
     */
    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody TrainStationSaveReq req) {
        trainStationService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainStationQueryResp>> queryList(@Valid TrainStationQueryReq req) {
        return new CommonResp<>(trainStationService.queryList(req));
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainStationService.delete(id);
        return new CommonResp<>();
    }

}
