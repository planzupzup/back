package travel.travel.image.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import travel.travel.image.domain.Image;
import travel.travel.image.dto.ImageResDto;
import travel.travel.image.repository.ImageRepository;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Client s3Client;
    private final ImageRepository imageRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public List<ImageResDto> uploadFiles(List<MultipartFile> multipartFiles) {
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        return multipartFiles.stream()
                .map(file -> {
                    try {
                        String originalFilename = file.getOriginalFilename();
                        String uniqueFileName = UUID.randomUUID().toString() + "-" + originalFilename;

                        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                .bucket(bucket)
                                .key(uniqueFileName)
                                .contentType(file.getContentType())
                                .contentLength(file.getSize())
                                .build();

                        s3Client.putObject(putObjectRequest,
                                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

                        String url = s3Client.utilities().getUrl(builder ->
                                builder.bucket(bucket).key(uniqueFileName)).toString();

                        Image image = Image.builder().imageUrl(url).build();
                        imageRepository.save(image);

                        return ImageResDto.of(image);

                    } catch (IOException e) {
                        throw new RuntimeException("S3 업로드 실패");
                    }
                })
                .toList();
    }
}

