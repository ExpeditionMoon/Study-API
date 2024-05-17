package moon.kakaoMapAPI.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import moon.kakaoMapAPI.entity.User;
import moon.kakaoMapAPI.exception.NoContentFoundException;
import moon.kakaoMapAPI.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import moon.kakaoMapAPI.dto.ApiResponse;
import moon.kakaoMapAPI.dto.CartProductDto;
import moon.kakaoMapAPI.dto.DiscountInfoDto;
import moon.kakaoMapAPI.dto.ProductAndDiscountDataDto;
import moon.kakaoMapAPI.entity.Mart;
import moon.kakaoMapAPI.entity.Product;
import moon.kakaoMapAPI.repository.CartRepository;
import moon.kakaoMapAPI.repository.MartProductRepository;
import moon.kakaoMapAPI.repository.ProductRepository;

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

    /**
     * 장바구니에 있는 모든 상품이 존재하는 마트 목록 + 마트별 합계 출력
     *
     * @return 마트별로 합계 금액을 martId와 함께 ApiResponse 형태로 반환
     * @throws EntityNotFoundException 사용자 엔티티를 찾을 수 없을 때 발생
     * @throws NoContentFoundException 장바구니에 데이터가 존재하지 않거나 비어있을 때 발생
     */
    @Transactional(readOnly = true)
    public ApiResponse<Map<Long, Long>> findMartsByProductIds(HttpServletRequest request) {
        ProductAndDiscountDataDto loadData = getProductAndDiscountData(request);

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
    public ApiResponse<List<DiscountInfoDto>> findMartInfoByMartId(Long martId, HttpServletRequest request) {
        ProductAndDiscountDataDto loadData = getProductAndDiscountData(request);
        List<DiscountInfoDto> filteredDiscountInfo = loadData.getDiscountInfoList().stream()
                .filter(dto -> dto.getMartId().equals(martId))
                .toList();

        return ApiResponse.success(filteredDiscountInfo, "마트 상품의 세부사항을 불러왔습니다.");
    }

    /** 사용자를 확인하고, 해당 사용자의 상품 목록과 각 상품의 마트별 할인 정보 반환 */
    private ProductAndDiscountDataDto getProductAndDiscountData(HttpServletRequest request) {
        Long memberId = 1L;

        // 회원 정보 확인
        Optional<User> member = userRepository.findById(memberId);
        if (member.isEmpty()) {
            throw new EntityNotFoundException("사용자 정보를 찾을 수 없습니다.");
        }

        return loadProductsAndDiscounts(memberId);
    }

    /** memberId로 해당 회원의 장바구니에 있는 상품 목록과 각 상품의 마트별 할인 정보를 반환 */
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

        List<Mart> martList =
                martProductRepository.findMartsSellingAllProducts(productList, numberOfProducts);

        /* 마트별로 할인 적용하여 상품 가격 출력 */
        List<DiscountInfoDto> discountInfoList =
                martProductRepository.findDiscountsByProductsAndMarts(productList, martList);

        return new ProductAndDiscountDataDto(productList, discountInfoList, cartProductList);
    }

    /** 마트별로 상품의 최종 가격을 수량과 함께 계산하여, 각 마트의 총 합계를 반환 */
    private Map<Long, Long> calculateTotalFinalPriceByMart(
            List<DiscountInfoDto> discountInfoDto,
            List<CartProductDto> cartProductList
    ) {
        Map<Long, Long> productIdToQuantity = cartProductList.stream()
                .collect(Collectors.toMap(
                        CartProductDto::getProductId,
                        CartProductDto::getQuantity,
                        (a, b) -> b));

        return discountInfoDto.stream()
                .filter(dto -> productIdToQuantity.containsKey(dto.getProductId()))
                .collect(Collectors.groupingBy(
                        DiscountInfoDto::getMartId,
                        Collectors.summingLong(
                                dto -> dto.getFinalPrice() * productIdToQuantity.get(dto.getProductId()))
                ));
    }
}
