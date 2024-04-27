package com.mat.api.models;

import com.mat.api.core.crudbasic.auditing.CustomAuditEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Getter
@Setter
@Table(name = "configurations")
@AllArgsConstructor
@NoArgsConstructor
public class ConfigurationEntity extends CustomAuditEntity {

    @Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "content")
    private String content;


    @ManyToOne
    @JoinColumn(name = "project_id")
    private ProjectEntity project;

}
