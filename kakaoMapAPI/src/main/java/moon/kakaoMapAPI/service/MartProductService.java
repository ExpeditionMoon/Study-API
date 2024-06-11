package moon.kakaoMapAPI.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import moon.kakaoMapAPI.dto.ApiResponse;
import moon.kakaoMapAPI.dto.CartProductDto;
import moon.kakaoMapAPI.dto.DiscountInfoDto;
import moon.kakaoMapAPI.dto.ProductAndDiscountDataDto;
import moon.kakaoMapAPI.entity.JoinMart;
import moon.kakaoMapAPI.entity.Mart;
import moon.kakaoMapAPI.entity.Product;
import moon.kakaoMapAPI.entity.User;
import moon.kakaoMapAPI.repository.*;
import moon.kakaoMapAPI.util.enums.MartAndProductMessage;
import moon.kakaoMapAPI.util.exception.NoContentFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final UserRepository memberRepository;
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
                MartAndProductMessage.LOADED_TOTAL_SUM.getMessage());
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

        Long joinId = martRepository.findJoinIdByMartId(martId)
                .orElseThrow(() -> new EntityNotFoundException(MartAndProductMessage.NOT_FOUND_MART_DETAILS.getMessage()));

        List<DiscountInfoDto> filteredDiscountInfo = loadData.getDiscountInfoList().stream()
                .filter(dto -> dto.getJoinId().equals(joinId))
                .toList();

        return ApiResponse.success(filteredDiscountInfo, MartAndProductMessage.LOADED_MART_DETAILS.getMessage());
    }

    /**
     * 사용자를 확인
     */
    private ProductAndDiscountDataDto getProductAndDiscountData() {
        Long memberId = 1L;

        // 회원 정보 확인
        User user = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(MartAndProductMessage.NOT_FOUND_USER.getMessage()));


        return loadProductsAndDiscounts(user);
    }

    /**
     * 회원의 장바구니에 있는 상품 목록과 각 상품의 마트별 할인 정보를 반환
     */
    private ProductAndDiscountDataDto loadProductsAndDiscounts(User user) {
        /* 장바구니에 있는 상품 목록 가져오기 */
        List<CartProductDto> cartProductList = cartRepository.findCartsByUser(user);
        if (cartProductList.isEmpty()) {
            throw new NoContentFoundException(MartAndProductMessage.EMPTY_CART.getMessage());
        }

        List<Product> productList = cartProductList.stream()
                .map(dto -> productRepository.findById(dto.getProductId()))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
        if (productList.isEmpty()) {
            throw new NoContentFoundException(MartAndProductMessage.EMPTY_CART_PRODUCTS.getMessage());
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
