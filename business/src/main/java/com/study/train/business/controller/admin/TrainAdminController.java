package com.study.train.business.controller.admin;


import com.study.train.business.req.TrainQueryReq;
import com.study.train.business.req.TrainSaveReq;
import com.study.train.business.resp.TrainQueryResp;
import com.study.train.business.service.TrainService;
import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/train")
public class TrainAdminController {

    @Resource
    private TrainService trainService;

    /**
     * 保存乘车人信息
     *
     * @param req
     * @return
     */
    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody TrainSaveReq req) {
        trainService.save(req);
        return new CommonResp<>();
    }

    @PostMapping("/query-list")
    public CommonResp<PageResp<TrainQueryResp>> queryList(@Valid @RequestBody TrainQueryReq req) {
        return new CommonResp<>(trainService.queryList(req));
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainService.delete(id);
        return new CommonResp<>();
    }

}
