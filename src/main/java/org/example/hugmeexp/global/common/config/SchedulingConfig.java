package org.example.hugmeexp.global.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulingConfig {

    //@EnableScheduling이 ScheduledAnnotationBeanPostProcessor를 등록
    //이 프로세서가 @Scheduled 애노테이션이 붙은 메서드를 찾아 실행
}
