package com.example.demo.common.code;

import com.example.demo.common.response.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardCode implements ResponseCode {
    BOARD_ALEADY_WRITE(1105, "게시판이 이미 작성되었습니다.")
            ;


    private final int code;
    private final String message;

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
