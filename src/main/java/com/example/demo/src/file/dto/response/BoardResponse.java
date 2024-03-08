package com.example.demo.src.file.dto.response;


import com.example.demo.src.file.domain.Boards;
import com.example.demo.src.file.domain.FeedbackStatuses;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;


@Data
public class BoardResponse {

private Long boardId;
private String title;
private LocalDateTime createdTime;
private Long viewCount;
private Integer feedbackYn;
private Long userId;
private Long workId;



    @Builder
    public BoardResponse(Boards boards){
        this.boardId = boards.getId();
        this.title = boards.getTitle();
        this.viewCount=boards.getViewCnt();
        this.createdTime=boards.getCreatedAt();
        this.userId=boards.getUserId();
        this.workId=boards.getWorkId();
    }

    @Builder
    public BoardResponse(Boards boards,Long memberId){
        this.boardId = boards.getId();
        this.title = boards.getTitle();
        this.viewCount=boards.getViewCnt();
        this.createdTime=boards.getCreatedAt();
        // FeedbackStatusList 필터링
        this.feedbackYn = boards.getFeedbackStatusList()
                .stream()
                .filter(feedbackStatus -> feedbackStatus.getUserId().equals(memberId))
                .map(FeedbackStatuses::getFeedbackYn)
                .findFirst()
                .orElse(null); // Set null if no matching element is found

        this.userId=boards.getUserId();
        this.workId=boards.getWorkId();

    }


}
