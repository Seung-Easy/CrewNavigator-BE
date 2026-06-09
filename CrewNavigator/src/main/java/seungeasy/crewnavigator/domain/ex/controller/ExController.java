package seungeasy.crewnavigator.domain.ex.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import seungeasy.crewnavigator.domain.ex.service.ExCommandService;
import seungeasy.crewnavigator.domain.ex.service.ExQueryService;

@RestController
@RequestMapping("/Ex")
@RequiredArgsConstructor
@Tag(name = "예시 API", description = "예시 API")
public class ExController {
    private final ExCommandService exCommandService;
    private final ExQueryService exQueryService;


}
