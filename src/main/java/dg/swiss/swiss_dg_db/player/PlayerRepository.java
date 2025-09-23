package dg.swiss.swiss_dg_db.player;

import org.springframework.data.jpa.repository.JpaRepository;


public interface PlayerRepository extends JpaRepository<Player, Long> {

    boolean existsByPdgaNumber(Long pdgaNumber);

    boolean existsBySdaNumber(Long sdaNumber);

}
