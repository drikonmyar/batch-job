package com.schedule.config;

import com.schedule.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerConfig {

    @Autowired
    private ProcessService processService;

    @Scheduled(cron = "0/10 * * * * *")
    public void run(){
        try{
            processService.sayHello();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
