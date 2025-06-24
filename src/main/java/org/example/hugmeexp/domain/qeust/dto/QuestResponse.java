package org.example.hugmeexp.domain.qeust.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestResponse {

    private Long id;
    private String name;
    private String url;
    private boolean isDeleted;
}