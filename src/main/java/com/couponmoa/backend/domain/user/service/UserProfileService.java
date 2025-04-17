package com.couponmoa.backend.domain.user.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    @Transactional
    public void updateUserImage(Long userId, MultipartFile multipartFile) throws IOException {
        User user = getUserById(userId);
        String userImageKey = uploadImageToS3(multipartFile);

        user.updateImageKey(userImageKey);

        userRepository.save(user);
    }

    @Transactional
    public void deleteUserImage(Long userId) {
        User user = getUserById(userId);

        String key = user.getImageKey();

        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));

        user.deleteImageKey();

        userRepository.save(user);
    }

    private User getUserById(Long userId) {
        return userRepository.findByIdOrElseThrow(userId, ErrorCode.USER_NOT_FOUND);
    }

    private String uploadImageToS3(MultipartFile multipartFile) throws IOException {
        System.out.println("메서드 실행2");
        String originalFilename = multipartFile.getOriginalFilename();
        String s3FileName = "image/" + UUID.randomUUID() + "_" + originalFilename;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());
        InputStream inputStream = multipartFile.getInputStream();
        try {
            amazonS3.putObject(new PutObjectRequest(bucketName, s3FileName, inputStream, metadata));
        } catch (AmazonServiceException e) {
            log.error("AWS S3 서비스 오류: {}", e.getErrorMessage());
            throw new ApplicationException(ErrorCode.S3_SERVICE_ERROR);
        } catch (SdkClientException e) {
            log.error("AWS S3 클라이언트 오류: {}", e.getMessage());
            throw new ApplicationException(ErrorCode.S3_CLIENT_ERROR);
        }
        return s3FileName;
    }
}
