package com.example.cctv;

import com.example.cctv.data.CameraInfo;
import com.example.cctv.poll.CamService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@Slf4j
@SpringBootApplication
@AllArgsConstructor
public class CctvApplication {

    public static void main(String[] args) {
        SpringApplication.run(CctvApplication.class, args);
    }


    @Bean
    public CommandLineRunner myCommandLineRunner(CamService camService) {
        return (args) -> {
            camService.setCamList();
            List<CameraInfo> cams = camService.getCamList();
            log.info("response = {}", cams);
        };
    }


}
