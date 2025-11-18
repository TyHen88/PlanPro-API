package com.planprostructure.planpro.domain.trips;

import com.planprostructure.planpro.enums.Status;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "tb_destinations")
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "destination_id")
    private Long id;

    @Column(name = "id")
    private String destDate;

    @Column(name = "trip_id", nullable = false)
    private Long tripId;

    @Column(name = "destination_name")
    private String destinationName;

    @Column(name = "days")
    private Integer days;

    @Column(name = "activity")
    private String activities;

    @Column(name = "status")
    @JdbcTypeCode(Types.CHAR)
    @Convert(converter = Status.Converter.class)
    @ColumnDefault("1")
    private Status status;

    @Builder
    public Destination(Long tripId,String destDate, String destinationName, Integer days, String activities, Status status) {
        this.tripId = tripId;
        this.destDate = destDate;
        this.destinationName = destinationName;
        this.days = days;
        this.activities = activities;
        this.status = status;
    }
}
