# Feature Specification: TOEIC Listening (v1) — Backend + Transcriber Service

> **Perspective**: Backend Product Owner & Architect  
> **Purpose**: "WHAT & WHY" cho backend Toeic Listening: lưu trữ, transcribe, alignment, scaled score.  
> **Version**: v1

---

## 1. Overview & Objective

- **Feature ID**: 006
- **Mục tiêu**: Hỗ trợ đề TOEIC **Listening** (Q1–100, Part 1–4) trên nền hạ tầng TOEIC Reading hiện có:
  1. Lưu **1 file audio full đề** mỗi test Listening.
  2. **Tự động transcribe + alignment** âm thanh → `audioStartMs` & `transcript` cho từng câu bằng **Python/Whisper service** (async).
  3. **Scaled score 5–495** theo section (Listening + Reading) với bảng chuẩn chung.
  4. Streaming audio hỗ trợ **HTTP Range (206)** để `<audio>` seek được.
- **Không làm**: import transcript/timestamp thủ công, `imageUrl`, xử lý Reading format mới.

---

## 2. Thay Đổi Data Model (MongoDB)

### `ToeicTestEntity` (collection `toeic_tests`)
```text
+ section            : String   // "reading" | "listening" (default "reading")
+ audioUrl           : String   // URL nội bộ /api/toeic/tests/audio/file/{filename} hoặc remote
+ localAudioPath     : String   // UUID + ext trong uploads/toeic_audio/
+ audioStatus        : String   // "none" | "processing" | "done" | "failed" (default "none")
+ audioMessage       : String   // lỗi transcribe nếu failed (optional)
```

### `ToeicQuestion` (embedded, collection `toeic_tests.questions[]`)
```text
+ audioStartMs : Long    // mốc bắt đầu câu trong audio full (Whisper alignment)
+ transcript   : String  // text Whisper cho câu/set
```

### Hardcoded part-ranges (Listening)
```text
Part 1: Q1–6   (6 câu)    Part 2: Q7–31  (25 câu, đáp án A/B/C)
Part 3: Q32–70 (39 câu)   Part 4: Q71–100(30 câu)
```

---

## 3. Endpoints (mở rộng `ToeicTestController` + mới)

| Method | Path | Mô tả |
|---|---|---|
| POST/PUT | `/api/toeic/tests` (multipart) | thêm `@RequestPart("audioFile")`; `requestData` nhận `section`, `audioUrl` |
| TRIGGER | `POST /api/toeic/tests/{id}/transcribe` | chạy lại job transcribe (async, trả ngay `202`) |
| STREAM | `GET /api/toeic/tests/audio/file/{filename:.+}` | stream audio **hỗ trợ Range 206** (`Content-Type` theo ext: mp3→audio/mpeg, m4a→audio/mp4, wav→audio/wav) |
| PROXY | `GET /api/toeic/tests/proxy-audio?url=` | fallback Cloudinary/remote (mirror `proxy-pdf`) |

- Thêm `audio/file/**`, `proxy-audio` vào `PublicSecurityEndpoints.java`.
- `GET /api/toeic/tests/{id}`/summary/dashboard: expose `section`, `audioUrl`, `audioStatus`.

---

## 4. Luồng Auto-Transcribe (async job)

```
[1] User upload audio khi tạo/update test (section=listening)
[2] Backend lưu file → uploads/toeic_audio/, set audioUrl nội bộ, audioStatus=processing
[3] Java gửi MultipartFile → Transcriber service (HTTP) POST /api/transcribe
[4] Transcriber: faster-whisper (model "small", en, word_timestamps) → segments [start,end,text]
[5] Java: AlignTranscriptToQuestionsInteractor
      → dò cue "Number N" / "Questions N through M" trong transcript (có word timestamps)
      → sinh audioStartMs[N] mỗi câu; transcript gán theo câu (P3/4 chung set)
      → fallback: proportional splitting theo part ranges nếu thiếu cue
[6] Cập nhật questions[] → audioStatus=done (hoặc failed + audioMessage)
[7] FE dashboard poll test detail (audioStatus) hiển thị tiến trình
```

---

## 5. Scaled Score 5–495 Theo Section

- `ScaledScoreConverter` (domain, constants): `int[] LISTENING_RAW_TO_SCALED[101]`, `int[] READING_RAW_TO_SCALED[101]` — bảng chuẩn chung (0→5 ... 100→495) theo các nguồn prep công khai, có thể tinh chỉnh.
- Gắn vào `SubmitToeicSessionInteractor` sau khi tính `rawScore`: set `scaledScore` cho attempt + legacy session; response `SubmitToeicSessionResponse.scaledScore` đã có sẵn.

---

## 6. Quy Ước Khác

- Giữ nguyên hành vi Reading hiện tại (Part 5/6/7, 30/16/54 câu): khi `section=reading` hoặc `section` rỗng → mặc định reading.
- `StartTestAttemptInteractor`/`SubmitToeicSessionInteractor`: `selectedParts` default theo section (`[1,2,3,4]` / `[5,6,7]`); vòng part-breakdown tổng quát theo selectedParts.
- Sau khi edit xong chạy `mvn compile -DskipTests` = BUILD SUCCESS.