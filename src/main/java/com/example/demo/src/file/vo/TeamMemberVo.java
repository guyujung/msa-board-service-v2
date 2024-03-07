package com.example.demo.src.file.vo;


import lombok.Data;

@Data
public class TeamMemberVo {

    private Long id;

    private Long teamId;

    private Long userId;

    private float contribution;

    TeamMemberVo(){

    }
}
