package cloudnative.spring.domain.task.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "Task Service";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * OAuth 인증 자격 증명 가져오기
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, String userId) throws IOException {
        // credentials.json 로드
        InputStream in = GoogleCalendarService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // OAuth 흐름 빌드
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH + "/" + userId)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Google Calendar 서비스 인스턴스 가져오기
     */
    private Calendar getCalendarService(String userId) throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, userId))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * 구글 캘린더에 이벤트 생성
     *
     * @param userId 사용자 ID
     * @param title 이벤트 제목
     * @param description 이벤트 설명
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 생성된 이벤트 ID
     */
    public String createCalendarEvent(String userId, String title, String description,
                                      LocalDateTime startTime, LocalDateTime endTime) throws Exception {
        log.info("구글 캘린더 이벤트 생성 - userId: {}, title: {}", userId, title);

        Calendar service = getCalendarService(userId);

        // 이벤트 생성
        Event event = new Event()
                .setSummary(title)
                .setDescription(description);

        // 시작 시간 설정
        DateTime startDateTime = new DateTime(
                Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant())
        );
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Asia/Seoul");
        event.setStart(start);

        // 종료 시간 설정
        DateTime endDateTime = new DateTime(
                Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant())
        );
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Asia/Seoul");
        event.setEnd(end);

        // 이벤트 삽입
        String calendarId = "primary";
        event = service.events().insert(calendarId, event).execute();

        log.info("✅ 구글 캘린더 이벤트 생성 완료 - eventId: {}", event.getId());
        return event.getId();
    }

    /**
     * 구글 캘린더 이벤트 수정
     *
     * @param userId 사용자 ID
     * @param eventId 이벤트 ID
     * @param title 제목
     * @param description 설명
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     */
    public void updateCalendarEvent(String userId, String eventId, String title, String description,
                                    LocalDateTime startTime, LocalDateTime endTime) throws Exception {
        log.info("구글 캘린더 이벤트 수정 - userId: {}, eventId: {}", userId, eventId);

        Calendar service = getCalendarService(userId);
        String calendarId = "primary";

        // 기존 이벤트 가져오기
        Event event = service.events().get(calendarId, eventId).execute();

        // 이벤트 정보 수정
        event.setSummary(title);
        event.setDescription(description);

        // 시작 시간 수정
        DateTime startDateTime = new DateTime(
                Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant())
        );
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Asia/Seoul");
        event.setStart(start);

        // 종료 시간 수정
        DateTime endDateTime = new DateTime(
                Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant())
        );
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Asia/Seoul");
        event.setEnd(end);

        // 이벤트 업데이트
        service.events().update(calendarId, eventId, event).execute();

        log.info("✅ 구글 캘린더 이벤트 수정 완료 - eventId: {}", eventId);
    }

    /**
     * 구글 캘린더 이벤트 삭제
     *
     * @param userId 사용자 ID
     * @param eventId 이벤트 ID
     */
    public void deleteCalendarEvent(String userId, String eventId) throws Exception {
        log.info("구글 캘린더 이벤트 삭제 - userId: {}, eventId: {}", userId, eventId);

        Calendar service = getCalendarService(userId);
        String calendarId = "primary";

        service.events().delete(calendarId, eventId).execute();

        log.info("✅ 구글 캘린더 이벤트 삭제 완료 - eventId: {}", eventId);
    }
}