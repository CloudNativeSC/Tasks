package cloudnative.spring.domain.task.dto.request;


import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskTagRequest {
    private String name;
    private String colorCode;
}