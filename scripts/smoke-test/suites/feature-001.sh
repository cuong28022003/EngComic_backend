#!/usr/bin/env bash

run_feature_001_suite() {
    echo ""
    log_info "=== SUITE: FEATURE 001 (DECK & VOCAB VAULT) ==="

    if [[ -z "$ACCESS_TOKEN" ]]; then
        log_fail "Bỏ qua feature suite vì chưa có ACCESS_TOKEN"
        return 1
    fi

    # 1. Tạo Deck mới
    local create_deck_payload='{
        "name": "Solo Leveling Vocab",
        "description": "Vocabulary deck for Comic Reading"
    }'
    http_step "1. Tạo Deck mới" "POST" "/api/deck" "$create_deck_payload" "200" "$ACCESS_TOKEN"
    
    local deck_id
    deck_id=$(echo "$LAST_BODY" | grep -o '"id":"[^"]*' | head -n1 | cut -d'"' -f4)
    if [[ -z "$deck_id" ]]; then
        deck_id="deck-smoke-test"
    fi
    log_info "Deck ID vừa tạo: $deck_id"

    # 2. Lấy danh sách Deck theo current user id
    if [[ -n "$CURRENT_USER_ID" ]]; then
        http_step "2. Lấy danh sách Deck của User" "GET" "/api/deck/user/$CURRENT_USER_ID" "" "200" "$ACCESS_TOKEN"
    fi

    # 3. Tạo Card đơn lẻ
    local create_card_payload="{
        \"front\": \"resilience\",
        \"back\": \"sự kiên cường\",
        \"ipa\": \"/rɪˈzɪl.jəns/\",
        \"partOfSpeech\": \"noun\",
        \"definitionEn\": \"the ability to recover quickly from difficulties\",
        \"deckId\": \"$deck_id\",
        \"topic\": \"personal-growth\",
        \"examples\": [
            { \"sentence\": \"He showed great resilience during the crisis.\", \"formality\": \"formal\", \"translation\": \"Anh ấy thể hiện sự kiên cường to lớn trong khủng hoảng.\" }
        ],
        \"relations\": [
            { \"relatedText\": \"resilient\", \"relationType\": \"family\", \"pos\": \"adjective\" }
        ]
    }"
    http_step "3. Tạo Card mới" "POST" "/api/card" "$create_card_payload" "200" "$ACCESS_TOKEN"

    local card_id
    card_id=$(echo "$LAST_BODY" | grep -o '"id":"[^"]*' | head -n1 | cut -d'"' -f4)
    if [[ -z "$card_id" ]]; then
        card_id="card-smoke-test"
    fi
    log_info "Card ID vừa tạo: $card_id"

    # 4. Xem chi tiết Card
    http_step "4. Xem chi tiết Card" "GET" "/api/card/$card_id" "" "200" "$ACCESS_TOKEN"

    # 5. Xem Dashboard thống kê
    http_step "5. Xem Dashboard thống kê Card" "GET" "/api/card/dashboard" "" "200" "$ACCESS_TOKEN"

    # 6. Lấy danh sách thẻ đến hạn ôn tập (Practice Due)
    http_step "6. Lấy danh sách Due Cards để luyện tập" "GET" "/api/card/practice/due?limit=10" "" "200" "$ACCESS_TOKEN"

    # 7. Nộp kết quả luyện tập SRS (Practice Result - Quality 4 GOOD)
    local practice_payload='{
        "quality": 4
    }'
    http_step "7. Nộp kết quả ôn tập SRS (Quality 4)" "POST" "/api/card/$card_id/practice-result" "$practice_payload" "200" "$ACCESS_TOKEN"

    # 8. Batch Import JSON (100% đúng schema)
    local batch_json='[{\"word\":\"tenacity\",\"ipa\":\"/təˈnæs.ə.ti/\",\"part_of_speech\":\"noun\",\"meaning_vi\":\"sự ngoan cường\",\"definition_en\":\"the quality of being determined\",\"topic\":\"general\",\"examples\":[{\"text\":\"His tenacity helped him succeed.\",\"formality\":\"formal\"}],\"relations\":[{\"text\":\"tenacious\",\"type\":\"family\",\"pos\":\"adj\"}]}]'
    local batch_payload="{\"jsonContent\":\"$batch_json\",\"deckId\":\"$deck_id\"}"
    http_step "8. Batch Import từ vựng JSON" "POST" "/api/card/batch-import" "$batch_payload" "200" "$ACCESS_TOKEN"

    # 9. Word Collector - Thêm từ vào danh sách chờ (Pending Item)
    local pending_payload="{
        \"content\": \"unyielding\",
        \"sourceType\": \"manual\"
    }"
    http_step "9. Thêm từ vào Pending Queue" "POST" "/api/pending-item" "$pending_payload" "201" "$ACCESS_TOKEN"

    # 10. Word Collector - Tạo Prompt AI từ danh sách chờ
    http_step "10. Tạo Prompt AI từ Pending Items" "GET" "/api/pending-item/generate-prompt" "" "200" "$ACCESS_TOKEN"
}
