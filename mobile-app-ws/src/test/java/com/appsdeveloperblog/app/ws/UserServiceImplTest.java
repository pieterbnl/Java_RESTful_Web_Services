package com.appsdeveloperblog.app.ws;

import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.io.repositories.UserRepository;
import com.appsdeveloperblog.app.ws.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    // UserServiceImpl is under test, so itself can't be mocked.
    // Because UserServiceImpl is using dependency injection, some objects needs to be mocked. For example UserRepository.
    // Therefore using @InjectMocks, to have the framework inject into the class, the mock object that it requires.
    @InjectMocks
    UserServiceImpl userService;

    // Creating a mock object which will be injected in UserServiceImpl
    @Mock
    UserRepository userRepository;


    @BeforeEach
    void setUp() throws Exception {
    // To allow Mockito to instantiate mock objects
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getUser() {
//        fail("Not yet implemented");

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFirstName("Myname");
        userEntity.setUserId("httpyu567e");
        userEntity.setEncryptedPassword("744jghjk787hd");

        when( userRepository.findByEmail( anyString() ) ).thenReturn(userEntity);

    }
}