package com.planprostructure.planpro.domain.trips;

import com.planprostructure.planpro.payload.trips.IGetTrips;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TripsRepository extends JpaRepository<Trips, Long> {

    @Query(value = """
       select t.trip_id,
              t.user_id,
              t.sts,
              t.accommo,
              t.budget,
              t.location,
              t.cate,
              t.change_at,
              t.curr,
              t.description,
              t.end_date,
              t.img_url,
              t.mem,
              t.rmk,
              t.status,
              t.transpo,
              t.title,
              t.created_at,
              t.start_date
       from tb_trips t
       where t.user_id = :userId
         and t.status = '1'
         AND (:searchValue IS NULL OR :searchValue = '' OR
              (t.title ILIKE '%' || :searchValue || '%' OR t.description ILIKE '%' || :searchValue || '%'))
         AND (:filterCategory IS NULL OR :filterCategory = '' OR t.cate = :filterCategory)
         AND (:filterStatus IS NULL OR :filterStatus = '' OR t.status = :filterStatus)
       ORDER BY t.trip_id
       
""" ,
            nativeQuery = true)
    List<IGetTrips> findByUserIdAndFilters(
            @Param("userId") Long userId,
            @Param("searchValue") String searchValue,
            @Param("filterCategory") String filterCategory,
            @Param("filterStatus") String filterStatus
    );

}
