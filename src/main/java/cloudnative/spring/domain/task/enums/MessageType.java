package cloudnative.spring.domain.task.enums;

//집중 모드 응원 메세지
public enum MessageType {
    INITIAL,      // 처음 시작 (1~9번)
    MILESTONE,    // 중요 이정표 (10, 20, 50, 100번)
    CONSISTENT,   // 꾸준함 (30번 단위)
    GENERAL       // 일반 응원 메시지
}