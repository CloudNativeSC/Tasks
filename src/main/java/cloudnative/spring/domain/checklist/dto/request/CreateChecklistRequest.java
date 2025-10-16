package cloudnative.spring.domain.checklist.dto.request;

import lombok.Getter;

import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateChecklistRequest {
    private String name;
}