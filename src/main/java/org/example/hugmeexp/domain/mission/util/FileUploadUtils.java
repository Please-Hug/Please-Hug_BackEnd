package org.example.hugmeexp.domain.mission.util;

import org.example.hugmeexp.domain.mission.enums.FileUploadType;
import org.example.hugmeexp.domain.mission.exception.SubMissionInternalException;
import org.example.hugmeexp.domain.mission.exception.SubmissionFileUploadException;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.*;

public final class FileUploadUtils {
    private FileUploadUtils() {}

    public static Path getUploadPath(FileUploadType uploadType) {
        Path uploadDir = Paths.get(System.getProperty("user.dir"), uploadType.value());
        if (!Files.exists(uploadDir)) {
            try {
                Files.createDirectories(uploadDir);
            } catch (IOException e) {
                throw new SubmissionFileUploadException("업로드 디렉토리를 생성할 수 없습니다.");
            }
        }
        return uploadDir;
    }

    public static String getSafeFileName(String fileName) {
        // 파일 이름이 null이거나 비어 있는지 확인
        if (fileName == null || fileName.isEmpty()) {
            throw new SubMissionInternalException("파일 이름이 비어 있거나 null입니다.");
        }

        // 위험한 문자 패턴 검증
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new SubMissionInternalException("허용되지 않는 문자가 포함된 파일명입니다.");
        }

        // 파일명 길이 제한
        if (fileName.length() > 255) {
            throw new SubMissionInternalException("파일명이 너무 깁니다.");
        }

        String cleanedFileName = StringUtils.getFilename(StringUtils.cleanPath(fileName));

        // 최종 검증
        if (cleanedFileName == null || cleanedFileName.isEmpty()) {
            throw new SubMissionInternalException("유효하지 않은 파일명입니다.");
        }

        return cleanedFileName;
    }
}
