package org.example.hugmeexp.domain.qeust.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hugmeexp.domain.qeust.enums.QuestType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestRequest {

    private String name;
    private String url;
    private QuestType type;
}
