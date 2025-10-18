package dg.swiss.swiss_dg_db.standings;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EventPointsDTO {
    private Long eventId;
    private Double points;
}
