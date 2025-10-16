package cloudnative.spring.domain.checklist.service;

import cloudnative.spring.domain.checklist.dto.request.CreateChecklistRequest;
import cloudnative.spring.domain.checklist.dto.response.ChecklistTemplateResponse;
import cloudnative.spring.domain.checklist.dto.response.UserChecklistResponse;
import cloudnative.spring.domain.checklist.entity.ChecklistItem;

import java.util.List;

public interface ChecklistService {

    /**
     * 모든 템플릿 목록 조회
     */
    List<ChecklistTemplateResponse> getAllTemplates();

    /**
     * 템플릿 상세 조회 (항목 포함)
     */
    ChecklistTemplateResponse getTemplateById(String templateId);

    /**
     * 템플릿으로부터 체크리스트 생성
     */
    UserChecklistResponse createChecklistFromTemplate(String userId, String templateId, CreateChecklistRequest request);

    /**
     * 사용자의 체크리스트 목록 조회
     */
    List<UserChecklistResponse> getUserChecklists(String userId);

    /**
     * 체크리스트 상세 조회 (항목 포함)
     */
    UserChecklistResponse getChecklistById(String checklistId);

    /**
     * 체크리스트 항목 토글 (체크/체크 해제)
     */
    ChecklistItem toggleItem(String itemId);

    /**
     * 체크리스트 삭제
     */
    void deleteChecklist(String checklistId);
}