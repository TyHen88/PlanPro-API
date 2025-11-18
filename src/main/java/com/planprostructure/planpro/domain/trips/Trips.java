package com.planprostructure.planpro.domain.trips;


import com.planprostructure.planpro.domain.UpdatableEntity;
import com.planprostructure.planpro.enums.*;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import java.math.BigDecimal;
import java.sql.Types;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_trips")
public class Trips extends UpdatableEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "trip_id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date", length = 14)
    private String startDate;

    @Column(name = "end_date", length = 14)
    private String endDate;

    @Column(name = "cate")
    @JdbcTypeCode(Types.VARCHAR)
    @Convert(converter = CategoryEnums.Converter.class)
    private CategoryEnums category;

    @Column(name = "sts")
    @JdbcTypeCode(Types.VARCHAR)
    @Convert(converter = TripsStatus.Converter.class)
    private TripsStatus status; // e.g., PLANNED, IN_PROGRESS, COMPLETED

    @Column(name = "budget")
    private BigDecimal budget; // Budget for the trip

    @Column(name = "curr")
    @JdbcTypeCode(Types.CHAR)
    @Convert(converter = CurrencyEnum.Converter.class)
    private CurrencyEnum currency; // Currency for the budget, e.g., USD, EUR

    @Column(name = "mem")
    private String travelers; // Comma-separated list of member names

    @Column(name = "accommo")
    private String accommodation; // Accommodation details

    @Column(name = "transpo")
    private String transportation; // Transportation details

    @Column(name = "rmk")
    private String remarks; // Additional remarks or notes about the trip

    @Column(name = "img_url")
    private String imageUrl;

    @Column(name = "location")
    private String location;

    @Column(name = "status")
    @JdbcTypeCode(Types.CHAR)
    @Convert(converter = Status.Converter.class)
    @ColumnDefault("1")
    private Status tripStatus;

    @Column(name = "is_cal_event")
    @JdbcTypeCode(Types.BOOLEAN)
    @ColumnDefault("false")
    private Boolean isCalendarEvent = false; // Flag to indicate if the trip is linked to a calendar event

    @Column(name = "is_notify")
    private boolean isNotify = false;

    @Builder
    public Trips(Long id, Long userId, String title, String description, String startDate, String endDate, CategoryEnums category, TripsStatus status, BigDecimal budget, CurrencyEnum currency, String travelers, String accommodation, String transportation, String remarks, String imageUrl, String location, Status tripStatus, Boolean isCalendarEvent, boolean isNotify) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.category = category;
        this.status = status;
        this.budget = budget;
        this.currency = currency;
        this.travelers = travelers;
        this.accommodation = accommodation;
        this.transportation = transportation;
        this.remarks = remarks;
        this.imageUrl = imageUrl;
        this.location = location;
        this.tripStatus = tripStatus;
        this.isCalendarEvent = isCalendarEvent;
        this.isNotify = isNotify;
    }



}
