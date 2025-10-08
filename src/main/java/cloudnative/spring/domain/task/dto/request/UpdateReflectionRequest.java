package cloudnative.spring.domain.task.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReflectionRequest {

    @NotBlank(message = "쿨다운 내용은 필수입니다.")
    @Size(min = 1, max = 2000, message = "쿨다운 내용은 1자 이상 2000자 이하여야 합니다.")
    private String cooldownNote;
}