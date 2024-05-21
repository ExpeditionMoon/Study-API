package moon.kakaoMapAPI.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import moon.kakaoMapAPI.entity.JoinMart;
import moon.kakaoMapAPI.entity.User;
import moon.kakaoMapAPI.exception.NoContentFoundException;
import moon.kakaoMapAPI.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import moon.kakaoMapAPI.dto.ApiResponse;
import moon.kakaoMapAPI.dto.CartProductDto;
import moon.kakaoMapAPI.dto.DiscountInfoDto;
import moon.kakaoMapAPI.dto.ProductAndDiscountDataDto;
import moon.kakaoMapAPI.entity.Mart;
import moon.kakaoMapAPI.entity.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MartProductService {

    private final MartProductRepository martProductRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final JoinMartRepository joinMartRepository;
    private final MartRepository martRepository;

    /**
     * 장바구니에 있는 모든 상품이 존재하는 마트 목록 + 마트별 합계 출력
     *
     * @return 마트별로 합계 금액을 martId와 함께 ApiResponse 형태로 반환
     * @throws EntityNotFoundException 사용자 엔티티를 찾을 수 없을 때 발생
     * @throws NoContentFoundException 장바구니에 데이터가 존재하지 않거나 비어있을 때 발생
     */
    @Transactional(readOnly = true)
    public ApiResponse<Map<Long, Long>> findMartsByProductIds() {
        ProductAndDiscountDataDto loadData = getProductAndDiscountData();

        return ApiResponse.success(calculateTotalFinalPriceByMart(
                        loadData.getDiscountInfoList(),
                        loadData.getCartProductList()),
                "마트별 총 합계를 불러왔습니다.");
    }

    /**
     * 마트별 상품 가격 세부사항 조회
     *
     * @return 선택된 마트의 상품의 세부 정보를 포함하는 객체를 ApiResponse 형태로 반환
     * @throws EntityNotFoundException 사용자 정보를 찾을 수 없을 때 발생
     * @throws NoContentFoundException 장바구니에 데이터가 존재하지 않거나 비어있을 때 발생
     */
    @Transactional(readOnly = true)
    public ApiResponse<List<DiscountInfoDto>> findMartInfoByMartId(Long martId) {
        ProductAndDiscountDataDto loadData = getProductAndDiscountData();

        Optional<Long> joinIdOptional = martRepository.findJoinIdByMartId(martId);
        if (joinIdOptional.isEmpty()) {
            return ApiResponse.fail("해당 마트에 대한 정보를 찾을 수 없습니다.");
        }
        Long joinId = joinIdOptional.get();

        List<DiscountInfoDto> filteredDiscountInfo = loadData.getDiscountInfoList().stream()
                .filter(dto -> dto.getJoinId().equals(joinId))
                .toList();

        return ApiResponse.success(filteredDiscountInfo, "마트 상품의 세부사항을 불러왔습니다.");
    }

    /**
     * 사용자를 확인하고, 해당 사용자의 상품 목록과 각 상품의 마트별 할인 정보 반환
     */
    private ProductAndDiscountDataDto getProductAndDiscountData() {
        Long userId = 1L;

        // 회원 정보 확인
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("사용자 정보를 찾을 수 없습니다.");
        }

        return loadProductsAndDiscounts(userId);
    }

    /**
     * memberId로 해당 회원의 장바구니에 있는 상품 목록과 각 상품의 마트별 할인 정보를 반환
     */
    private ProductAndDiscountDataDto loadProductsAndDiscounts(Long memberId) {
        /* 장바구니에 있는 상품 목록 가져오기 */
        List<CartProductDto> cartProductList = cartRepository.findCartsByMemberMemberId(memberId);
        // memberId에 해당하는 장바구니가 없을 경우
        if (cartProductList.isEmpty()) {
            throw new NoContentFoundException("장바구니가 존재하지 않습니다.");
        }

        List<Product> productList = cartProductList.stream()
                .map(dto -> productRepository.findById(dto.getProductId()))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
        // 상품 목록이 비어있을 경우
        if (productList.isEmpty()) {
            throw new NoContentFoundException("장바구니 목록이 비어있습니다.");
        }
        Long numberOfProducts = (long) productList.size();

        List<JoinMart> joinMartList =
                joinMartRepository.findJoinMartsSellingAllProducts(productList, numberOfProducts);
        /* 마트별로 할인 적용하여 상품 가격 출력 */
        List<DiscountInfoDto> discountInfoList =
                martProductRepository.findDiscountsByProductsAndJoinMarts(productList, joinMartList);

        return new ProductAndDiscountDataDto(productList, discountInfoList, cartProductList);
    }

    /**
     * 마트별로 상품의 최종 가격을 수량과 함께 계산하여, 각 마트의 총 합계를 반환
     */
    private Map<Long, Long> calculateTotalFinalPriceByMart(
            List<DiscountInfoDto> discountInfoDto,
            List<CartProductDto> cartProductList
    ) {
        Map<Long, Long> productIdToQuantity = cartProductList.stream()
                .collect(Collectors.toMap(
                        CartProductDto::getProductId,
                        CartProductDto::getQuantity,
                        Long::sum));

        Map<Long, Long> martIdToTotalPrice = new HashMap<>();

        discountInfoDto.stream()
                .filter(dto -> productIdToQuantity.containsKey(dto.getProductId()))
                .forEach(dto -> {
                    List<Mart> marts = martRepository.findByJoinMartJoinId(dto.getJoinId());
                    marts.forEach(mart -> {
                        Long totalPrice = dto.getFinalPrice() * productIdToQuantity.get(dto.getProductId());
                        martIdToTotalPrice.merge(mart.getMartId(), totalPrice, Long::sum);
                    });
                });
        return martIdToTotalPrice;
    }
}
