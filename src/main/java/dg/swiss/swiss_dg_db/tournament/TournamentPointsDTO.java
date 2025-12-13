package dg.swiss.swiss_dg_db.tournament;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class TournamentPointsDTO {
    private String division;
    private Long playerId;
    private Long eventId;
    private Boolean swisstourLicense;
    private Boolean isSwisstour;
    private Double points;
}
