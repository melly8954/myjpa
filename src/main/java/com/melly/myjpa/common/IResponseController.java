package com.melly.myjpa.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface IResponseController {
    default ResponseEntity<ResponseDto> makeResponseEntity(HttpStatus httpStatus, String message, Object responseData) {
        ResponseDto responseDto = ResponseDto.builder()
                .message(message)
                .responseData(responseData)
                .build();
        return ResponseEntity.status(httpStatus).body(responseDto);

    }

}
