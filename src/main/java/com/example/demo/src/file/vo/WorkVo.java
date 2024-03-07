package com.example.demo.src.file.vo;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorkVo {

    private Long id;
    private Long teamId;
    private String workName;
    private Integer importance;
    private Integer status;
    private Integer workerNumber;
    private LocalDateTime endDate;

    public WorkVo() {
        // 기본 생성자 내용 (선택적)
    }

    @Builder
    public WorkVo(Long workId, String workName){
        this.id=workId;
        this.workName=workName;
    }


}
