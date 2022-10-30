package com.example.hellotalk.service;

import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.model.user.User;
import com.example.hellotalk.repository.UserRepository;
import com.example.hellotalk.service.impl.user.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void testGetActor() {

        UUID userId = UUID.fromString("1bfff94a-b70e-4b39-bd2a-be1c0f898589");

        UserEntity userEntity = UserEntity.builder().id(userId).name("anyName").selfIntroduction("anySelfIntroduction").build();
        when(userRepository.findById(any())).thenReturn(Optional.of(userEntity));

        User user = userService.getUser(UUID.randomUUID());

        assertAll(
                () -> assertEquals(userId, user.getId()),
                () -> assertEquals("anyName", user.getName()),
                () -> assertEquals("anySelfIntroduction", user.getSelfIntroduction())
        );
    }

}
