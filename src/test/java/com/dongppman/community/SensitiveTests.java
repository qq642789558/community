package com.dongppman.community;

import com.dongppman.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SensitiveTests {
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Test
    public void testSensitiveFilter(){
        String text="牛啊,妈,的,吸毒,吸土烟";
        text= sensitiveFilter.filter(text);
        System.out.println(text);
    }
}
