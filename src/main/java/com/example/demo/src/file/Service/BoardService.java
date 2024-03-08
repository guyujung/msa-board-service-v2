package com.example.demo.src.file.Service;

import com.example.demo.common.exception.BoardAlreadyWriteException;
import com.example.demo.src.file.Repository.*;
import com.example.demo.src.file.client.*;
import com.example.demo.src.file.domain.Boards;
import com.example.demo.src.file.domain.FeedbackStatuses;
import com.example.demo.src.file.dto.request.BoardWriteRequest;
import com.example.demo.src.file.dto.response.*;
import com.example.demo.src.file.vo.*;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static com.example.demo.src.file.dto.request.BoardWriteRequest.toEntity;
import static java.util.stream.Collectors.toList;

@AllArgsConstructor
@Service
public class BoardService {



    private BoardRepository boardRepository;
    private FileService fileService;
    private AlarmService alarmService;
    private FeedbackStatusService feedbackStatusService;

    @Autowired
    WorkerServiceClient workerServiceClient;
    @Autowired
    TeamServiceClient teamServiceClient;
    @Autowired
    MemberServiceClient memberServiceClient;

    //조회수 증가
    public void increaseCount(Long boardId) {
        boardRepository.increaseViewCount(boardId);
    }


    //글 작성
    @Transactional
    public multiWriteResponse multiWrite(BoardWriteRequest request, Long memberId, Long teamId, Long workId, MultipartFile[] files) throws Exception {
        WorkerVo workerVo =workerServiceClient.getWriteStatus(memberId,workId);//memberId와 workId로 worker조회하기. 글을 쓰는 사람이 work를 담당한 worker인지 확인하기 위함(feign client 사용)
        if (workerVo.isWriteYn()) { //work의 담당자만 게시판을 작성할 수 있고, 각각의 담당자마다 게시판을 한번만 작성할 수 있음
            throw new BoardAlreadyWriteException("Worker has already written a board.");
        }
        workerServiceClient.setWriteStatusTrue(memberId,workId); //woker가 게시판을 작성했음을 등록(feign client 사용)
        workerServiceClient.setWorkStatus(workId, 3);//게시판 등록, 피드백을 등록 하기만 하면 work 상태를 피드백진행중=3으로 바꿈, 한번 피드백을 했으면 다시 못바꿈)
        Boards boards = toEntity(request,memberId,teamId,workId);
        boardRepository.save(boards);
        fileService.fileupload( files ,boards);//파일 업로드
        FeedbackStatusAndAlarm(boards,memberId,workId,teamId); // 멤버수에 맞는 feedbackstatus 테이블 등록 및 글 생성 알람 메시지 저장

        return multiWriteResponse.from(boards);
    }
    //글 수정
    public multiWriteResponse multiReWrite(Long boardId,Long workId,BoardWriteRequest request, MultipartFile[] files) throws IOException {
        Boards boards = boardRepository.findBoardById(boardId);
        boards.setTitle(request.getTitle());
        boards.setContent(request.getContent());
        fileService.fileupload( files ,boards);//파일 업로드
        boardRepository.save(boards);
        reWrtieCompletionAlarm(boards); //글작성자 제외 팀원 모두에게 알람이 가도록

        return multiWriteResponse.from(boards);
    }

    //학생 게시판 리스트
    public CombinedListResponse boardList(Long memberId, Long teamId) {
        List<Boards> boards= boardRepository.findBoardsByTeamIdWithFeedbackStatus(memberId,teamId); //게시글과 게시글에 대한 멤버들의 피드백 상태
        List<BoardResponse> result = boards.stream()
                .map(o -> new BoardResponse(o,memberId))
                .collect(toList());

        List<BoardWorkVo> works= workerServiceClient.findWorksByTeamId(teamId); //team에 해당하는 work 리스트 조회
        List<MemberVo> teammembers=teamServiceClient.findTeamById(teamId); //team에 해당하는 member 리스트 조회
        return new CombinedListResponse(result,works,teammembers);
    }

    //교수님 게시판 리스트
    public CombinedListResponse professorBoardList(Long teamId) {

        List<Boards> boards = boardRepository.findBoardsByTeamId(teamId);
        List<BoardResponse> result = boards.stream()
                .map(o -> new BoardResponse(o))
                .collect(toList());

        List<BoardWorkVo> works= workerServiceClient.findWorksByTeamId(teamId); //team에 해당하는 work 리스트 조회
        List<MemberVo> teammembers=teamServiceClient.findTeamById(teamId); //team에 해당하는 member 리스트 조회
        return new CombinedListResponse(result,works,teammembers);
    }



    //특정 게시글 불러오기
    public CombinedResponse boardView(Long id){
        Boards boards = boardRepository.findBoardById(id);
        WorkVo workResponse=workerServiceClient.findWorkById(boards.getWorkId());
        MemberVo memberDto=memberServiceClient.findByUserId(boards.getUserId());
        return new CombinedResponse(boards,workResponse,memberDto);
    }


    //재수정 완료 알람
    public void reWrtieCompletionAlarm(Boards boards){
        List<FeedbackStatuses> feedbackStatusesList=feedbackStatusService.findFeedbackStatusByBoardId(boards.getId());// 해당 팀에 속한 모든 멤버 가져와서 FeedbackStatuses에 추가
        List<MemberVo> allMembers = teamServiceClient.findTeamById(boards.getTeamId());
        MemberVo writers=memberServiceClient.findByUserId(boards.getUserId());
        WorkVo work=workerServiceClient.findWorkById(boards.getWorkId());
        alarmService.createAlarmMessage(boards, writers, work, allMembers, "completeUpdate",boards.getUserId(),feedbackStatusesList);
    }

    //wiriter은 게시판 작성자
    @Transactional
    public void FeedbackStatusAndAlarm(Boards boards, Long writerId, Long workId, Long teamId) {
        List<MemberVo> allMembers = teamServiceClient.findTeamById(boards.getTeamId());
        MemberVo writers=memberServiceClient.findByUserId(writerId);
        WorkVo works=workerServiceClient.findWorkById(workId);
        feedbackStatusService.createFeedbackStatus(allMembers,boards,teamId,writerId);  // FeedbackStatuses 생성
        alarmService.createAlarmMessage(boards,writers, works,  allMembers, "newWrite",writerId,null);   // Alarms 생성

    }

    //특정 게시글 삭제
    public void boardDelete(Long id){
        Boards boards = boardRepository.findBoardById(id);
        fileService.deletePhotoFromFileSystem(boards.getFileList());
        boardRepository.deleteById(id);
    }

    //workId에 해당하는 board 리스트 조회
    public List<PostsResponse> getPosts(Long workId){
        List<Boards> boards = boardRepository.findByWorksId(workId);
        List<PostsResponse> posts = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        boards.forEach(v -> {
            posts.add(modelMapper.map(v, PostsResponse.class));
        });
        return posts;
    }
    //boardId에 해당하는 board조회
    Boards findBoardById(Long boardId) {
       return  boardRepository.findBoardById(boardId);
    }

    //모든 팀원 피드백이 완료되었을 경우
    void finishFeedback(Boards boards){
        boards.setFeedbackYn(true);
        boardRepository.save(boards);
    }

    List<Boards> findBoardListByUserIdListAndWorkId(List<Long> userIdList,Long workId){
        return boardRepository.findByUserIdInAndWorkId(userIdList,workId);
    }



}
