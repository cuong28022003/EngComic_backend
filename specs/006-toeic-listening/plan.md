# Implementation Plan: TOEIC Listening (v1) — Backend Architect

> **Perspective**: Backend Architect / Lead Developer  
> **Purpose**: HOW & ARCHITECTURE (Clean Architecture + Transcriber client + async job).

---

## 1. Kiến Trúc Mới / Sửa Đổi

```
apis/reader/
├── ToeicTestController.java            ← multipart + audioFile; transcribe trigger; audio/file; proxy-audio
businesses/
├── boundaries/reader/CreateToeicTestBoundary / UpdateToeicTestBoundary   ← + audioFile, section
├── interactors/reader/
│   ├── CreateToeicTestInteractor.java  ← lưu audio local (uploads/toeic_audio/), set audioStatus=processing, launch job
│   ├── UpdateToeicTestInteractor.java  ← tương tự (xóa audio cũ nếu thay)
│   ├── TranscribeAudioInteractor.java  ← MỚI: orchestrate transcribe + align
│   ├── AlignTranscriptToQuestionsInteractor.java ← MỚI: segments → per-question audioStartMs/transcript
│   ├── StartTestAttemptInteractor.java ← default selectedParts theo section
│   └── SubmitToeicSessionInteractor.java ← default theo section; part-breakdown generic; set scaledScore
├── domains/reader/ScaledScoreConverter.java ← MỚI: 2 bảng hằng int[101] + convert(raw, section)
databases/services/
├── TranscriberClientService.java / Impl ← MỚI: HTTP → Python service (mirror TranslatorClientService)
├── AudioProcessingJob.java               ← MỚI: ExecutorService (1 job đồng thời), chain TranscribeAudio
```

## 2. Transcriber Service — `EngComic_transcriber` (repo Python mới)

```
EngComic_transcriber/
├── app/main.py          ← FastAPI, POST /api/transcribe (multipart file) → JSON segments+words
├── app/requirements.txt ← fastapi, uvicorn[standard], faster-whisper, python-multipart
├── app/.env.example     ← MODEL=small, DEVICE=cpu, COMPUTE_TYPE=int8, PORT=8000
└── README.md
```

**Response** (JSON):
```json
{
  "duration_ms": 2700000,
  "language": "en",
  "segments": [
    { "start_ms": 0, "end_ms": 5200, "text": "Now let's begin Part 1..." },
    { "start_ms": 6500, "end_ms": 12300, "text": "Number 1. The woman is ..." }
  ]
}
```
Ghi chú: `faster-whisper` dùng PyAV (bundle ffmpeg) → không bắt buộc cài ffmpeg hệ thống; `word_timestamps=True` để language-model vẫn hoạt động. Model `small`/`base` chạy CPU ok.

## 3. AlignTranscriptToQuestionsInteractor (thuật toán)

1. Giữ `ToeicTestEntity.questions` (biết `part` từng câu, đã sort theo number).
2. Regex trên transcript: `/Number\s+(\d+)/i`, `/Questions\s+(\d+)\s*(?:through|to|-|–)\s*(\d+)/i` → gom địa chỉ thời gian.
3. Với mỗi câu có cue → `audioStartMs[number] = cue.start_ms`. Cue `Questions N through M` gán start chung cho N..M nếu không tìm được cue số riêng.
4. `transcript` mỗi câu = text từ cue của đến cue kế tiếp (P3/4: 3 câu cùng track nhận chung text set).
5. Fallback (không tìm thấy cue): chia tỉ lệ từng part theo độ dài: `start = sum(prev part durations)`, chia đều theo `questionsPerPart`. Đánh dấu `audioStatus=done` nhưng ghi chú low-confidence (tuỳ chọn trong `audioMessage`).

## 4. Audio Streaming Range (206)

- Dùng Spring `ResourceRegion`/`HttpRange` để trả `206 Partial Content` cho `<audio>` seek:
  ```java
  List<HttpRange> ranges = HttpRange.parseRanges(rangeHeader);
  ResourceRegion region = ...; return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(region);
  ```
- `Content-Type` theo extension (mp3/m4a/wav), thêm `Accept-Ranges: bytes`.
- Thêm đường dẫn vào `PublicSecurityEndpoints`.

## 5. ScaledScoreConverter

- 2 hằng `int[101]` (index=raw, value=scaled). Source: bảng chuẩn công khai (VD bộ conversion phổ biến từ prep providers). Có thể khai báo thành `application.yml` array để dễ chỉnh không cần build.
- `convert(int rawScore, String section)` → clamp 5..495; null nếu rawScore ngoài phạm vi.

## 6. Xử Lý Job Async

- `AudioProcessingJob` dùng `ExecutorService` (1 slot) để giới hạn tải CPU; idempotent theo `testId` (chạy lại nếu status != processing).
- Timeout HTTP tới Transcriber: 10 phút. Lỗi → `audioStatus=failed`, `audioMessage`.
- Endpoint retry `POST /{id}/transcribe` gọi lại `AudioProcessingJob`.

## 7. Verify
- `mvn compile -DskipTests` = BUILD SUCCESS.
- Manual: upload đề Listening → tạo test → poll `audioStatus` → check `questions[].audioStartMs` monotonic ↔ cue "Number N", transcript đúng set.
- `curl -H "Range: bytes=0-1023" .../audio/file/x.mp3` trả `206` + `Accept-Ranges`.
- Scaled score: submit test Reading/Listening → response & attempt có `scaledScore` ∈ [5,495].