package dg.swiss.swiss_dg_db.scrape;

import lombok.Getter;

public class LocationConverter {

    @Getter
    public static class LocationInfo {
        private String city;
        private String state;
        private String country;

        public LocationInfo(String city, String state, String country) {
            this.city = city;
            this.state = state;
            this.country = country;
        }
    }

    public static LocationInfo convertLocation(String location) {
        if (location == null || location.isBlank()) {
            return new LocationInfo(null, null, null);
        }
        String[] parts = location.split(",\\s*");
        if (parts.length == 3) {
            return new LocationInfo(parts[0], parts[1], parts[2]);
        } else if (parts.length == 2) {
            return new LocationInfo(parts[0], null, parts[1]);
        } else {
            return new LocationInfo(location, null, null);
        }
    }
}
