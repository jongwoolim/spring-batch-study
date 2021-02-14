package me.jongwoo.demospringbatch.job;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.jongwoo.demospringbatch.domain.PersonRepository;
import net.bytebuddy.asm.Advice;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    @Autowired
    private PersonRepository personRepository;

    @Override
    public void afterJob(JobExecution jobExecution) {
        super.afterJob(jobExecution);

        if(jobExecution.getStatus() == BatchStatus.COMPLETED){
            log.info("Job Finished....!!");
            personRepository.findAll().forEach(System.out::println);
        }
    }
}
