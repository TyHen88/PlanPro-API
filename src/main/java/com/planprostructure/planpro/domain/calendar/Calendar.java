package com.planprostructure.planpro.domain.calendar;

import com.planprostructure.planpro.domain.UpdatableEntity;
import com.planprostructure.planpro.enums.CalendarEnum;
import com.planprostructure.planpro.enums.Status;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "tb_calendar")
public class Calendar extends UpdatableEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "cal_id")
    private Long id;

    @Column(name = "event_title" , nullable = false)
    private String title;

    @Column(name = "start_date", length = 14, nullable = false)
    private String startDate;

    @Column(name = "end_date", length = 14, nullable = false)
    private String endDate;

    @Column(name = "start_time", length = 6, nullable = false)
    private String startTime;

    @Column(name = "end_time", length = 6, nullable = false)
    private String endTime;

    @Column(name = "description")
    private String description;

    @Column(name = "location")
    private String location;

    @Column(name = "trip_id")
    private Long tripId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "note_id")
    private Long noteId; //optional

    @Column(name = "cal_type")
    @JdbcTypeCode(Types.VARCHAR)
    @Convert(converter = CalendarEnum.Converter.class)
    private CalendarEnum calendarType;

    @Column(name = "status")
    @JdbcTypeCode(Types.CHAR)
    @Convert(converter = Status.Converter.class)
    @ColumnDefault("1")
    private Status status;

    @Column(name = "attendees")
    private String attendees; // Comma-separated list of user IDs

    @Column(name = "is_notify")
    @ColumnDefault("false")
    private boolean isNotify = false;

    @Builder
    public Calendar(Long id, String title, String startDate, String endDate, String startTime, String endTime, String description, String location, Long tripId, Long userId, Long noteId, CalendarEnum calendarType, Status status, String attendees, boolean isNotify) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.location = location;
        this.tripId = tripId;
        this.userId = userId;
        this.noteId = noteId;
        this.calendarType = calendarType;
        this.status = status;
        this.attendees = attendees;
        this.isNotify = isNotify;
    }
}
