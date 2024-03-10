package com.example.demo;

import com.example.demo.src.file.Repository.AlarmRepository;
import com.example.demo.src.file.Service.AlarmService;
import com.example.demo.src.file.domain.Alarms;
import com.example.demo.src.file.dto.response.AlarmDetailResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlarmServiceTest {
    @InjectMocks
    private AlarmService alarmService;

    @Mock
    private AlarmRepository alarmRepository;
    private MockMvc mockMvc;


    @DisplayName("Test Alarm List")
    @Test
    void testAlarmList() {
        // given
        Long memberId = 1L;
        List<Object[]> mockResults = createMockResults();
        when(alarmRepository.findAlarmsWithFeedbackYn(memberId)).thenReturn(mockResults);

        // when
        List<AlarmDetailResponse> result = alarmService.alarmList(memberId);

        // then
        verify(alarmRepository, times(1)).findAlarmsWithFeedbackYn(memberId);
        assertEquals(2, result.size());


        assertEquals(1L, result.get(0).getAlarmId());
        assertEquals("/board/view/1", result.get(0).getRedirectUrl());
        assertEquals(0, result.get(0).getFeedbackYn());
        assertEquals(2L, result.get(1).getAlarmId());
        assertEquals(1, result.get(1).getFeedbackYn());


    }

    private List<Object[]> createMockResults() {
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{new Alarms(1L,"20201572 구유정'님께서 '[k]sdf'에 대해 새로운 글을 등록 하였습니다.","/board/view/1","https://lh3.googleusercontent.com/a/ACg8ocK6KegVWvdbmToigFN-5bk-BEhPM7HHXxojVsm7h1WC=s96-c","newWrite",1L,3L,1L), 0});
        mockResults.add(new Object[]{new Alarms(2L,"20201572 구유정'님께서 '[k]sdf'에 대해 새로운 글을 등록 하였습니다.","/board/view/1","https://lh3.googleusercontent.com/a/ACg8ocK6KegVWvdbmToigFN-5bk-BEhPM7HHXxojVsm7h1WC=s96-c","newWrite",1L,3L,1L), 1});
        return mockResults;
    }


}
