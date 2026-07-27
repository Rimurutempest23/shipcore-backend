package com.shipcore.business.data.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("ORIGIN")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class OriginAddress extends QuoteAddress {
}
