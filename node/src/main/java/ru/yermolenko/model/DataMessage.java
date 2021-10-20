package ru.yermolenko.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "data_messages")
public class DataMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer externalServiceId;
    @OneToOne
    private ServiceUser serviceUser;
    @Type(type = "text")
    private String messageText;
    @OneToOne
    private Document doc;
    @OneToOne
    private Photo photo;
    private Long chatId;
    @Enumerated(EnumType.STRING)
    private ChatType chatType;
    private Integer unixTimeFromExternalService;
    @CreationTimestamp
    private LocalDateTime createDate;
    @UpdateTimestamp
    private LocalDateTime modifyDate;
    @ManyToOne
    private Bot bot;
}
