package travel.travel.location.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import travel.travel.location.dto.LocationCreateReqDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
@RequiredArgsConstructor
public class LocationBulkJdbcRepository {

    private final JdbcTemplate jdbcTemplate;
    private int batchSize = 1000;

    public void bulkInsertByDay(List<List<LocationCreateReqDto>> locationsByDay, Long planId) {
        List<Object[]> locationBatch = new ArrayList<>();
        List<LocationImageData> imageBatch = new ArrayList<>();

        prepareLocationData(locationsByDay, planId, locationBatch, imageBatch);
        bulkInsertLocations(locationBatch);
        if (!imageBatch.isEmpty()) {
            bulkInsertLocationImages(planId, imageBatch);
        }
    }

    private void prepareLocationData(List<List<LocationCreateReqDto>> locationsByDay, Long planId,
                                      List<Object[]> locationBatch, List<LocationImageData> imageBatch) {
        int day = 1;
        for (List<LocationCreateReqDto> dayLocations : locationsByDay) {
            int scheduleOrder = 1;
            for (LocationCreateReqDto loc : dayLocations) {
                locationBatch.add(new Object[]{
                        day, loc.getDescription(), loc.getGoogleImageUrl(), loc.getLatitude(),
                        loc.getLocationName(), loc.getLongitude(), loc.getPlaceId(), planId,
                        loc.getRating(), scheduleOrder, loc.getTypes()
                });

                if (loc.getImages() != null && !loc.getImages().isEmpty()) {
                    imageBatch.add(new LocationImageData(day, scheduleOrder, loc.getImages()));
                }
                scheduleOrder++;
            }
            day++;
        }
    }

    private void bulkInsertLocations(List<Object[]> locationBatch) {
        String sql = "INSERT INTO location " +
                "(day, description, google_image_url, latitude, location_name, " +
                "longitude, place_id, plan_id, rating, schedule_order, types) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        executeBatch(sql, locationBatch);
    }

    private void bulkInsertLocationImages(Long planId, List<LocationImageData> imageBatch) {
        Map<String, Long> locationIdMap = fetchLocationIdMap(planId);
        List<Object[]> imageBatchData = prepareImageBatchData(imageBatch, locationIdMap);

        if (!imageBatchData.isEmpty()) {
            String sql = "INSERT INTO location_images (location_id, image_url, image_order) VALUES (?, ?, ?)";
            executeBatch(sql, imageBatchData);
        }
    }

    private Map<String, Long> fetchLocationIdMap(Long planId) {
        String sql = "SELECT location_id, day, schedule_order FROM location WHERE plan_id = ? ORDER BY day, schedule_order";

        List<LocationIdMapping> mappings = jdbcTemplate.query(sql, new Object[]{planId},
                (rs, rowNum) -> new LocationIdMapping(
                        rs.getLong("location_id"),
                        rs.getInt("day"),
                        rs.getInt("schedule_order")
                )
        );

        Map<String, Long> map = new HashMap<>();
        for (LocationIdMapping mapping : mappings) {
            map.put(mapping.day + "-" + mapping.scheduleOrder, mapping.locationId);
        }
        return map;
    }

    private List<Object[]> prepareImageBatchData(List<LocationImageData> imageBatch, Map<String, Long> locationIdMap) {
        List<Object[]> batchData = new ArrayList<>();

        for (LocationImageData imageData : imageBatch) {
            Long locationId = locationIdMap.get(imageData.day + "-" + imageData.scheduleOrder);
            if (locationId != null) {
                int imageOrder = 0;
                for (String imageUrl : imageData.images) {
                    batchData.add(new Object[]{locationId, imageUrl, imageOrder++});
                }
            }
        }
        return batchData;
    }

    private void executeBatch(String sql, List<Object[]> batch) {
        for (int i = 0; i < batch.size(); i += batchSize) {
            int end = Math.min(i + batchSize, batch.size());
            jdbcTemplate.batchUpdate(sql, batch.subList(i, end));
        }
    }

    private static class LocationImageData {
        int day;
        int scheduleOrder;
        List<String> images;

        LocationImageData(int day, int scheduleOrder, List<String> images) {
            this.day = day;
            this.scheduleOrder = scheduleOrder;
            this.images = images;
        }
    }

    private static class LocationIdMapping {
        Long locationId;
        int day;
        int scheduleOrder;

        LocationIdMapping(Long locationId, int day, int scheduleOrder) {
            this.locationId = locationId;
            this.day = day;
            this.scheduleOrder = scheduleOrder;
        }
    }

}