package seungeasy.crewnavigator.domain.auth.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import seungeasy.crewnavigator.domain.auth.service.AuthCommandService;
import seungeasy.crewnavigator.domain.auth.service.AuthQueryService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "인증/인가 API", description = "인증/인가와 관련된 API")
public class AuthController {
    private final AuthCommandService authCommandService;
    private final AuthQueryService authQueryService;


}
