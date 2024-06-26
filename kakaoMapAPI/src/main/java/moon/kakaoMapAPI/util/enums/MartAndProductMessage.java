package moon.kakaoMapAPI.util.enums;

public enum MartAndProductMessage {
    /**
     * SUCCESS
     */
    LOADED_TOTAL_SUM("마트별 총 합계를 성공적으로 불러왔습니다."),
    LOADED_MART_DETAILS("마트 상품의 세부사항을 성공적으로 불러왔습니다."),

    /**
     * ERROR
     */
    NOT_FOUND_USER("사용자 정보를 찾을 수 없습니다."),
    NOT_FOUND_MART_DETAILS("해당 마트에 대한 정보를 찾을 수 없습니다."),
    EMPTY_CART("장바구니가 비어있습니다."),
    EMPTY_CART_PRODUCTS("장바구니 상품목록이 비어있습니다.");

    private final String message;

    MartAndProductMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
