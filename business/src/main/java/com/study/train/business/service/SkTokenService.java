package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.study.train.business.domain.SkToken;
import com.study.train.business.domain.SkTokenExample;
import com.study.train.business.enums.RedisKeyPreEnum;
import com.study.train.business.mapper.SkTokenMapper;
import com.study.train.business.mapper.cust.SkTokenMapperCust;
import com.study.train.business.req.SkTokenQueryReq;
import com.study.train.business.req.SkTokenSaveReq;
import com.study.train.business.resp.SkTokenQueryResp;
import com.study.train.common.resp.PageResp;
import com.study.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 令牌
 */
@Service
public class SkTokenService {

    private static final Logger LOG = LoggerFactory.getLogger(SkTokenService.class);

    @Resource
    private SkTokenMapper skTokenMapper;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    @Resource
    private DailyTrainStationService dailyTrainStationService;

    @Resource
    private SkTokenMapperCust skTokenMapperCust;
    @Autowired
    private RedissonClient redissonClient;
    @Value("${spring.profiles.active}")
    private String env;

    /**
     * 初始化
     */
    public void genDaily(Date date, String trainCode) {
        LOG.info("删除日期【{}】车次【{}】的令牌记录", DateUtil.formatDate(date), trainCode);
        SkTokenExample skTokenExample = new SkTokenExample();
        skTokenExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        skTokenMapper.deleteByExample(skTokenExample);

        DateTime now = DateTime.now();
        SkToken skToken = new SkToken();
        skToken.setDate(date);
        skToken.setTrainCode(trainCode);
        skToken.setId(SnowUtil.getSnowflakeNextId());
        skToken.setCreateTime(now);
        skToken.setUpdateTime(now);

        int seatCount = dailyTrainSeatService.countSeat(date, trainCode);
        LOG.info("车次【{}】座位数：{}", trainCode, seatCount);

        long stationCount = dailyTrainStationService.countByTrainCode(date, trainCode);
        LOG.info("车次【{}】到站数：{}", trainCode, stationCount);

        // 3/4需要根据实际卖票比例来定，一趟火车最多可以卖（seatCount * stationCount）张火车票
        int count = (int) (seatCount * stationCount); // * 3/4);
        LOG.info("车次【{}】初始生成令牌数：{}", trainCode, count);
        skToken.setCount(count);

        skTokenMapper.insert(skToken);
    }

    public void save(SkTokenSaveReq req) {
        DateTime now = DateTime.now();
        SkToken skToken = BeanUtil.copyProperties(req, SkToken.class);
        if (ObjectUtil.isNull(skToken.getId())) {
            skToken.setId(SnowUtil.getSnowflakeNextId());
            skToken.setCreateTime(now);
            skToken.setUpdateTime(now);
            skTokenMapper.insert(skToken);
        } else {
            skToken.setUpdateTime(now);
            skTokenMapper.updateByPrimaryKey(skToken);
        }
    }

    public PageResp<SkTokenQueryResp> queryList(SkTokenQueryReq req) {
        SkTokenExample skTokenExample = new SkTokenExample();
        skTokenExample.setOrderByClause("id desc");
        SkTokenExample.Criteria criteria = skTokenExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<SkToken> skTokenList = skTokenMapper.selectByExample(skTokenExample);

        PageInfo<SkToken> pageInfo = new PageInfo<>(skTokenList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<SkTokenQueryResp> list = BeanUtil.copyToList(skTokenList, SkTokenQueryResp.class);

        PageResp<SkTokenQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        skTokenMapper.deleteByPrimaryKey(id);
    }

    /**
     * 校验令牌
     */
    public boolean validSkToken(Date date, String trainCode, Long memberId) {
        LOG.info("会员【{}】获取日期【{}】车次【{}】的令牌开始", memberId, DateUtil.formatDate(date), trainCode);


        // 生产环境开启
        if (!env.equals("dev")) {
            // 防止同一个人刷票  先获取令牌锁，再校验令牌余量，防止机器人抢票，lockKey就是令牌，用来表示【谁能做什么】的一个凭证
            String lockKey = RedisKeyPreEnum.SK_TOKEN + "-" + DateUtil.formatDate(date) + "-" + trainCode + "-" + memberId;
            RLock lock = redissonClient.getLock(lockKey);
            // 不用等待  直接获取锁  获取不到则直接返回  ，获取到则设置5秒过期时间
            try {
                boolean tryLock = lock.tryLock(0, 5, TimeUnit.SECONDS);
                if (tryLock) {
                    LOG.info("恭喜，抢到令牌锁了！lockKey：{}", lockKey);
                } else {
                    LOG.info("很遗憾，没抢到令牌锁！lockKey：{}", lockKey);
                    return false;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


        // 将令牌数量放入redis缓存，提高速度
        String skTokenCountKey = RedisKeyPreEnum.SK_TOKEN_COUNT + "-" + DateUtil.formatDate(date) + "-" + trainCode;
        RBucket<Long> bucket = redissonClient.getBucket(skTokenCountKey);
        Long skTokenCount = bucket.get();
        if (Objects.nonNull(skTokenCount)) {
            LOG.info("缓存中有该车次令牌大闸的key：{}", skTokenCountKey);
            if (skTokenCount < 0) {
                LOG.error("获取令牌失败：{}", skTokenCountKey);
                return false;
            } else {
                LOG.info("获取令牌后，令牌余数：{}", skTokenCount);
                // 续期
                bucket.set(skTokenCount - 1, 60, TimeUnit.SECONDS);
                if (skTokenCount % 5 == 0) {
                    // 如果令牌数是5的倍数则更新数据库  ，没有每次都更新是为了考虑到性能
                    skTokenMapperCust.decrease(date, trainCode, 5);
                }
                return true;
            }


        } else {
            // 从数据查询 然后放入缓存
            LOG.info("缓存中没有该车次令牌大闸的key：{}", skTokenCountKey);
            // 检查是否还有令牌
            SkTokenExample skTokenExample = new SkTokenExample();
            skTokenExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
            List<SkToken> tokenCountList = skTokenMapper.selectByExample(skTokenExample);
            if (CollUtil.isEmpty(tokenCountList)) {
                LOG.info("找不到日期【{}】车次【{}】的令牌记录", DateUtil.formatDate(date), trainCode);
                return false;
            }
            SkToken skToken = tokenCountList.get(0);
            if (skToken.getCount() <= 0) {
                LOG.info("日期【{}】车次【{}】的令牌余量为0", DateUtil.formatDate(date), trainCode);
                return false;
            }

            // 令牌还有余量
            // 令牌余数-1
            Integer count = skToken.getCount() - 1;
            skToken.setCount(count);
            LOG.info("将该车次令牌大闸放入缓存中，key: {}， count: {}", skTokenCountKey, count);
            // 不需要更新数据库，只要放缓存即可
            bucket.set(Long.valueOf(count), 60, TimeUnit.SECONDS);
            // skTokenMapper.updateByPrimaryKey(skToken);
            return true;
        }


        // 库存令牌约等于库存，令牌没有了，就不再卖票了，不需要再进入购票主流程去判断库存，判断令牌肯定比判断库存效率高
//        int updateCount = skTokenMapperCust.decrease(date, trainCode, 1);
//        if (updateCount > 0) {
//            return true;
//        } else {
//            return false;
//        }

    }

}
