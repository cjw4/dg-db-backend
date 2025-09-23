package dg.swiss.swiss_dg_db.player;

import dg.swiss.swiss_dg_db.tournament.Tournament;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "Players")
@Getter
@Setter
public class Player {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "primary_sequence",
            sequenceName = "primary_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence"
    )
    private Long id;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(unique = true)
    private Long pdgaNumber;

    @Column(unique = true)
    private Long sdaNumber;

    @Column(nullable = false)
    private Boolean swisstourLicense;

    @Column(nullable = false)
    private Boolean isPro;

    @OneToMany(mappedBy = "player")
    private Set<Tournament> tournaments = new HashSet<>();

}
