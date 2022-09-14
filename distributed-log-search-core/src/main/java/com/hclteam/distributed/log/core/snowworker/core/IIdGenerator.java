package com.hclteam.distributed.log.core.snowworker.core;

import com.hclteam.distributed.log.core.snowworker.exception.IdGeneratorException;

public interface IIdGenerator {
    long next() throws IdGeneratorException;
}
