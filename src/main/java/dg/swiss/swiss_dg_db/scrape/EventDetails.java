package dg.swiss.swiss_dg_db.scrape;

import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Component
@Setter
@Getter
public class EventDetails {
    private static final String baseUrl = "https://www.pdga.com/tour/event/";
    private String name;
    private LocalDate date;
    private int numberDays;
    private String tier;
    private String city;
    private String country;
    private Integer numberPlayers;
    private Double points;
    private Double purse;

    public void scrapeEventInfo(Long eventId) throws IOException {
        // get the DOM of the event
        Document document = Jsoup.connect(baseUrl + eventId).get();
        // scrape for event name and set it
        String name = scrapeName(document);
        this.setName(name);

        // scrape for event info
        Element eventInfo = document.selectFirst("div.panel-pane.pane-tournament-event-info");
        if (eventInfo != null) {
            DateConverter.DateInfo dateInfo = scrapeDateInfo(eventInfo);
            this.setDate(dateInfo.getStartDate());
            this.setNumberDays(dateInfo.getDays());
            LocationConverter.LocationInfo locationInfo = scrapeLocationInfo(eventInfo);
            this.setCity(locationInfo.getCity());
            this.setCountry(locationInfo.getCountry());
            this.setTier(scrapeTier(eventInfo));
        } else {
            this.setDate(LocalDate.EPOCH);
            this.setNumberDays(0);
            this.setTier("Event info not found");
        }

        // scrape for event number of players and purse
        this.setNumberPlayers(scrapeNumberPlayers(document));
        this.setPurse(scrapePurse(document));
    }

    private String scrapeName(Document document) {
        Element name = document.selectFirst("div.panel-pane.pane-page-title h1");
        if (name != null) {
            return name.text().trim();
        } else {
            return "Event name not found";
        }
    }

    private DateConverter.DateInfo scrapeDateInfo(Element eventInfo) {
        Element dateElement = eventInfo.selectFirst("li.tournament-date");
        if (dateElement != null) {
            String dateText = dateElement.text().replace("Date: ", "").trim();
            try {
                return DateConverter.convertDate(dateText);
            } catch (DateTimeParseException e) {
                throw e;
            }
        } else {
            return null;
        }
    }

    public LocationConverter.LocationInfo scrapeLocationInfo(Element eventInfo) {
        Element locationElement = eventInfo.selectFirst("li.tournament-location");
        if (locationElement != null) {
            String locationText = locationElement.text().replace("Location: ", "").trim();
            return LocationConverter.convertLocation(locationText);
        } else {
            return new LocationConverter.LocationInfo(null, null, null);
        }
    }

    private String scrapeTier(Element eventInfo) {
        Element tierElement = eventInfo.selectFirst("div.pane-content h4");
        if (tierElement != null) {
            return tierElement.text().trim();
        } else {
            return "Event tier not found";
        }
    }

    private Integer scrapeNumberPlayers(Document document) {
        Element numberPlayersElement = document.selectFirst("td.players");
        if (numberPlayersElement != null) {
            return Integer.parseInt(numberPlayersElement.text().trim());
        } else {
            return 0;
        }
    }

    private Double scrapePurse(Document document) {
        Element purseElement = document.selectFirst("td.purse");
        if (purseElement != null) {
            return Double.parseDouble(purseElement.text().replace("$", "").replace(",", "").trim());
        } else {
            return 0.00;
        }
    }
}
