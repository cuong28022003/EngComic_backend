package mobile.model.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMember {
    private String word;
    private String partOfSpeech; // "noun", "verb", "adjective", "adverb"
    private String ipa;
    private String meaningVi;
    private String cardId;       // Link tới Card ID nếu có
}
