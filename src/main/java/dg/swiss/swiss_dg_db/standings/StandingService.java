package dg.swiss.swiss_dg_db.standings;

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

    public StandingService(TournamentRepository tournamentRepository,
                           PlayerRepository playerRepository) {
        this.tournamentRepository = tournamentRepository;
        this.playerRepository = playerRepository;
    }

    public List<StandingDTO> getStandings(String division) {
        List<TournamentPointsDTO> tournamentPointsDTOs = tournamentRepository.findTournamentPointsByDivision(division);
        List<StandingDTO> standingDTOs = tournamentPointsDTOs.stream()
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

        // Keep only players with swisstourLicense == true
        List<StandingDTO> licensedStandingDTOs = standingDTOs.stream()
                .filter(s -> playerRepository.findById(s.getPlayerId())
                        .map(Player::getSwisstourLicense)
                        .orElse(false))
                .toList();

        return calculateRankings(licensedStandingDTOs);
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
