//코드 수정할거 request,response DTO로 변환


package com.example.demo.src.file.controller;
import com.example.demo.common.code.CommonCode;
import com.example.demo.common.response.Response;
import com.example.demo.src.file.Repository.FileRepository;
import com.example.demo.src.file.Service.FileService;
import com.example.demo.src.file.domain.Files;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController

@RequestMapping("/")
public class FileController {

    private final FileService fileService;
    private final FileRepository fileRepository;

public FileController(FileService fileService,FileRepository fileRepository){
    this.fileService=fileService;
    this.fileRepository=fileRepository;
}
    //파일 다운로드
    @GetMapping("/downloadFile/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("fileId") Long fileId, HttpServletRequest request) throws IOException {

        Files files = fileRepository.findById(fileId).get();

        Resource resource = new FileUrlResource("src/main/resources/static"+files.getFilepath());
        String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());

        if (contentType == null) { //null인 경우, 해당 파일의 MIME 타입이 정확하게 식별되지 않음
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + files.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/files/delete")
    public ResponseEntity<Response<String>> deleteFiles(@RequestBody List<Long> fileIdList) {
            fileService.deleteFileSystem(fileIdList);
            return ResponseEntity.ok(Response.of(CommonCode.GOOD_REQUEST, "파일 삭제 성공"));

    }


}


