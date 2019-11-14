package model;

import lombok.*;

import java.sql.Timestamp;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
@Setter
public class Assignment {
    private Long id;
    private String assignmentName;
    private Long createdAt;
    private Boolean isAlreadyFailed;
}
