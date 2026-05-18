package io.github.ptus04.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "transactions", schema = "storedb")
public class Transaction implements Serializable {
    @Id
    @Column(name = "id", nullable = false, length = 16)
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private String id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Size(max = 14)
    @NotNull
    @Column(name = "transaction_code", nullable = false, length = 14)
    private String transactionCode;

    @Size(max = 32)
    @NotNull
    @Column(name = "reference_code", nullable = false, length = 32)
    private String referenceCode;

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_payload", nullable = false)
    private Map<String, Object> rawPayload;

    @Size(max = 32)
    @NotNull
    @Column(name = "gateway_name", nullable = false, length = 32)
    private String gatewayName;

    @NotNull
    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @NotNull
    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_date", nullable = false)
    private Instant transactionDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;


}