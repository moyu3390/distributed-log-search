package com.hclteam.distributed.log.core.snowworker.exception;

/**
 * @description: 自定义异常
 */
public class IdGeneratorException extends RuntimeException {

    public IdGeneratorException(String msgFormat, Object... args) {
        super(String.format(msgFormat, args));
    }
//    public IdGeneratorException(String errorCode, Throwable cause, Object... args) {
//        super(errorCode, cause, args);
//    }


    public IdGeneratorException(String message) {
        super(message);
    }

    public IdGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdGeneratorException(Throwable cause) {
        super(cause);
    }
}
