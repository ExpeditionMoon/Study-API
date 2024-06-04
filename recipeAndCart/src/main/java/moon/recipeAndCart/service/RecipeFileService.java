package moon.recipeAndCart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RecipeFileService {

    @Value("${file.dir}")
    private String fileDir;

    /**
     * recipeId 와 step, 파일 이름으로 새로운 파일 저장
     *
     * @param recipeId 레시피 ID
     * @param step     메뉴얼 단계 번호
     * @param file     저장할 파일
     * @return 저장된 파일의 이름 반환
     * @throws IOException 이미지 파일 저장 중 예외 발생
     */
    public String saveFile(Long recipeId, Long step, MultipartFile file) throws IOException {
        Path fileStoragePath = Paths.get(fileDir).toAbsolutePath().normalize();
        Files.createDirectories(fileStoragePath);

        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileName = recipeId + "-" + step + "-" + originalFileName;
        Path targetLocation = fileStoragePath.resolve(fileName);

        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }
}