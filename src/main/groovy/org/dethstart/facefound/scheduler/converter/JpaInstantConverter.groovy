package org.dethstart.facefound.scheduler.converter

import javax.persistence.AttributeConverter
import java.sql.Timestamp
import java.time.Instant

class JpaInstantConverter implements AttributeConverter<Instant, Timestamp> {

    @Override
    Instant convertToEntityAttribute(Timestamp timestamp) {
        if (timestamp == null) {
            return null
        }

        return timestamp.toInstant()
    }

    @Override
    Timestamp convertToDatabaseColumn(Instant instant) {
        if (instant == null) {
            return null
        }

        return Timestamp.from(instant)
    }
}
