package me.jongwoo.demospringbatch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.jongwoo.demospringbatch.domain.Person;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.BindException;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Log4j2
@RequiredArgsConstructor
@Configuration
public class SimpleJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;


    /*하나의 job은 여러개의 stepd을 가질 수 있다
    Step은 ItemReader, ItemProcessor, ItemWriter로 만들어진다
    Step은 차례대로 ItemReader, ItemProcessor, ItemWriter로 처리
    Job,Step이 실행됐는지 또는 중복 실행 됐는지는 스프링 배치에서 생성한 JobRepository를 통해 확인할 수 있다.*/


    @Bean
    public Job job(JobCompletionNotificationListener listener) throws Exception {
        return jobBuilderFactory.get("myJob")
                .incrementer(new RunIdIncrementer()) // 동일 파라미터에서 다시 실행하고 싶을때 사용
                .listener(listener)
                .flow(step(jpaWriter()))
                .end()
                .build();
    }

    @Bean
    public Step step(JpaItemWriter<Person> jpaWriter) throws Exception {
        return stepBuilderFactory.get("step")
                .<Person, Person> chunk(10)
                .reader(reader())
                .processor(new PersonAvgProcess())
                .writer(jpaWriter)
                .build();
    }

    @Bean
    public FlatFileItemReader<Person> reader(){

        return new FlatFileItemReaderBuilder<Person>()
                .name("PersonReader")
                .resource(new ClassPathResource("sample.txt"))
                .delimited()
                .delimiter(",")
                .names("name","studentId","korean","english","math")
                .fieldSetMapper(new FieldSetMapper<Person>() {

                    @Override
                    public Person mapFieldSet(FieldSet fieldSet) throws BindException {
                        String name = fieldSet.readString(0);
                        String studentId = fieldSet.readString(1);
                        int korean = fieldSet.readInt(2);
                        int english = fieldSet.readInt(3);
                        int math = fieldSet.readInt(4);

                        return Person.builder()
                                .name(name)
                                .studentId(studentId)
                                .korean(korean)
                                .english(english)
                                .math(math).build();
                    }
                }).build();


    }

    @Bean
    public JpaItemWriter<Person> jpaWriter(){

        JpaItemWriter<Person> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }

    //jpa를 사용하지 않는 경우 jdbc의 배치 기능 이용 jdbcBatchItemWriter
//    @Bean
//    public JdbcBatchItemWriter<Person> writer(DataSource dataSource){
//        return new JdbcBatchItemWriterBuilder<Person>()
//                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
//                .sql("INSERT INTO PERSON (name, student_Id, korean, english, math, avg) VALUES(:name, :studentId, :korean, :english, :math, :avg)")
//                .dataSource(dataSource).build();
//    }

    @Bean
    public Job simpleJob(){
        return jobBuilderFactory.get("simpleJob")
                .start(simpleStep1())
                .build();
    }

    @Bean
    public Step simpleStep1(){
        return stepBuilderFactory.get("simpleStep1")
                .tasklet((stepContribution, chunkContext) -> {
                    log.info(">>>>>>> this is Step1 ");
                    return RepeatStatus.FINISHED;
                }).build();


    }

}
