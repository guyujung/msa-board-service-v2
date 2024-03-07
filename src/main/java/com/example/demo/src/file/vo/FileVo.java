package com.example.demo.src.file.vo;

import lombok.Builder;
import lombok.Data;

@Data
public class FileVo {

        private Long id;
        private String filename;
        private String filepath;
        private Long boardId;
        @Builder
        FileVo(Long id, String filename, String filepath, Long boardId){
                this.id=id;
                this.filename=filename;
                this.filepath=filepath;
                this.boardId=boardId;
        }
}
