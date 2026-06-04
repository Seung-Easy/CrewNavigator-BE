package seungeasy.crewnavigator.domain.auth.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("authQueryService")
public class AuthQueryServiceImpl implements AuthQueryService {
}
