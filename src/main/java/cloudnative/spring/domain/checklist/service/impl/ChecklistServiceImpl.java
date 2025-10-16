package cloudnative.spring.domain.checklist.service.impl;

import cloudnative.spring.domain.checklist.dto.request.CreateChecklistRequest;
import cloudnative.spring.domain.checklist.dto.response.ChecklistTemplateResponse;
import cloudnative.spring.domain.checklist.dto.response.UserChecklistResponse;
import cloudnative.spring.domain.checklist.entity.ChecklistItem;
import cloudnative.spring.domain.checklist.entity.ChecklistTemplate;
import cloudnative.spring.domain.checklist.entity.UserChecklist;
import cloudnative.spring.domain.checklist.repository.ChecklistItemRepository;
import cloudnative.spring.domain.checklist.repository.ChecklistTemplateRepository;
import cloudnative.spring.domain.checklist.repository.UserChecklistRepository;
import cloudnative.spring.domain.checklist.service.ChecklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChecklistServiceImpl implements ChecklistService {

    private final ChecklistTemplateRepository templateRepository;
    private final UserChecklistRepository checklistRepository;
    private final ChecklistItemRepository itemRepository;

    @Override
    public List<ChecklistTemplateResponse> getAllTemplates() {
        log.info("모든 체크리스트 템플릿 조회");

        // 시스템 템플릿만 조회 (기본 제공 템플릿)
        List<ChecklistTemplate> templates = templateRepository.findByIsSystemTemplateOrderByCreatedAtAsc(true);

        log.info("조회된 템플릿 개수: {}", templates.size());

        return templates.stream()
                .map(ChecklistTemplateResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public ChecklistTemplateResponse getTemplateById(String templateId) {
        log.info("템플릿 상세 조회 - templateId: {}", templateId);

        ChecklistTemplate template = templateRepository.findByIdWithItems(templateId)
                .orElseThrow(() -> new IllegalArgumentException("템플릿을 찾을 수 없습니다 - id: " + templateId));

        return ChecklistTemplateResponse.fromWithItems(template);
    }

    @Override
    @Transactional
    public UserChecklistResponse createChecklistFromTemplate(String userId, String templateId, CreateChecklistRequest request) {
        log.info("템플릿으로부터 체크리스트 생성 - userId: {}, templateId: {}, name: {}",
                userId, templateId, request.getName());

        // 템플릿 조회
        ChecklistTemplate template = templateRepository.findByIdWithItems(templateId)
                .orElseThrow(() -> new IllegalArgumentException("템플릿을 찾을 수 없습니다 - id: " + templateId));

        // 사용자 체크리스트 생성
        UserChecklist checklist = UserChecklist.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .name(request.getName())
                .templateId(templateId)
                .build();

        checklistRepository.save(checklist);

        // 템플릿 항목을 복사하여 체크리스트 항목 생성
        List<ChecklistItem> items = template.getItems().stream()
                .map(templateItem -> ChecklistItem.builder()
                        .id(UUID.randomUUID().toString())
                        .checklist(checklist)
                        .content(templateItem.getContent())
                        .displayOrder(templateItem.getDisplayOrder())
                        .build())
                .collect(Collectors.toList());

        itemRepository.saveAll(items);
        checklist.getItems().addAll(items);

        log.info("체크리스트 생성 완료 - checklistId: {}, 항목 개수: {}", checklist.getId(), items.size());

        return UserChecklistResponse.fromWithItems(checklist);
    }

    @Override
    public List<UserChecklistResponse> getUserChecklists(String userId) {
        log.info("사용자 체크리스트 목록 조회 - userId: {}", userId);

        List<UserChecklist> checklists = checklistRepository.findByUserIdOrderByCreatedAtDesc(userId);

        log.info("조회된 체크리스트 개수: {}", checklists.size());

        return checklists.stream()
                .map(UserChecklistResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public UserChecklistResponse getChecklistById(String checklistId) {
        log.info("체크리스트 상세 조회 - checklistId: {}", checklistId);

        UserChecklist checklist = checklistRepository.findByIdWithItems(checklistId)
                .orElseThrow(() -> new IllegalArgumentException("체크리스트를 찾을 수 없습니다 - id: " + checklistId));

        return UserChecklistResponse.fromWithItems(checklist);
    }

    @Override
    @Transactional
    public ChecklistItem toggleItem(String itemId) {
        log.info("체크리스트 항목 토글 - itemId: {}", itemId);

        ChecklistItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("항목을 찾을 수 없습니다 - id: " + itemId));

        // 토글
        item.toggle();

        log.info("항목 토글 완료 - itemId: {}, isChecked: {}", itemId, item.getIsChecked());

        // 체크리스트의 완료 상태 업데이트
        UserChecklist checklist = item.getChecklist();
        if (checklist.isAllCompleted()) {
            checklist.markAsCompleted();
            log.info("체크리스트 모두 완료! - checklistId: {}", checklist.getId());
        } else {
            checklist.unmarkCompleted();
        }

        return item;
    }

    @Override
    @Transactional
    public void deleteChecklist(String checklistId) {
        log.info("체크리스트 삭제 - checklistId: {}", checklistId);

        UserChecklist checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new IllegalArgumentException("체크리스트를 찾을 수 없습니다 - id: " + checklistId));

        checklistRepository.delete(checklist);

        log.info("체크리스트 삭제 완료 - checklistId: {}", checklistId);
    }
}