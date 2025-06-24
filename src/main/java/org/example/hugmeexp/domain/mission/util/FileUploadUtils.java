package org.example.hugmeexp.domain.mission.util;

import org.example.hugmeexp.domain.mission.enums.FileUploadType;
import org.example.hugmeexp.domain.mission.exception.SubMissionInternalException;
import org.example.hugmeexp.domain.mission.exception.SubmissionFileUploadException;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.*;

public final class FileUploadUtils {
    private FileUploadUtils() {}

    public static Path getUploadDir(FileUploadType uploadType) {
        // 절대 경로와 현재 작업 경로 조합, 필요에 따라 수정 가능
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
        if (fileName == null || fileName.isEmpty()) {
            throw new SubMissionInternalException("파일 이름이 비어 있거나 null입니다.");
        }

        return StringUtils.getFilename(StringUtils.cleanPath(fileName));
    }
}
