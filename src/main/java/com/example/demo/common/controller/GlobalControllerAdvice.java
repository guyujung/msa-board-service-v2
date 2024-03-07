package com.example.demo.common.controller;


import com.example.demo.common.code.BoardCode;
import com.example.demo.common.exception.BoardAlreadyWriteException;
import com.example.demo.common.response.Response;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(BoardAlreadyWriteException.class)
    public ResponseEntity<Response> handleBoardAlreadyWriteException() {
        return ResponseEntity.ok().body(Response.of(BoardCode.BOARD_ALEADY_WRITE, null));
    }




}
