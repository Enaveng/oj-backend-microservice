package com.oj.questionservice.manager;

import com.oj.common.common.ErrorCode;
import com.oj.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class RedissonLimitManager {
    @Resource
    private RedissonClient redissonClient;

    //限流配置
    public void doRateLimit(String key) {
        //创建一个限流器
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        //配置限流规则
        boolean setRate = rateLimiter.trySetRate(RateType.OVERALL, 1, 1, RateIntervalUnit.SECONDS);
        if (setRate) {
            log.info("init rate = {}, interval = {}", rateLimiter.getConfig().getRate(), rateLimiter.getConfig().getRateInterval());
        }
        // 每当一个操作来了后，请求一个令牌  参数为每次请求允许请求的令牌数量  tryAcquire是非阻塞的 请求令牌失败时立刻返回false
        boolean canOp = rateLimiter.tryAcquire(1);
        //rateLimiter.acquire(); acquire在令牌获取失败时会阻塞当前线程
        if (!canOp) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
    }
}
