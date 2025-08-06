package travel.travel.image.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import travel.travel.image.domain.Image;
import travel.travel.image.dto.ImageResDto;
import travel.travel.image.repository.ImageRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
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
                        String datePath = LocalDate.now().toString();
                        String uniqueFileName = datePath + "/" + UUID.randomUUID() + "-" + originalFilename;

                        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                .bucket(bucket)
                                .key(uniqueFileName)
                                .contentType(file.getContentType())
                                .contentLength(file.getSize())
                                .build();

                        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

                        String url = s3Client.utilities().getUrl(builder ->
                                builder.bucket(bucket).key(uniqueFileName)).toString();

                        Image image = Image.builder()
                                .imageUrl(url)
                                .s3Key(uniqueFileName)
                                .build();

                        log.info("images= {}", image.getS3Key());
                        Image savedImage = imageRepository.save(image);
                        log.info("savedImage= {}", savedImage.getS3Key());
                        return ImageResDto.of(savedImage);

                    } catch (IOException e) {
                        log.error("S3 업로드 실패", e);
                        throw new RuntimeException("S3 업로드 실패: " + e.getMessage(), e);
                    }
                })
                .toList();
    }

    public void deleteImages(List<Image> images) {
        if (images == null || images.isEmpty()) return;

        List<String> keys = images.stream()
                .map(Image::getS3Key)
                .filter(StringUtils::hasText)
                .toList();

        if (keys.isEmpty()) {
            log.warn("삭제할 S3 키가 없습니다.");
            return;
        }

        List<ObjectIdentifier> identifiers = keys.stream()
                .map(key -> ObjectIdentifier.builder().key(key).build())
                .toList();

        DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                .bucket(bucket)
                .delete(builder -> builder.objects(identifiers))
                .build();

        s3Client.deleteObjects(deleteObjectsRequest);
        imageRepository.deleteAll(images);
    }

}
