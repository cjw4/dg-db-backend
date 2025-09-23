package dg.swiss.swiss_dg_db.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EventDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String displayName;

    @NotNull
    @Size(max = 255)
    private String tier;

    @NotNull
    private LocalDate date;

    @NotNull
    private Integer numberDays;

    @NotNull
    @Size(max = 255)
    private String city;

    @NotNull
    @Size(max = 255)
    private String country;

    @NotNull
    private Integer numberPlayers;

    private Double purse;

    @NotNull
    @JsonProperty("isChampionship")
    private Boolean isChampionship;

    @NotNull
    @JsonProperty("isSwisstour")
    private Boolean isSwisstour;

}
