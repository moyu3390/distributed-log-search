package com.hclteam.distributed.log.core.snowworker.core.impl;


import com.hclteam.distributed.log.core.snowworker.exception.IdGeneratorException;
import com.hclteam.distributed.log.core.snowworker.properties.IdGeneratorOptions;

/**
 * @description: 传统雪花漂移算法核心代码
 */
public class TraditionalSnowWorker extends DriftSnowWorker {

    //调用父类构造
    public TraditionalSnowWorker(IdGeneratorOptions options) {
        super(options);
    }

    @Override
    public long next() {
        synchronized (_SyncLock) {
            long currentTimeTick = GetCurrentTimeTick();

            //如果最后一次生成与当前时间相同
            if (_LastTimeTick == currentTimeTick) {
                //如果当前使用到的序列号已经大于最大序列号，就是用预留的插
                if (_CurrentSeqNumber++ > MaxSeqNumber) {
                    _CurrentSeqNumber = MinSeqNumber;
                    currentTimeTick = GetNextTimeTick();
                }
            } else {
                _CurrentSeqNumber = MinSeqNumber;
            }

            //如果发生了时间回拨
            if (currentTimeTick < _LastTimeTick) {
                throw new IdGeneratorException("Time error for {0} milliseconds", _LastTimeTick - currentTimeTick);
            }

            _LastTimeTick = currentTimeTick;

            //位移并返回
            return ShiftStitchingResult(currentTimeTick);
        }

    }
}
