package me.jongwoo.demospringbatch.domain;

import lombok.*;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Person {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private String studentId;
    private int korean;
    private int english;
    private int math;
    private double avg;

}
