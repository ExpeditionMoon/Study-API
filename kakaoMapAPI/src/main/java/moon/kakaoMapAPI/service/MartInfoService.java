package moon.kakaoMapAPI.service;

import lombok.RequiredArgsConstructor;
import moon.kakaoMapAPI.repository.MartInfoRepository;
import moon.kakaoMapAPI.repository.MartProductRepository;
import moon.kakaoMapAPI.repository.MartRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MartInfoService {

    private final MartRepository martRepository;
    private final MartInfoRepository martInfoRepository;
    private final MartProductRepository martProductRepository;

}
