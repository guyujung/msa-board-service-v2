package com.example.demo.src.file.dto.request;


import com.example.demo.src.file.domain.Boards;
import lombok.Data;

@Data
public class BoardWriteRequest{
    private Long boardId;
    private String title;
    private String content;

public static Boards toEntity(BoardWriteRequest boardWriteRequest, Long memberId, Long teamId, Long workId) {
    return Boards.builder()
            .title(boardWriteRequest.getTitle())
            .content(boardWriteRequest.getContent())
            .memberId(memberId)
            .teamId(teamId)
            .workId(workId)
            .build();
}

}