package moon.kakaoMapAPI.service;

import jakarta.persistence.EntityNotFoundException;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MartProductServiceTest {
    private final Long memberId = 1L;
    private final Long martAId = 1L;
    private final Long martBId = 2L;
    private final Long joinAId = 1L;
    private final Long joinBId = 3L;
    private final Long productAId = 1L;
    private final Long productBId = 2L;
    @Mock
    private MartProductRepository martProductRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private JoinMartRepository joinMartRepository;
    @Mock
    private MartRepository martRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private MartProductService martProductService;
    private User user;
    private ProductAndDiscountDataDto mockloadData;
    private Map<Long, Long> expectedTotalFinalPrice;

    @BeforeEach
    void 초기_설정() {
        user = Mockito.mock(User.class);
        when(user.getUserId()).thenReturn(memberId);

        JoinMart joinA = JoinMart.builder().joinId(joinAId).build();
        JoinMart joinB = JoinMart.builder().joinId(joinBId).build();
        Mart martA = Mart.builder().martId(martAId).joinMart(joinA).build();
        Mart martB = Mart.builder().martId(martBId).joinMart(joinB).build();
        Product product1 = Product.builder().productName("자유시간").build();
        Product product2 = Product.builder().productName("아이스티").build();
        DiscountInfoDto dto1 = new DiscountInfoDto(productAId, "자유시간", joinAId, null, 1000L, BigDecimal.valueOf(10.0));
        DiscountInfoDto dto2 = new DiscountInfoDto(productAId, "자유시간", joinBId, null, 900L, null);
        DiscountInfoDto dto3 = new DiscountInfoDto(productBId, "아이스티", joinAId, null, 2000L, null);
        DiscountInfoDto dto4 = new DiscountInfoDto(productBId, "아이스티", joinBId, null, 2500L, BigDecimal.valueOf(15.0));

        List<CartProductDto> cartProductList = List.of(
                new CartProductDto(3L, user.getUserId(), productAId),
                new CartProductDto(2L, user.getUserId(), productBId));
        List<Product> productList = List.of(product1, product2);
        Long numberOfProducts = (long) productList.size();

        lenient().when(joinMartRepository.findJoinMartsSellingAllProducts(productList, numberOfProducts))
                .thenReturn(List.of(joinA, joinB));
        lenient().when(martProductRepository.findDiscountsByProductsAndJoinMarts(productList, List.of(joinA, joinB)))
                .thenReturn(List.of(dto1, dto2, dto3, dto4));

        mockloadData = new ProductAndDiscountDataDto(productList, List.of(dto1, dto2, dto3, dto4), cartProductList);
        expectedTotalFinalPrice = Map.of(
                martA.getMartId(), 6700L,
                martB.getMartId(), 6950L
        );
    }

    @Test
    void 마트별_합계금액_출력테스트() {
        when(userRepository.findById(memberId)).thenReturn(Optional.of(user));
        when(cartRepository.findCartsByUser(user)).thenReturn(mockloadData.getCartProductList());
        when(productRepository.findById(productAId)).thenReturn(Optional.of(mockloadData.getProductList().get(0)));
        when(productRepository.findById(productBId)).thenReturn(Optional.of(mockloadData.getProductList().get(1)));
        when(martRepository.findByJoinMartJoinId(joinAId)).thenReturn(List.of(Mart.builder().martId(martAId).build()));
        when(martRepository.findByJoinMartJoinId(joinBId)).thenReturn(List.of(Mart.builder().martId(martBId).build()));

        ApiResponse<Map<Long, Long>> response = martProductService.findMartsByProductIds();

        assertThat(response.getData()).isEqualTo(expectedTotalFinalPrice);
        assertThat(response.getMessage()).isEqualTo(MartAndProductMessage.LOADED_TOTAL_SUM.getMessage());
    }

    @Test
    void 마트별_세부사항_출력테스트() {
        when(userRepository.findById(memberId)).thenReturn(Optional.of(user));
        when(cartRepository.findCartsByUser(user)).thenReturn(mockloadData.getCartProductList());
        when(productRepository.findById(productAId)).thenReturn(Optional.of(mockloadData.getProductList().get(0)));
        when(productRepository.findById(productBId)).thenReturn(Optional.of(mockloadData.getProductList().get(1)));
        when(martRepository.findJoinIdByMartId(martAId)).thenReturn(Optional.of(joinAId));

        ApiResponse<List<DiscountInfoDto>> response = martProductService.findMartInfoByMartId(martAId);

        List<DiscountInfoDto> filteredDiscountInfo = mockloadData.getDiscountInfoList().stream()
                .filter(dto -> dto.getJoinId().equals(1L))
                .toList();
        assertThat(response.getData()).isEqualTo(filteredDiscountInfo);
        assertThat(response.getMessage()).isEqualTo(MartAndProductMessage.LOADED_MART_DETAILS.getMessage());
    }

    @Test
    @DisplayName("사용자 정보를 찾을 수 없는 경우")
    void 회원_정보_예외테스트() {
        when(userRepository.findById(memberId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> martProductService.findMartInfoByMartId(martAId));
        assertThat(exception.getMessage()).isEqualTo(MartAndProductMessage.NOT_FOUND_USER.getMessage());
    }

    @Test
    @DisplayName("장바구니가 비어있는 경우")
    void 장바구니_정보_예외테스트() {
        when(userRepository.findById(memberId)).thenReturn(Optional.of(user));
        when(cartRepository.findCartsByUser(user)).thenReturn(List.of());

        NoContentFoundException e = assertThrows(NoContentFoundException.class,
                () -> martProductService.findMartsByProductIds());
        assertThat(e.getMessage()).isEqualTo(MartAndProductMessage.EMPTY_CART.getMessage());
    }

    @Test
    @DisplayName("장바구니 상품목록이 비어있는 경우")
    void 장바구니_상품정보_예외테스트() {
        when(userRepository.findById(memberId)).thenReturn(Optional.of(user));
        when(cartRepository.findCartsByUser(user)).thenReturn(mockloadData.getCartProductList());
        when(productRepository.findById(productAId)).thenReturn(Optional.empty());
        when(productRepository.findById(productBId)).thenReturn(Optional.empty());

        NoContentFoundException e = assertThrows(NoContentFoundException.class,
                () -> martProductService.findMartsByProductIds());
        assertThat(e.getMessage()).isEqualTo(MartAndProductMessage.EMPTY_CART_PRODUCTS.getMessage());
    }

    @Test
    @DisplayName("마트 상세정보를 찾을 수 없는 경우")
    void 마트_상세정보_예외테스트() {
        when(userRepository.findById(memberId)).thenReturn(Optional.of(user));
        when(cartRepository.findCartsByUser(user)).thenReturn(mockloadData.getCartProductList());
        when(productRepository.findById(productAId)).thenReturn(Optional.of(mockloadData.getProductList().get(0)));
        when(productRepository.findById(productBId)).thenReturn(Optional.of(mockloadData.getProductList().get(1)));
        when(martRepository.findJoinIdByMartId(martAId)).thenReturn(Optional.empty());

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class,
                () -> martProductService.findMartInfoByMartId(martAId));
        assertThat(e.getMessage()).isEqualTo(MartAndProductMessage.NOT_FOUND_MART_DETAILS.getMessage());
    }
}