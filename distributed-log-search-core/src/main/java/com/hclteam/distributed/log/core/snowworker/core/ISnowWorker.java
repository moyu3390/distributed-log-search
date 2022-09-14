package com.hclteam.distributed.log.core.snowworker.core;

import com.hclteam.distributed.log.core.snowworker.exception.IdGeneratorException;

public interface ISnowWorker {
    long next() throws IdGeneratorException;
}
