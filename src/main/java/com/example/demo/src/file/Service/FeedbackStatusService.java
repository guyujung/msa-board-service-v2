package com.example.demo.src.file.Service;

import com.example.demo.src.file.Repository.FeedbackStatusRepository;
import com.example.demo.src.file.client.TeamServiceClient;
import com.example.demo.src.file.domain.Boards;
import com.example.demo.src.file.domain.FeedbackStatuses;
import com.example.demo.src.file.dto.response.FeedbackStatusFeedbackYnUserIdResponse;
import com.example.demo.src.file.vo.MemberVo;
import com.example.demo.src.file.vo.WorkVo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class FeedbackStatusService {
    private final FeedbackStatusRepository feedbackStatusRepository;
    private final AlarmService alarmService;
    @Autowired
    TeamServiceClient teamServiceClient;
    public FeedbackStatusService(FeedbackStatusRepository feedbackStatusService,AlarmService alarmService){
        this.feedbackStatusRepository=feedbackStatusService;
        this.alarmService=alarmService;

    }




    void createFeedbackStatus(List<MemberVo> allMembers , Boards boards,Long teamId, Long writerId){
        List<FeedbackStatuses> feedbackStatusesList = allMembers.stream().map(member -> {
            FeedbackStatuses feedbackStatuses = new FeedbackStatuses(boards,member.getId(),teamId,member.getId().equals(writerId) ? 3 : 0);
            return feedbackStatuses;
        }).collect(Collectors.toList());
        feedbackStatusRepository.saveAll(feedbackStatusesList);
    }

    List<FeedbackStatuses> findFeedbackStatusByBoardId(Long boardId){
        return  feedbackStatusRepository.findByBoardsId(boardId);
    }


    FeedbackStatuses findFeedbackStatusByBoardsIdAndUsersId(Long boardId, Long writerId){
        return feedbackStatusRepository.findByBoardsIdAndUsersId(boardId, writerId);
    }



    List<FeedbackStatusFeedbackYnUserIdResponse>  findFeedbackYnAndUserIdByBoardsId (Long boardId){
    return  feedbackStatusRepository.findFeedbackYnAndUserIdByBoardsId(boardId);
    }

    //피드백 상태 업데이트
    public void UpdateFeedbackStatusAndAlarm(FeedbackStatuses feedbackStatuses, Integer isApproved,Boards boards,MemberVo writers, WorkVo work) {
        if (isApproved == 1) {feedbackStatuses.feedbackAgree();}      // 동의 시 feedback_yn=1
        else if ((isApproved == 2)) {feedbackStatuses.feedbackDeny();// , 비동의 시 feedback_yn=2
            List<MemberVo> allMembers = teamServiceClient.findTeamById(boards.getTeamId());
            alarmService.createAlarmMessage(boards, writers, work, allMembers, "requestFeedback",boards.getUserId(),null);//수정 요청 알람
        }
        feedbackStatusRepository.save(feedbackStatuses);
    }



}
