package org.dragonli.service.modules.userservice;

import com.alibaba.dubbo.config.annotation.Reference;
import org.dragonli.service.modules.user.interfaces.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = UserServiceApplication.class)
public class UserManagerTest2 extends AbstractTransactionalJUnit4SpringContextTests {
    @Reference
    UserService userService;

    @Test
    public void getListTest() {
        System.out.println(userService == null);
    }

//    public  static void main(String[] args){
//        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("bootstrap.yml");
//
//        context.start();
//
//        UserService userService = (UserService) context.getBean(UserService.class);
//        System.out.println(userService==null);
//    }

}