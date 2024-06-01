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