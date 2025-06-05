package org.example.hugmeexp.global;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;


//@TODO 추후 수정 필

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private Timestamp createdDate;

    @LastModifiedDate
    @Column(name = "modified_date")
    private Timestamp modifiedDate;
}
