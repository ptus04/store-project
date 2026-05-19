package io.github.ptus04.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "order_shipping_addresses", schema = "storedb")
public class OrderShippingAddress {
    @Id
    @Column(name = "order_id", nullable = false, length = 16)
    private UUID orderId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Size(max = 128)
    @NotNull
    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @Size(max = 10)
    @NotNull
    @Column(name = "phone", nullable = false, length = 10)
    private String phone;

    @Size(max = 32)
    @NotNull
    @Column(name = "city", nullable = false, length = 32)
    private String city;

    @Size(max = 32)
    @NotNull
    @Column(name = "district", nullable = false, length = 32)
    private String district;

    @Size(max = 128)
    @NotNull
    @Column(name = "ward", nullable = false, length = 128)
    private String ward;

    @Size(max = 128)
    @NotNull
    @Column(name = "address", nullable = false, length = 128)
    private String address;


}