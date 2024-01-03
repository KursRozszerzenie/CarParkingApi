package com.example.carparkingapi.action;

import com.example.carparkingapi.model.ActionType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DiscriminatorFormula;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorFormula("action_type")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "action_type")
    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @CreatedBy
    private String createdBy;

    @CreatedDate
    private LocalDate createdDate;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private LocalDate lastModifiedDate;
}
