package com.example.hellotalk.service.impl.user;

import com.example.hellotalk.entity.user.FollowingRequestEntity;
import com.example.hellotalk.entity.user.UserEntity;
import com.example.hellotalk.exception.FollowerNotFoundException;
import com.example.hellotalk.exception.UserNotFoundException;
import com.example.hellotalk.model.user.HobbyAndInterest;
import com.example.hellotalk.model.user.Hometown;
import com.example.hellotalk.model.user.User;
import com.example.hellotalk.repository.UserRepository;
import com.example.hellotalk.service.user.UserService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.hellotalk.entity.user.UserEntity.buildUserEntityFromModel;
import static com.example.hellotalk.exception.AppExceptionHandler.USER_NOT_FOUND_EXCEPTION;

@Service
public class UserServiceImpl implements UserService {

    final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUser(UUID userId) {

        Optional<UserEntity> userEntityOptional = userRepository.findById(userId);

        if (userEntityOptional.isPresent()) {

            UserEntity userEntity = userEntityOptional.get();

            Set<HobbyAndInterest> hobbyAndInterestEntities =
                    userEntity.getHobbyAndInterestEntities().stream()
                            .map(h -> HobbyAndInterest.builder()
                                    .id(h.getId())
                                    .title(h.getTitle()).build())
                            .collect(Collectors.toSet());

            return User.builder()
                    .id(userEntity.getId())
                    .name(userEntity.getName())
                    .dob(userEntity.getDob())
                    .status(userEntity.getStatus())
                    .gender(userEntity.getGender())
                    .subscriptionType(userEntity.getSubscriptionType())
                    .creationDate(userEntity.getCreationDate())
                    .handle(userEntity.getHandle())
                    .nativeLanguage(userEntity.getNativeLanguage())
                    .targetLanguage(userEntity.getTargetLanguage())
                    .selfIntroduction(userEntity.getSelfIntroduction())
                    .occupation(userEntity.getOccupation())
                    .placesToVisit(userEntity.getPlacesToVisit())
                    .hometown(Hometown.builder()
                            .city(userEntity.getHometownEntity().getCity())
                            .country(userEntity.getHometownEntity().getCountry())
                            .build())
                    .hobbyAndInterests(hobbyAndInterestEntities)
                    .build();
        } else {
            throw new UserNotFoundException(USER_NOT_FOUND_EXCEPTION);
        }

    }

    @Override public User createUser(User user) {

        UserEntity userEntity = buildUserEntityFromModel(user);
        userEntity = userRepository.save(userEntity);
        user.setId(userEntity.getId());
        return user;
    }

    @Override public User updateUser(UUID userId, User user) {

        if (userRepository.findById(userId).isPresent()) {
            UserEntity userEntity = UserEntity.buildUserEntityFromModel(user);
            userEntity.setId(userId);

            userEntity = userRepository.save(userEntity);
            return User.buildUserFromEntity(userEntity);
        } else {
            throw new UserNotFoundException(USER_NOT_FOUND_EXCEPTION);
        }
    }

    @Override public void deleteUser(UUID userId) {

        Optional<UserEntity> optionalUserEntity = userRepository.findById(userId);

        if (optionalUserEntity.isPresent()) {
            userRepository.deleteById(userId);
        } else {
            throw new UserNotFoundException(USER_NOT_FOUND_EXCEPTION);
        }
    }

    @Override public void followUser(UUID fromId, UUID toId) throws FollowerNotFoundException {

        Optional<UserEntity> userEntityOptionalFrom = userRepository.findById(fromId);
        Optional<UserEntity> userEntityOptionalTo = userRepository.findById(toId);

        if (userEntityOptionalFrom.isEmpty() || userEntityOptionalTo.isEmpty()) {
            throw new UserNotFoundException(USER_NOT_FOUND_EXCEPTION);
        }

        UserEntity userEntityTo = userEntityOptionalTo.get();
        UserEntity userEntityFrom = userEntityOptionalFrom.get();
        userEntityTo = userRepository.save(setFollower(userEntityTo, userEntityFrom));

        if (userEntityTo.getId()
                == null) { // Not sure how to assert the follower was saved properly, so if there's a problem with the id for the original user it means the whole object has been compromised
            throw new FollowerNotFoundException("Error saving follower");
        }
    }

    private UserEntity setFollower(UserEntity userEntityTo, UserEntity userEntityFrom) {
        Set<FollowingRequestEntity> followingRequestEntities = new HashSet<>();
        FollowingRequestEntity followingRequestEntity = FollowingRequestEntity.builder().userSenderEntity(userEntityFrom).userReceiverEntity(userEntityTo).build();
        followingRequestEntities.add(followingRequestEntity);

        userEntityTo.setFollowedByEntity(followingRequestEntities);

        return userEntityTo;
    }
}
