package travel.travel.location.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import travel.travel.location.dto.LocationCreateReqDto;

import java.util.ArrayList;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class LocationBulkJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void bulkInsertByDay(List<List<LocationCreateReqDto>> locationsByDay, Long planId, int batchSize) {

        String sql = "INSERT INTO location " +
                "(day, description, google_image_url, latitude, location_name, " +
                "longitude, place_id, plan_id, rating, schedule_order, types) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> batchArgs = new ArrayList<>();

        int day = 1;
        for (List<LocationCreateReqDto> dayLocations : locationsByDay) {
            int scheduleOrder = 1;

            for (LocationCreateReqDto loc : dayLocations) {
                batchArgs.add(new Object[]{
                        day,
                        loc.getDescription(),
                        loc.getGoogleImageUrl(),
                        loc.getLatitude(),
                        loc.getLocationName(),
                        loc.getLongitude(),
                        loc.getPlaceId(),
                        planId,
                        loc.getRating(),
                        scheduleOrder,
                        loc.getTypes()
                });
                scheduleOrder++;
            }
            day++;
        }

        for (int i = 0; i < batchArgs.size(); i += batchSize) {
            int end = Math.min(i + batchSize, batchArgs.size());
            jdbcTemplate.batchUpdate(sql, batchArgs.subList(i, end));
        }
    }
}