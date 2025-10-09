package cloudnative.spring.domain.task.repository;

import cloudnative.spring.domain.task.entity.EncouragementMessage;
import cloudnative.spring.domain.task.enums.MessageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EncouragementMessageRepository extends JpaRepository<EncouragementMessage, Long> {

    // 타입별 메시지 조회
    List<EncouragementMessage> findByMessageType(MessageType messageType);

    // 랜덤 메시지 조회
    @Query(value = "SELECT message FROM encouragement_messages WHERE message_type = :type ORDER BY RAND() LIMIT 1", nativeQuery = true)
    String findRandomMessageByType(@Param("type") String type);

    // MessageType enum을 받는 헬퍼 메서드
    default String findRandomMessageByType(MessageType messageType) {
        return findRandomMessageByType(messageType.name());
    }
}