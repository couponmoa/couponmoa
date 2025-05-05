package com.couponmoa.backend.domain.coupon.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;

@Getter
@Setter //테스트용
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "coupons")
public class Search {

    @Id
    @Field(name = "coupon_id", type = FieldType.Long)
    private Long couponId;

    @Field(name = "name", type = FieldType.Keyword)
    private String name;

    @Field(name = "description", type = FieldType.Text)
    private String description;

    @Field(name = "discount_amount", type = FieldType.Double)
    private BigDecimal discountAmount;

    @Field(name = "discount_rate", type = FieldType.Double)
    private BigDecimal discountRate;

    @Field(name = "min_order_amount", type = FieldType.Double)
    private BigDecimal minOrderAmount;

    @Field(name = "max_order_amount", type = FieldType.Double)
    private BigDecimal maxDiscountAmount;

    @Field(name = "expiry_date", type = FieldType.Date)
    private String expiryDate;

    @Field(name = "store_id", type = FieldType.Long)
    private Long storeId;

    @Field(name = "store_name", type = FieldType.Keyword)
    private String storeName;
}
