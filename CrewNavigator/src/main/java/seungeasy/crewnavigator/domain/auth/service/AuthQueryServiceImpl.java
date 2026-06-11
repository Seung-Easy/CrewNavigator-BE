package seungeasy.crewnavigator.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seungeasy.crewnavigator.common.exception.BusinessException;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.auth.dto.request.FindIdRequest;
import seungeasy.crewnavigator.domain.auth.dto.response.UserInfoResponse;
import seungeasy.crewnavigator.domain.auth.entity.User;
import seungeasy.crewnavigator.domain.auth.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthQueryServiceImpl implements AuthQueryService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<String> findUserId(FindIdRequest request) {
        Optional<User> user = userRepository.findByNameAndEmail(request.name(), request.email());

        if (user.isEmpty()) {
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        }

        return List.of(user.get().getUserId());
    }

    @Override
    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        return new UserInfoResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getUserImage()
        );
    }
}
