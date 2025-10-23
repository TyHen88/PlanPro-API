package com.planprostructure.planpro.domain.chatRoom;

import com.planprostructure.planpro.domain.users.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Builder;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "chat_rooms")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType type;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Users createdBy;

    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<RoomParticipant> participants = new HashSet<>();

    public enum RoomType {
        DIRECT, GROUP
    }

}
