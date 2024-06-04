package moon.recipeAndCart.util.enums;

public enum RecipeMessage {
    /**
     * SUCCESS
     */
    LOADED_RECIPE("레시피 성공적으로 불러왔습니다."),
    LOADED_RECIPE_LIST("레시피 목록을 성공적으로 불러왔습니다."),


    /**
     * ERROR
     */
    NOT_FOUND_RECIPE("레시피를 찾을 수 없습니다."),
    NOT_FOUND_RECIPE_MANUAL("레시피 메뉴얼을 찾을 수 없습니다."),
    NOT_FOUND_RECIPE_IMG("레시피 이미지를 찾을 수 없습니다."),
    EMPTY_RECIPE_LIST("레시피 목록이 비어있습니다."),
    ERROR_RECIPE_IMG_PROCESSING("이미지 파일 처리 중 오류가 발생했습니다.");

    private final String message;

    RecipeMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
