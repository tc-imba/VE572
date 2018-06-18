library (nycflights13)
library (dplyr)
library (ggplot2)

# (a)
airports_delay <-
  flights %>%
  group_by(dest) %>%
  summarize(delay = mean(arr_delay, na.rm = TRUE)) %>%
  filter(!is.na(delay)) %>%
  ungroup()

airports %>%
  inner_join(airports_delay, c("faa" = "dest")) %>%
  ggplot(aes(lon, lat)) +
  borders("state") +
  geom_point(aes(color = delay)) +
  scale_color_gradient(low = "green", high = "red") +
  ggplot2::coord_quickmap()

# (b)
flights_dt <-
  flights %>%
  inner_join(select(airports, faa, lat, lon), c("dest" = "faa"))

# (c)
flights_with_age_dt <-
  select(flights, tailnum, year, delay = arr_delay) %>%
  left_join(select(planes, tailnum, manu.year = year), "tailnum") %>%
  mutate(age = year - manu.year) %>%
  filter(!is.na(age) & !is.na(delay))

ggplot(flights_with_age_dt, aes(age, delay)) +
  geom_point(aes(alpha = delay))

# (d)
flights_20130613_dt <-
  flights %>%
  filter(year == 2013 & month == 6 & day == 13) %>%
  group_by(dest) %>%
  summarize(delay = mean(arr_delay, na.rm = TRUE)) %>%
  filter(!is.na(delay)) %>%
  ungroup()

airports %>%
  inner_join(flights_20130613_dt, c("faa" = "dest")) %>%
  ggplot(aes(lon, lat)) +
  borders("state") +
  geom_point(aes(color = delay)) +
  scale_color_gradient(low = "green", high = "red") +
  ggplot2::coord_quickmap()

# we can get weather of June 13, 2013
weather_20130613_dt <-
  weather %>%
  filter(year == 2013 & month == 6 & day == 13)

# we found that on that day the humidity is high,
# so maybe there is a heavr rain, which makes a 
# high delay of airplanes
