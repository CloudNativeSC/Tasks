package cloudnative.spring.global.exception.handler;

import cloudnative.spring.global.exception.GeneralException;
import cloudnative.spring.global.response.BaseErrorCode;

public class GeneralHandler extends GeneralException {
    public GeneralHandler(BaseErrorCode code) {
        super(code);
    }
}