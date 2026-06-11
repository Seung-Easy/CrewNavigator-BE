package seungeasy.crewnavigator.domain.auth.dto.response;

public record UserInfoResponse(
        String userId,
        String name,
        String email,
        String phone,
        String userImage
) {}
