package com.example.demo;

import com.example.demo.common.code.CommonCode;
import com.example.demo.common.response.Response;
import com.example.demo.src.file.Repository.BoardRepository;
import com.example.demo.src.file.Service.AlarmService;
import com.example.demo.src.file.controller.AlarmController;


import com.example.demo.src.file.dto.response.AlarmDetailResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class AlarmControllerTest {


    @Mock
    private AlarmService alarmService;

    @InjectMocks
    private AlarmController alarmController;

    private MockMvc mockMvc;

     @BeforeEach
        public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(alarmController).build();
      }

    @DisplayName("알람 리스트 조회")
    @Test
    public void testAlarmList() throws Exception {
        // given
        Long memberId =1L;
        List<AlarmDetailResponse> mockResponseList = userList();

        when(alarmService.alarmList(anyLong())).thenReturn(mockResponseList);

        //when
        mockMvc.perform(
                MockMvcRequestBuilders.get("/alarmList/view/{memberId}", memberId)
        )
                .andExpect(status().isOk())

                // then
                .andExpect(jsonPath("$.status.code").value(CommonCode.GOOD_REQUEST.getCode()))
                .andExpect(jsonPath("$.status.message").value(CommonCode.GOOD_REQUEST.getMessage()))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].content").value(mockResponseList.get(0).getContent()))
                .andExpect(jsonPath("$.content[1].content").value(mockResponseList.get(1).getContent()));

    }


    private List<AlarmDetailResponse> userList() {
     List<AlarmDetailResponse> userList = new ArrayList<>();
    for (int i = 0; i < 2; i++) {
    userList.add(new AlarmDetailResponse(Long.valueOf(i),"20201572 구유정'님께서 '[k]sdf'에 대해 새로운 글을 등록 하였습니다.","/board/view/1","https://lh3.googleusercontent.com/a/ACg8ocK6KegVWvdbmToigFN-5bk-BEhPM7HHXxojVsm7h1WC=s96-c","newWrite",1L,3L));
     }
    return userList;
    }



}