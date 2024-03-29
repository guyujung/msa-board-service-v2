package com.example.demo.src.file.domain;

import com.example.demo.src.file.FeedbackTimeEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Table(name = "Alarms")
@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Alarms extends FeedbackTimeEntity {

    public Alarms(Long alarmId, String content, String redirectUrl,String pictureUrl, String alarmKind, Long boardId,Long writerId, Long userId) {
        this.alarmId=alarmId;
        this.content=content;
        this.redirectUrl=redirectUrl;
        this.writerPictureUrl=pictureUrl;
        this.alarmKind=alarmKind;
        this.boardId=boardId;
        this.writerId=writerId;
        this.userId=userId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Long alarmId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "board_id")
    private Long boardId;

    @Column(name = "seen", nullable = false)
    private boolean seen;

    @Column(name = "content", nullable = false, length = 255)
    private String content;

    @Column(name = "redirect_url", length = 100)
    private String redirectUrl;

    @Column(name = "writer_picture_url")
    private String writerPictureUrl;

    @Column(name = "alarm_kind")
    private String alarmKind;

    @Column(name = "writer_id")
    private Long writerId;



    @Builder
    public Alarms( String content, String url, String writerPictureUrl, String alarmKind,Long userId, Long boardId, Long writerId ){
        this.content = content;
        this.redirectUrl=url;
        this.writerPictureUrl=writerPictureUrl;
        this.alarmKind=alarmKind;
        this.userId=userId;
        this.boardId=boardId;
        this.writerId=writerId;
    }


    public Alarms() {

    }
}