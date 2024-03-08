package com.example.demo.src.file.Service;


import com.example.demo.src.file.Repository.FileRepository;
import com.example.demo.src.file.domain.Boards;
import com.example.demo.src.file.domain.Files;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;


@AllArgsConstructor
@Service
@Slf4j
public class FileService {




    private FileRepository fileRepository;



    void fileupload(MultipartFile[] files , Boards boards) throws IOException {
        String projectPath = "/src/main/resources/static/files";
        // Save all uploaded files
        if (files != null) {
            for (MultipartFile file : files) {
                UUID uuid = UUID.randomUUID();
                String fileName = uuid + "_" + file.getOriginalFilename();
                File saveFile = new File(projectPath, fileName);
                file.transferTo(saveFile);


                // 빌더를 사용하여 파일 객체 생성
                Files file1 = Files.builder()
                        .filename(fileName)
                        .filepath("/files/" + fileName)
                        .build();
                file1.confirmBoard(boards);
                fileRepository.save(file1);
            }
        }
    }

    //특정 파일 객체로 삭제
    public void deletePhotoFromFileSystem(List<Files> files) {
        try {
            for (Files file : files) {
                String photoPath = file.getFilepath();
                String projectPath = System.getProperty("user.dir");
                File photoFile = new File(projectPath +"/src/main/resources/static/" + photoPath);



                // 파일이 존재하는지 확인하고 삭제
                if (photoFile.exists()) {
                    if (photoFile.delete()) {
                        log.info("사진 파일 삭제 성공: " + photoPath);
                    } else {
                        log.error("사진 파일 삭제 실패: " + photoPath);
                    }
                } else {
                    log.error("해당 경로에 사진 파일이 존재하지 않습니다: " + photoPath);
                }
            }

        } catch (Exception e) {
            log.error("사진 파일 삭제 중 오류 발생: ");
        }
    }


    //fileId로 파일 삭제
    public void deleteFileSystem(List<Long> fileIdList) {

        List<Files> files = fileRepository.findAllById(fileIdList);
        try {
            for (Files file : files) {
                String photoPath = file.getFilepath();
                String projectPath = System.getProperty("user.dir");
                File photoFile = new File(projectPath+"/src/main/resources/static/" + photoPath);



                // 파일이 존재하는지 확인하고 삭제
                if (photoFile.exists()) {
                    if (photoFile.delete()) {
                        log.info("사진 파일 삭제 성공: " + photoPath);
                    } else {
                        log.error("사진 파일 삭제 실패: " + photoPath);
                    }
                } else {
                    log.error("해당 경로에 사진 파일이 존재하지 않습니다: " + photoPath);
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            System.err.println("사진 파일 삭제 중 오류 발생: ");
        }
    }



}
