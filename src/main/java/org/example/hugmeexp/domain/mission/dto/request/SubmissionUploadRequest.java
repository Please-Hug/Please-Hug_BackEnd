package org.example.hugmeexp.domain.mission.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class SubmissionUploadRequest {
    @NotBlank
    @Length(min = 1, max = 65535)
    private String comment;

    @NotBlank
    @Length(min = 1, max = 255)
    private String fileName;
}
