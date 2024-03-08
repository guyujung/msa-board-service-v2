package com.example.demo.src.file.dto.response;

import com.example.demo.src.file.domain.Boards;
import com.example.demo.src.file.vo.BoardWorkVo;
import com.example.demo.src.file.vo.MemberVo;
import com.example.demo.src.file.vo.WorkVo;

import java.util.List;

public class CombinedResponse<T> {
    private T boards;
    private T workResponse;
    private T memberVo;



    public CombinedResponse(T boards, T workResponse, T memberVo) {
        this.boards = boards;
        this.workResponse = workResponse;
        this.memberVo = memberVo;
    }
}
