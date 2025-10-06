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
public class CreateTaskTagRequest {
    
    @NotBlank(message = "태그 이름은 필수입니다.")
    @Size(min = 1, max = 30, message = "태그 이름은 1자 이상 30자 이하여야 합니다.")
    private String name;
    
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", 
             message = "색상 코드는 '#' 으로 시작하는 6자리 또는 3자리 Hex 값이어야 합니다. (예: #FF5733)")
    private String colorCode;
}