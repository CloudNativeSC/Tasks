package cloudnative.spring.domain.checklist.initializer;

import cloudnative.spring.domain.checklist.entity.ChecklistTemplate;
import cloudnative.spring.domain.checklist.entity.ChecklistTemplateItem;
import cloudnative.spring.domain.checklist.repository.ChecklistTemplateItemRepository;
import cloudnative.spring.domain.checklist.repository.ChecklistTemplateRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChecklistTemplateInitializer {

    private final ChecklistTemplateRepository templateRepository;
    private final ChecklistTemplateItemRepository itemRepository;

    @PostConstruct
    @Transactional
    public void init() {
        if (templateRepository.count() > 0) {
            log.info("체크리스트 템플릿이 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        log.info("체크리스트 템플릿 초기화 시작");

        createTravelTemplate();
        createMovingTemplate();
        createWorkoutTemplate();
        createInterviewTemplate();
        createProjectTemplate();

        log.info("체크리스트 템플릿 초기화 완료! 총 5개 템플릿 생성됨");
    }

    /**
     * 1. 여행 준비 템플릿
     */
    private void createTravelTemplate() {
        ChecklistTemplate template = ChecklistTemplate.builder()
                .id("template-travel")
                .name("여행 준비")
                .description("여행 갈 때 필요한 체크리스트")
                .isSystemTemplate(true)
                .build();

        templateRepository.save(template);

        String[] items = {
                "여권 챙기기",
                "비행기 예약 확인",
                "숙소 예약 확인",
                "환전하기",
                "여행자 보험 가입",
                "짐 꾸리기",
                "충전기/어댑터 챙기기",
                "일정표 정리"
        };

        for (int i = 0; i < items.length; i++) {
            ChecklistTemplateItem item = ChecklistTemplateItem.builder()
                    .id(UUID.randomUUID().toString())
                    .template(template)
                    .content(items[i])
                    .displayOrder(i + 1)
                    .build();
            itemRepository.save(item);
        }

        log.info("여행 준비 템플릿 생성 완료 ({}개 항목)", items.length);
    }

    /**
     * 2. 이사 준비 템플릿
     */
    private void createMovingTemplate() {
        ChecklistTemplate template = ChecklistTemplate.builder()
                .id("template-moving")
                .name("이사 준비")
                .description("이사할 때 필요한 체크리스트")
                .isSystemTemplate(true)
                .build();

        templateRepository.save(template);

        String[] items = {
                "이사 업체 알아보기",
                "포장 박스 준비",
                "짐 정리 시작",
                "주소 변경 신청",
                "인터넷/전화 이전 신청",
                "전기/가스/수도 신청",
                "우편물 전송 신청",
                "냉장고 비우기",
                "청소 도구 준비",
                "이사 당일 체크리스트 작성"
        };

        for (int i = 0; i < items.length; i++) {
            ChecklistTemplateItem item = ChecklistTemplateItem.builder()
                    .id(UUID.randomUUID().toString())
                    .template(template)
                    .content(items[i])
                    .displayOrder(i + 1)
                    .build();
            itemRepository.save(item);
        }

        log.info("이사 준비 템플릿 생성 완료 ({}개 항목)", items.length);
    }

    /**
     * 3. 운동 루틴 템플릿
     */
    private void createWorkoutTemplate() {
        ChecklistTemplate template = ChecklistTemplate.builder()
                .id("template-workout")
                .name("운동 루틴")
                .description("매일 운동할 때 체크하는 루틴")
                .isSystemTemplate(true)
                .build();

        templateRepository.save(template);

        String[] items = {
                "스트레칭 10분",
                "런닝 30분",
                "복근 운동 3세트",
                "팔굽혀펴기 50개",
                "스쿼트 100개",
                "쿨다운 스트레칭"
        };

        for (int i = 0; i < items.length; i++) {
            ChecklistTemplateItem item = ChecklistTemplateItem.builder()
                    .id(UUID.randomUUID().toString())
                    .template(template)
                    .content(items[i])
                    .displayOrder(i + 1)
                    .build();
            itemRepository.save(item);
        }

        log.info("운동 루틴 템플릿 생성 완료 ({}개 항목)", items.length);
    }

    /**
     * 4. 면접 준비 템플릿
     */
    private void createInterviewTemplate() {
        ChecklistTemplate template = ChecklistTemplate.builder()
                .id("template-interview")
                .name("면접 준비")
                .description("면접 볼 때 준비할 체크리스트")
                .isSystemTemplate(true)
                .build();

        templateRepository.save(template);

        String[] items = {
                "이력서 업데이트",
                "자기소개 연습",
                "기술 질문 대비",
                "회사 조사",
                "정장 준비",
                "포트폴리오 점검",
                "예상 질문 답변 준비"
        };

        for (int i = 0; i < items.length; i++) {
            ChecklistTemplateItem item = ChecklistTemplateItem.builder()
                    .id(UUID.randomUUID().toString())
                    .template(template)
                    .content(items[i])
                    .displayOrder(i + 1)
                    .build();
            itemRepository.save(item);
        }

        log.info("면접 준비 템플릿 생성 완료 ({}개 항목)", items.length);
    }

    /**
     * 5. 프로젝트 시작 템플릿
     */
    private void createProjectTemplate() {
        ChecklistTemplate template = ChecklistTemplate.builder()
                .id("template-project")
                .name("프로젝트 시작")
                .description("새 프로젝트 시작할 때 체크리스트")
                .isSystemTemplate(true)
                .build();

        templateRepository.save(template);

        String[] items = {
                "Git 저장소 생성",
                "README 작성",
                "개발 환경 설정",
                "의존성 설치",
                ".gitignore 설정",
                "기본 구조 생성",
                "CI/CD 파이프라인 설정",
                "첫 커밋",
                "팀원 초대"
        };

        for (int i = 0; i < items.length; i++) {
            ChecklistTemplateItem item = ChecklistTemplateItem.builder()
                    .id(UUID.randomUUID().toString())
                    .template(template)
                    .content(items[i])
                    .displayOrder(i + 1)
                    .build();
            itemRepository.save(item);
        }

        log.info("프로젝트 시작 템플릿 생성 완료 ({}개 항목)", items.length);
    }
}