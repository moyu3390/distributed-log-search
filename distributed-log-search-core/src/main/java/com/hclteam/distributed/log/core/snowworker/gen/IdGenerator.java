package com.hclteam.distributed.log.core.snowworker.gen;


import com.hclteam.distributed.log.core.snowworker.core.IIdGenerator;
import com.hclteam.distributed.log.core.snowworker.core.ISnowWorker;
import com.hclteam.distributed.log.core.snowworker.core.impl.DriftSnowWorker;
import com.hclteam.distributed.log.core.snowworker.core.impl.TraditionalSnowWorker;
import com.hclteam.distributed.log.core.snowworker.exception.IdGeneratorException;
import com.hclteam.distributed.log.core.snowworker.properties.IdGeneratorOptions;

/**
 * @description: 构建配置类，检查参数是否合法，根据参数构建算法生成器
 */
public class IdGenerator implements IIdGenerator {
    private static ISnowWorker _SnowWorker = null;

    /**
     * 构造函数，检查参数是否都合法
     *
     * @throws IdGeneratorException
     */
    public IdGenerator(IdGeneratorOptions options) throws IdGeneratorException {
        // 1.BaseTime
        if (options.BaseTime < 315504000000L || options.BaseTime > System.currentTimeMillis()) {
            throw new IdGeneratorException("BaseTime error.");
        }

        // 2.WorkerIdBitLength
        if (options.WorkerIdBitLength <= 0) {
            throw new IdGeneratorException("WorkerIdBitLength error.(range:[1, 21])");
        }
        if (options.WorkerIdBitLength + options.SeqBitLength + options.DataCenterIdBitLength > 22) {
            throw new IdGeneratorException("error：WorkerIdBitLength + SeqBitLength + DataCenterIdBitLength <= 22");
        }

        // 3.WorkerId
        int maxWorkerIdNumber = (1 << options.WorkerIdBitLength) - 1;
        if (maxWorkerIdNumber == 0) {
            maxWorkerIdNumber = 63;
        }
        if (options.WorkerId < 0 || options.WorkerId > maxWorkerIdNumber) {
            throw new IdGeneratorException("WorkerId error. (range:[0, " + (maxWorkerIdNumber > 0 ? maxWorkerIdNumber : 63) + "]");
        }

        // 4. DataCenterId
        int maxDataCenterId = (1 << options.DataCenterIdBitLength) - 1;
        if (options.DataCenterId < 0 || options.DataCenterId > maxDataCenterId){
            throw new IdGeneratorException("DataCenterId error. (range:[0,"+ maxDataCenterId + "])");
        }


        // 5.SeqBitLength
        if (options.SeqBitLength < 2 || options.SeqBitLength > 21) {
            throw new IdGeneratorException("SeqBitLength error. (range:[2, 21])");
        }

        // 6.MaxSeqNumber
        int maxSeqNumber = (1 << options.SeqBitLength) - 1;
        if (maxSeqNumber == 0) {
            maxSeqNumber = 63;
        }
        if (options.MaxSeqNumber < 0 || options.MaxSeqNumber > maxSeqNumber) {
            throw new IdGeneratorException("MaxSeqNumber error. (range:[1, " + maxSeqNumber + "]");
        }

        // 7.MinSeqNumber
        if (options.MinSeqNumber < 5 || options.MinSeqNumber > maxSeqNumber) {
            throw new IdGeneratorException("MinSeqNumber error. (range:[5, " + maxSeqNumber + "]");
        }

        //判断是构建雪花漂移算法还是普通雪花算法
        switch (options.Method) {
            case 2:
                _SnowWorker = new DriftSnowWorker(options);
                break;
            case 1:
            default:
                _SnowWorker = new TraditionalSnowWorker(options);
                break;
        }

        if (options.Method == 1) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public long next() {
        return _SnowWorker.next();
    }
}
