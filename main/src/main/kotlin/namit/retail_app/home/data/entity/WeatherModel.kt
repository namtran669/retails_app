package namit.retail_app.home.data.entity

import namit.retail_app.home.enums.DaySessionType
import namit.retail_app.home.enums.WeatherType

data class WeatherModel(
    var type: WeatherType = WeatherType.UNKNOWN,
    var session: DaySessionType = DaySessionType.MORNING
)