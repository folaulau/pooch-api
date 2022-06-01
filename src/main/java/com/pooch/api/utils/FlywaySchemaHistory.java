package com.pooch.api.utils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.DynamicUpdate;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.entity.DatabaseTableNames;


/**
 * Create this class just so flyway_schema_history can be wiped out on
 * spring.jpa.hibernate.ddl-auto=create
 */
//@JsonInclude(value = Include.NON_NULL)
//@Entity
//@Table(name = "flyway_schema_history")
//public class FlywaySchemaHistory {
//
//  @Id
//  @Column(name = "installed_rank")
//  private Long installedRank;
//}
