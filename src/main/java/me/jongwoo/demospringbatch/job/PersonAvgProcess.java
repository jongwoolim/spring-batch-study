package me.jongwoo.demospringbatch.job;

import me.jongwoo.demospringbatch.domain.Person;
import org.springframework.batch.item.ItemProcessor;

public class PersonAvgProcess implements ItemProcessor<Person, Person> {

    @Override
    public Person process(Person person) throws Exception {
        
        final int korean = person.getKorean();
        final int english = person.getKorean();
        final int math = person.getMath();

        final double avg = (korean + english + math) / 3.0;
        person.setAvg(avg);
        return person;
    }

}
