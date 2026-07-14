package com.pims.exception;

import com.pims.common.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 * 
 * @author PIMS
 */
@Getter
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    private Integer code;
    
    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.ERROR.getCode();
    }
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }
}
