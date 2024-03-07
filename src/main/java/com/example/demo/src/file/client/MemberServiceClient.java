package com.example.demo.src.file.client;



import com.example.demo.src.file.vo.MemberVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name="user-service" )
public interface MemberServiceClient {

    @GetMapping("/user/{userId}")
    MemberVo findByUserId(@PathVariable Long userId);

}
