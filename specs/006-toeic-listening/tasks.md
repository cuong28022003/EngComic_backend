# Task Breakdown: TOEIC Listening (v1) — Backend + Transcriber

> **Perspective**: Backend Developer / AI Agent  
> **Purpose**: EXECUTION & CHECKLIST  
> **Instructions**: Chạy `mvn compile -DskipTests` sau mỗi phase trước khi tick.

---

## Phase 0: Transcriber Service (repo `EngComic_transcriber` mới)
- [ ] **B-001**: Scaffold FastAPI project (`app/main.py`, `requirements.txt`, `.env.example`, `README`).
- [ ] **B-002**: `POST /api/transcribe` — nhận multipart audio → faster-whisper (`small`, en, `word_timestamps=True`) → JSON `segments[]` (start_ms/end_ms/text) + `duration_ms`.
- [ ] **B-003**: Cài đặt + smoke test với 1 file mp3 test (chạy `uvicorn`, gọi endpoint).

## Phase 1: Data Model & DTO
- [ ] **B-004**: `ToeicTestEntity` — thêm `section`, `audioUrl`, `localAudioPath`, `audioStatus`, `audioMessage`.
- [ ] **B-005**: `ToeicQuestion` — thêm `audioStartMs(Long)`, `transcript(String)`.
- [ ] **B-006**: `CreateToeicTestRequest`/`UpdateToeicTestRequest` + `QuestionItem` — thêm `section`, `audioUrl`, `audioStartMs`, `transcript`; `ToeicTestDetailDto`/`SummaryDto`/`DashboardDto` — expose `section`, `audioUrl`, `audioStatus`.

## Phase 2: Upload & Streaming
- [ ] **B-007**: Controller multipart — thêm `@RequestPart("audioFile")`; interactor lưu `uploads/toeic_audio/`, set `audioUrl=/api/toeic/tests/audio/file/{filename}`.
- [ ] **B-008**: `GET /api/toeic/tests/audio/file/{filename:.+}` — stream + **Range/206** (`HttpRange`), content-type theo ext.
- [ ] **B-009**: `GET /api/toeic/tests/proxy-audio?url=` — mirror proxy-pdf; đăng ký public endpoints.

## Phase 3: Transcriber Client + Async Job
- [ ] **B-010**: `TranscriberClientService{Impl}` — RestTemplate POST multipart → segments; cấu hình `TRANSCRIBER_BASE_URL` trong `application.yml`.
- [ ] **B-011**: `AlignTranscriptToQuestionsInteractor` — thuật toán cue "Number N"/"Questions N through M" → `audioStartMs`/`transcript` (kèm fallback proportional).
- [ ] **B-012**: `TranscribeAudioInteractor` + `AudioProcessingJob` (ExecutorService 1 slot); `audioStatus` lifecycle.
- [ ] **B-013**: `POST /api/toeic/tests/{id}/transcribe` (202) — trigger/retry.

## Phase 4: Part-Agnostic & Scaled Score
- [ ] **B-014**: `StartTestAttemptInteractor` + `SubmitToeicSessionInteractor` — `selectedParts` default theo section; part-breakdown loop generic.
- [ ] **B-015**: `ScaledScoreConverter` — 2 bảng hằng int[101] (reading/listening) + `convert(raw, section)`; gắn vào submit (attempt + legacy session).

## Phase 5: Verification
- [ ] **B-016**: `mvn compile -DskipTests` = BUILD SUCCESS.
- [ ] **B-017**: Manual: create listening test → transcribe → `audioStatus=done`, `audioStartMs` monotonic đúng thứ tự câu; retry khi failed.
- [ ] **B-018**: Range 206 test qua curl; scaled score Reading/Listening ∈ [5,495].