package com.example.demo.src.file.Service;

import com.example.demo.src.file.Repository.AlarmRepository;
import com.example.demo.src.file.domain.Alarms;
import com.example.demo.src.file.domain.Boards;
import com.example.demo.src.file.domain.FeedbackStatuses;
import com.example.demo.src.file.dto.response.AlarmDetailResponse;
import com.example.demo.src.file.vo.MemberVo;
import com.example.demo.src.file.vo.WorkVo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class AlarmService {
    private final AlarmRepository alarmRepository;

    @Autowired
    public AlarmService(AlarmRepository alarmRepository){
        this.alarmRepository=alarmRepository;
    }

    //알람 리스트 처리리
    public List<AlarmDetailResponse> alarmList(Long memberId) {
        List<Object[]> results = alarmRepository.findAlarmsWithFeedbackYn(memberId);

        return results.stream()
                .map(result -> {
                    Alarms alarm = (Alarms) result[0];
                    Integer feedbackYn = (Integer) result[1];
                    AlarmDetailResponse response = AlarmDetailResponse.from(alarm);
                    response.setFeedbackYn(feedbackYn);
                    return response;
                })
                .collect(Collectors.toList());
    }


    // 알람을 확인했을때 seen=true로 업데이트
    public void updateSeenStatus(Long alarmId) {
        alarmRepository.updateSeenStatus(alarmId);
    }

    //알람 종류에 따른 알람 메시지 생성
    public void createAlarmMessage(Boards boards, MemberVo writers, WorkVo work, List<MemberVo> allMembers, String alarmKind, Long writerId, List<FeedbackStatuses> feedbackStatuses)
    {
         Integer studentNumber=writers.getStudentNumber();
         String userName=writers.getName();
         String workName=work.getWorkName();
         String title=boards.getTitle();
         String url = "/board/view/" + boards.getId();

        List<Alarms> alarmsList = allMembers.stream()
                .filter(memberResponse -> memberResponse != null && !memberResponse.getId().equals(writerId))
                .map(memberResponse -> {
                    Integer feedbackYn=-1;
                    if(feedbackStatuses!=null){
                        feedbackYn= feedbackStatuses.get(allMembers.indexOf(memberResponse)).getFeedbackYn();
                    }
                    String message = "";

                    if(alarmKind.equals("completeUpdate")&&feedbackYn!=-1) {//글 수정
                        if(feedbackYn == 2){
                            message = "'" + studentNumber + " " + userName + "'님께서 '[" + workName + "]" + title + "' 피드백을 반영하여 수정하였습니다.";
                        } else if(feedbackYn == 1 || feedbackYn == 0) {
                            message = "'" + studentNumber + " " + userName + "'님께서 '[" + workName + "]" + title + "' 수정하였습니다.";
                        }
                    }
                    else if(alarmKind.equals("newWrite")){//글 작성
                        message = "'" + studentNumber + " " + userName + "'님께서 '[" + workName + "]" + title + "'에 대해 새로운 글을 등록 하였습니다.";
                    }
                    else if(alarmKind.equals("requestFeedback")){ //피드백 수정 요청
                        message = "'" + studentNumber + " " + userName + "'님께서 '[" + workName + "]" + title + "'에 대해 수정 요청을 하였습니다.";
                    }
                    else if(alarmKind.equals("denyFeedback")){
                        message = "'" + studentNumber + " " + userName + "'님께서 '[" + workName + "]" + title + "'작성자님의 수정에 대해 거절을 하였습니다.";
                    }
                    return new Alarms(message, url, writers.getPictureUrl(), alarmKind, memberResponse.getId() , boards.getId(), writerId);
                })
                .collect(Collectors.toList());
        alarmRepository.saveAll(alarmsList);
    }


    //필터 처리 안하고 모든 팀원에게 알람 전송 시
    public void createAlarmMessageNofilter(Boards boards,  WorkVo work, List<MemberVo> allMembers, String alarmKind)
    {
        String workName=work.getWorkName();
        String title=boards.getTitle();
        String url = "/board/view/" + boards.getId();

        List<Alarms> alarmsList = allMembers.stream()
                .map(memberResponse -> {
                    String message = "";
                    if(alarmKind.equals("complFeedback")) {//피드백 완료
                        message = "'[" + workName + "]" + title + "'에 대한 모든 피드백이 완료되어 '완료' 상태가 되었습니다.";
                    }
                    return new Alarms(message, url, null, alarmKind, memberResponse.getId() , boards.getId(), null);
                })
                .collect(Collectors.toList());
        alarmRepository.saveAll(alarmsList);
    }





}




