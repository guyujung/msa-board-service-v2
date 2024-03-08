package com.example.demo.src.file.dto.request;

import com.example.demo.src.file.domain.Boards;
import com.example.demo.src.file.vo.MemberVo;
import com.example.demo.src.file.vo.WorkVo;
import lombok.Builder;
import lombok.Data;

@Data
public class AlarmMessageRequest {
    String userName;
    Integer studentNumber;
    String workName;
    String title;
  @Builder
  public AlarmMessageRequest(Boards boards, MemberVo writer, WorkVo work){

      this.userName= writer.getName();
      this.studentNumber = writer.getStudentNumber();
      this.workName=work.getWorkName();
      this.title=boards.getTitle();

  }
}
