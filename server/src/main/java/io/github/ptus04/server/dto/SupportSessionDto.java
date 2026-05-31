package io.github.ptus04.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportSessionDto {
    private String sessionId;
    private boolean active;
    private String lastMessage;
    private String customerName;
    private String staffName;
}
