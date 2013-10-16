package mappers

import slick.lifted.MappedTypeMapper
import java.sql.Timestamp
import org.joda.time.DateTime

object DateTimeMapper {

  implicit def date2dateTime = MappedTypeMapper.base[DateTime, Timestamp] (
    dateTime => new java.sql.Timestamp(dateTime.getMillis),
    date => new DateTime(date)
  )

}