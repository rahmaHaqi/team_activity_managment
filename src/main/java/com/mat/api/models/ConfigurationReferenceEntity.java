package com.mat.api.models;

import com.mat.api.core.crudbasic.auditing.CustomAuditEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@Table(name = "configuration_references")
@AllArgsConstructor
@NoArgsConstructor
public class ConfigurationReferenceEntity extends CustomAuditEntity {


    @Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "content")
    private String content;

    @ManyToMany
    @JoinTable(name = "configuration_configuration_references",
            joinColumns = @JoinColumn(name = "configuration_reference_id"),
            inverseJoinColumns = @JoinColumn(name = "configuration_id"))
    private List<ConfigurationEntity> configurations = new ArrayList<>();

   /*@OneToMany(mappedBy = "configurationReference")
    private List<ConfigurationEntity> configurations;

    */

}
