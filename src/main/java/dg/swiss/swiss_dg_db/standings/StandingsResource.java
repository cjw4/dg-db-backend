package dg.swiss.swiss_dg_db.standings;

import dg.swiss.swiss_dg_db.tournament.TournamentPointsDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/api/standings")
public class StandingsResource {

    private final StandingService standingService;

    public StandingsResource(StandingService standingService) {
        this.standingService = standingService;
    }

    @GetMapping("/{division}")
    public ResponseEntity<List<StandingDTO>> getStandings(@PathVariable(name = "division") final String division) {
        return ResponseEntity.ok(standingService.getStandings(division));
    }
}
