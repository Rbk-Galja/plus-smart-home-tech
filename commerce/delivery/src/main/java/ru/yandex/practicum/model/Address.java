package ru.yandex.practicum.model;

import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {
    String country;
    String city;
    String street;
    String house;
    String flat;
}
