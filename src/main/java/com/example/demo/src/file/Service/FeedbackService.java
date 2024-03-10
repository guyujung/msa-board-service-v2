package com.example.demo.src.file.Service;



import com.example.demo.src.file.Repository.FeedbackRepository;
import com.example.demo.src.file.client.*;
import com.example.demo.src.file.domain.Boards;
import com.example.demo.src.file.domain.FeedbackStatuses;
import com.example.demo.src.file.domain.Feedbacks;
import com.example.demo.src.file.dto.request.FeedbackRequest;
import com.example.demo.src.file.dto.response.BoardFeedbackResponse;
import com.example.demo.src.file.dto.response.CombinedListResponse;
import com.example.demo.src.file.dto.response.FeedbackResponse;
import com.example.demo.src.file.dto.response.FeedbackStatusFeedbackYnUserIdResponse;
import com.example.demo.src.file.vo.MemberVo;
import com.example.demo.src.file.vo.TeamMemberVo;
import com.example.demo.src.file.vo.WorkVo;
import com.example.demo.src.file.vo.WorkersVo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;


@Service

public class FeedbackService {


    private final FeedbackRepository feedbackRepository;
    private final BoardService boardService;
    private final FeedbackStatusService feedbackStatusService;
    private final AlarmService alarmService;
    @Autowired
    WorkerServiceClient workerServiceClient;
    @Autowired
    TeamServiceClient teamServiceClient;
    @Autowired
    MemberServiceClient memberServiceClient;

    @Autowired
    public FeedbackService(FeedbackRepository feedbackRepository,BoardService boardService,FeedbackStatusService feedbackStatusService,AlarmService alarmService){
        this.feedbackRepository=feedbackRepository;
        this.boardService=boardService;
        this.feedbackStatusService=feedbackStatusService;
        this.alarmService=alarmService;

    }



    //피드백 작성
    public FeedbackResponse save(Long boardId, Long memberId, FeedbackRequest requset, Integer isApproved) {

        Boards boards=boardService.findBoardById(boardId);
        MemberVo writers = memberServiceClient.findByUserId(memberId);//피드백 작성자
        WorkVo workResponse=workerServiceClient.findWorkById(boards.getWorkId());

        Feedbacks feedbacks=OneTeaamMemberAgreeCheck(requset,isApproved, boards, writers,workResponse);//피드백 메시지 생성 및 동의여부에 따른 피드백 상태 갱신

        AllTeamMemberAgreeCheck(boards,workResponse);// 팀원 모두 동의하면 boards의 feedback_yn=true로 변경

        return FeedbackResponse.from(feedbacks, boards,writers);
    }


    public Feedbacks OneTeaamMemberAgreeCheck(FeedbackRequest request, Integer isApproved, Boards boards, MemberVo writers, WorkVo work) {
        FeedbackStatuses feedbackStatuses = feedbackStatusService.findFeedbackStatusByBoardsIdAndUsersId(boards.getId(), writers.getId());
        Feedbacks feedbacks =new Feedbacks(request.getFeedbackId(), request.getComment(),boards,writers.getId());
        if (feedbackStatuses.getFeedbackYn() != 0||feedbackStatuses.getFeedbackYn() == 3)return feedbacks; //한번도 피드백을 이미 한 경우 예외처리, 글작성 본인인 경우

        TeamMemberVo teamMembers = teamServiceClient.findByTeamsIdAndUsersId(boards.getTeamId(),boards.getUserId());
        teamMembers.setContribution(1);//피드백 점수 1점 주기
        teamServiceClient.addContribution(teamMembers);

        if (isApproved == 1) {feedbacks.feedbackAgree();}    // 피드백을 처음 달았고 승인한 경우 modReq=true로 바꾸기
        else if ((isApproved == 2)) {feedbacks.feedbackDeny();}; //피드백에서 수정 요청 시  수정 요청한 본인 제외 모든 팀의 모든 팀원들에게 알람이 감
        feedbackRepository.save(feedbacks);
        feedbackStatusService.UpdateFeedbackStatusAndAlarm(feedbackStatuses,isApproved,boards, writers, work); //피드백 상태 갱신

        return feedbacks;
    }


    //게시글 당 피드백 완료시 작업 점수
    public void AllTeamMemberAgreeCheck(Boards boards, WorkVo work) {
        List<FeedbackStatuses> feedbackStatusesList = feedbackStatusService.findFeedbackStatusByBoardId(boards.getId());// Board에 해당하는 모든 FeedbackStatuses 조회
        boolean hasFeedbackYnTrue = true;
        for (FeedbackStatuses feedbackStatuses : feedbackStatusesList) {// 모든 FeedbackStatuses의 feedback_yn이 true인지 확인
            if (feedbackStatuses.getFeedbackYn() == 0 || feedbackStatuses.getFeedbackYn() == 2) { //한명이라도 feedback을 안했으면
                hasFeedbackYnTrue = false;
                break;
            }
        }

            if (hasFeedbackYnTrue && boards.isFeedbackYn() == false) {// 모든 FeedbackStatuses의 feedback_yn이 true라면 board의 feedback_yn도 true로 변경
                long hoursDifference = ChronoUnit.HOURS.between( work.getEndDate(),boards.getCreatedAt());
                TeamMemberVo teamMembers = teamServiceClient.findByTeamsIdAndUsersId(boards.getTeamId(),boards.getUserId());
                float importance =  work.getImportance();

                if (work.getEndDate().isAfter(boards.getCreatedAt())) {//마감일 이내
                    teamMembers.setContribution(importance);
                    teamServiceClient.addContribution(teamMembers);
                }else if (0<hoursDifference && hoursDifference <= 24) {// 작성자가 마감일이 지난 후 24시간 이내에 작성했으면 점수를 절반만 받을 수 있음
                    teamMembers.setContribution(importance/2);
                    teamServiceClient.addContribution(teamMembers);
                }
                boardService.finishFeedback(boards);//게시물 작업 완료 표시
                List<MemberVo> allMembers = teamServiceClient.findTeamById(boards.getTeamId());
                alarmService.createAlarmMessageNofilter( boards,  work, allMembers, "complFeedback");//팀원 모두에게 피드백 완료 알람 전송
                AllWorkComplCheck(boards, work); //workId에 해당하는 모든 게시판 피드백이 완료 되었을시 work의 status를 4로 변경
            }

    }


    //작업에 해당되는 boards 모두 완료시 work status 4로 업데이트
    public void AllWorkComplCheck(Boards boards, WorkVo workResponse) {
        List<WorkersVo> workersVoList =workerServiceClient.findWorkerById(workResponse.getId());
        List<Long> userIdList = workersVoList.stream()
                .map(WorkersVo::getUserId)
                .collect(Collectors.toList());

        List<Boards> boardList =boardService.findBoardListByUserIdListAndWorkId(userIdList,workResponse.getId());

        boolean hasFeedbackYnTrue = true;
        for (Boards board : boardList) {   // 모든 FeedbackStatuses의 feedback_yn이 true인지 확인
            if (board.isFeedbackYn() == false||(boardList.size()!=userIdList.size())) { //올린 게시물 하나라도 피드백 완료상태가 아니거나, 한 작업의 담당자수와 작성된 게시물 수가 다르면 완료(4)가 아님
                hasFeedbackYnTrue = false; break;
            }

            if (hasFeedbackYnTrue && workResponse.getStatus() != 4) {// 모든 BoardList들의 feedback_yn이 true라면 work의 status를 4로 변경
                workerServiceClient.setWorkStatus(workResponse.getId(), 4);
            }
        }
    }



    public void reFeedback(Long boardId, Long memberId, Integer isApproved) { //재수락할지, 거절할지
        Boards boards = boardService.findBoardById(boardId);
        MemberVo writers=memberServiceClient.findByUserId(memberId);
        WorkVo work=workerServiceClient.findWorkById(boards.getWorkId());
        List<MemberVo> allMembers = teamServiceClient.findTeamById(boards.getTeamId());
        FeedbackStatuses feedbackStatus =feedbackStatusService.findFeedbackStatusByBoardsIdAndUsersId(boardId,memberId); //boards, writers로 feedbackStatus찾기

        if (isApproved == 1) {
            feedbackStatus.feedbackAgree();     // 승인한 경우 feedbackYn=true로 바꾸기
            alarmService.createAlarmMessage(boards,writers,work,allMembers,"agreeFeedback",writers.getId(),null);//게시판 작성자에게 수락 알람이 가도록 함
        } else if ((isApproved == 2)) {
            feedbackStatus.feedbackDeny();     // 거부한 경우 feedbackYn=false로 바꾸기
            alarmService.createAlarmMessage(boards,writers,work,allMembers,"denyFeedback",writers.getId(),null);//게시판 작성자에게 거절 알람이 가도록 함
        }

        AllTeamMemberAgreeCheck(boards, work);  // 팀원 모두 동의하면 boards의 feedback_yn=true로 변경
    }


    public CombinedListResponse feedbackView(Long teamId, Long boardId){
    List<MemberVo> teamMembers=teamServiceClient.findTeamById(teamId);
    List<FeedbackStatusFeedbackYnUserIdResponse> feedbackStatusesList = feedbackStatusService.findFeedbackYnAndUserIdByBoardsId (boardId);
    List<BoardFeedbackResponse> feedbacksList = feedbackRepository.findFeedbackByBoardsId(boardId);
    return new CombinedListResponse(teamMembers,feedbackStatusesList,feedbacksList);
    }
}
