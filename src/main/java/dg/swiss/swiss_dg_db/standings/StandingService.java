package dg.swiss.swiss_dg_db.standings;

import dg.swiss.swiss_dg_db.event.EventRepository;
import dg.swiss.swiss_dg_db.player.Player;
import dg.swiss.swiss_dg_db.player.PlayerRepository;
import dg.swiss.swiss_dg_db.tournament.TournamentPointsDTO;
import dg.swiss.swiss_dg_db.tournament.TournamentRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StandingService {
    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;
    private final EventRepository eventRepository;

    public StandingService(TournamentRepository tournamentRepository,
                           PlayerRepository playerRepository, EventRepository eventRepository) {
        this.tournamentRepository = tournamentRepository;
        this.playerRepository = playerRepository;
        this.eventRepository = eventRepository;
    }

    public List<StandingDTO> getStandings(String division) {
        List<TournamentPointsDTO> tournamentPointsDTOs = tournamentRepository.findTournamentPointsByDivision(division);
        List<StandingDTO> standingDTOs = tournamentPointsDTOs.stream()
                // only include swisstour events
                .filter(TournamentPointsDTO::getIsSwisstour)
                // only include players with swisstour license
                .filter(TournamentPointsDTO::getSwisstourLicense)
                // turn the TournamentPointsDTOs into StandingDTOs
                .collect(Collectors.groupingBy(TournamentPointsDTO::getPlayerId))
                .entrySet().stream()
                .map(entry -> {
                    Long playerId = entry.getKey();
                    List<EventPointsDTO> eventPointsDTOs = entry.getValue().stream()
                            .map(event -> new EventPointsDTO(event.getEventId(), event.getPoints()))
                            .toList();
                    Double totalPoints = eventPointsDTOs.stream()
                            .mapToDouble(EventPointsDTO::getPoints)
                            .sum();
                    return new StandingDTO(playerId, eventPointsDTOs, totalPoints, 0);
                }).toList();

        return calculateRankings(standingDTOs);
    }

    private List<StandingDTO> calculateRankings(List<StandingDTO> standingDTOs) {
        // Sort events by total points in descending order
        List<StandingDTO> rankedStandingDTOs = standingDTOs.stream()
                .sorted(Comparator.comparing(StandingDTO::getTotalPoints).reversed())
                .toList();

        // Calculate ranks with handling for tied points
        List<StandingDTO> finalRankedStandingDTOs = new ArrayList<>();
        int currentRank = 1;
        Double previousTotal = rankedStandingDTOs.isEmpty() ? 0 : rankedStandingDTOs.getFirst().getTotalPoints();

        for (int i = 0; i < rankedStandingDTOs.size(); i++) {
            StandingDTO standingDTO = rankedStandingDTOs.get(i);

            // Assign rank, keeping same rank for same total points
            if (i > 0 && standingDTO.getTotalPoints() < previousTotal) {
                currentRank = i + 1;
            }

            finalRankedStandingDTOs.add(new StandingDTO(
                    standingDTO.getPlayerId(),
                    standingDTO.getEventPoints(),
                    standingDTO.getTotalPoints(),
                    currentRank
            ));

            previousTotal = standingDTO.getTotalPoints();
        }

        return finalRankedStandingDTOs;
    }
}
