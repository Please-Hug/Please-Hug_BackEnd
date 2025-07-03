package org.example.hugmeexp.domain.mission.util;

import org.example.hugmeexp.domain.mission.enums.FileUploadType;
import org.example.hugmeexp.domain.mission.exception.SubMissionInternalException;
import org.example.hugmeexp.domain.mission.exception.SubmissionFileUploadException;
import org.example.hugmeexp.domain.mission.util.FileUploadUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FileUploadUtils Test")
class FileUploadUtilsTest {

    @InjectMocks
    private FileUploadUtils fileUploadUtils;

    @Test
    void returnsCorrectUploadPathForValidType() {
        FileUploadType uploadType = FileUploadType.MISSION_UPLOADS;
        Path uploadPath = FileUploadUtils.getUploadPath(uploadType);
        assertTrue(uploadPath.toFile().exists());
        assertEquals(uploadType.value(), uploadPath.getFileName().toString());
    }

    @Test
    void returnsSafeFileNameForValidFileName() {
        String fileName = "valid_file_name.txt";
        String safeFileName = FileUploadUtils.getSafeFileName(fileName);
        assertEquals(fileName, safeFileName);
    }

    @Test
    void throwsExceptionForNullFileName() {
        assertThrows(SubMissionInternalException.class, () -> FileUploadUtils.getSafeFileName(null));
    }

    @Test
    void throwsExceptionForEmptyFileName() {
        assertThrows(SubMissionInternalException.class, () -> FileUploadUtils.getSafeFileName(""));
    }

    @Test
    void throwsExceptionForFileNameWithDangerousCharacters() {
        assertThrows(SubMissionInternalException.class, () -> FileUploadUtils.getSafeFileName("../dangerous.txt"));
        assertThrows(SubMissionInternalException.class, () -> FileUploadUtils.getSafeFileName("dangerous/file.txt"));
        assertThrows(SubMissionInternalException.class, () -> FileUploadUtils.getSafeFileName("dangerous\\file.txt"));
    }

    @Test
    void throwsExceptionForFileNameExceedingMaxLength() {
        String longFileName = "a".repeat(256) + ".txt";
        assertThrows(SubMissionInternalException.class, () -> FileUploadUtils.getSafeFileName(longFileName));
    }

    @Test
    void throwsExceptionForInvalidCleanedFileName() {
        String invalidFileName = "/invalid/path/";
        assertThrows(SubMissionInternalException.class, () -> FileUploadUtils.getSafeFileName(invalidFileName));
    }
}