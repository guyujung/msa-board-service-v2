package com.example.demo.src.file.dto.response;

import com.example.demo.src.file.vo.BoardWorkVo;
import com.example.demo.src.file.vo.MemberVo;

import java.util.List;

public class CombinedListResponse<T> {
    private List<T> boardResponses;
    private List<T> boardWorkVos;
    private List<T> memberVos;

    public CombinedListResponse(List<T> boardResponses, List<T> boardWorkVos, List<T> memberVos) {
        this.boardResponses = boardResponses;
        this.boardWorkVos = boardWorkVos;
        this.memberVos = memberVos;
    }
}
