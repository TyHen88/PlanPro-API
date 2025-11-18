package com.planprostructure.planpro.domain.trips;

import com.planprostructure.planpro.payload.trips.IGetTrips;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DestinationRepository extends JpaRepository<Destination, Long> {

    @Query(value = """
            SELECT td.trip_id,
                   json_agg(
                           json_build_object(
                                   'id', d.id,
                                   'destination_id', d.destination_id,
                                   'destination_name', d.destination_name,
                                   'activities', d.activity
                           )
                           ORDER BY d.destination_id
                   ) AS destinations
            FROM tb_destinations td
                     JOIN tb_destinations d ON td.destination_id = d.destination_id
            WHERE d.trip_id = ?1 and td.status <> '9'
            GROUP BY td.trip_id
            ORDER BY td.trip_id
            """, nativeQuery = true)
    List<IGetTrips> findAllByUserIdAndFilters(Long tripId);

    @Modifying
    @Query(value = """
            UPDATE tb_destinations
            SET status = '9'
            WHERE destination_id = ?1
            """, nativeQuery = true)
    void updateStatusToCancelled(Long dateDesId);
}
