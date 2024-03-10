package com.example.demo.src.file.controller;

import com.example.demo.common.code.CommonCode;
import com.example.demo.common.response.Response;

import com.example.demo.src.file.Service.AlarmService;

import com.example.demo.src.file.Service.BoardService;
import com.example.demo.src.file.dto.response.AlarmDetailResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AlarmController {

    private  final AlarmService alarmService;


    @Autowired
    public AlarmController(AlarmService alarmService) {
        this.alarmService = alarmService;
    }



    //멤버별로 알람 리스트 확인하기
    @GetMapping("/alarmList/view/{memberId}")
    public ResponseEntity<Response<List<AlarmDetailResponse>>> AlarmList(@PathVariable(value = "memberId") Long memberId) {
        return ResponseEntity.ok(Response.of(CommonCode.GOOD_REQUEST, alarmService.alarmList(memberId)));
    }

    @PutMapping("/updateSeenStatus/{alarmId}")
    public void updateSeenStatus(@PathVariable(value="alarmId") Long alarmId) {
        alarmService.updateSeenStatus(alarmId);
    }


}


