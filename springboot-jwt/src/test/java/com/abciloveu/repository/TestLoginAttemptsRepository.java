package com.abciloveu.repository;

import com.abciloveu.entities.LoginAttempts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
public class TestLoginAttemptsRepository {

    @Autowired
    private LoginAttemptsRepository repository;

    @Test
    public void testSave() {
        LoginAttempts obj = new LoginAttempts();
//        obj.setId(Long.valueOf("20240308001"));
        obj.setUsername("taggdev");
        obj.setLastUpd(new Date());
        obj.setAttempts(1);
        repository.save(obj);
    }
}
