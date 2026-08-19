package mobile.apis.vocab.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardDetailResponseDto {
    private CardResponseDto card;
    private List<CardResponseDto> reverseRelations = new ArrayList<>();
}

