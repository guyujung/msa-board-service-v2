package com.example.demo.src.file.client;


import com.example.demo.src.file.vo.MemberVo;
import com.example.demo.src.file.vo.TeamMemberVo;
import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name="dashboard-service" )
public interface TeamServiceClient {

    @GetMapping("/teammember/members/{teamId}")
    List<MemberVo> findTeamById(@PathVariable Long teamId);

    @GetMapping("/teammember/member/{teamId}/{memberId}")
    TeamMemberVo findByTeamsIdAndUsersId(@PathVariable Long teamId, @PathVariable Long memberId);

    @PostMapping("/teammember/add-contribution")
    void addContribution(@RequestBody TeamMemberVo teamMemberVo);
}
