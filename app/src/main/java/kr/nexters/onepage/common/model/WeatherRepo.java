package kr.nexters.onepage.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import kr.nexters.onepage.common.NetworkManager;

public class WeatherRepo {

    @SerializedName("result")
    Result result;
    @SerializedName("weather")
    Weather weather;

    public class Result {
        @SerializedName("message")
        String message;
        @SerializedName("code")
        String code;

        public String getMessage() {
            return message;
        }

        public String getCode() {
            return code;
        }
    }

    public class Weather {

        public List<Hourly> hourly = new ArrayList<>();

        public List<Hourly> getHourly() {
            return hourly;
        }

        public class Hourly {
            @SerializedName("sky")
            Sky sky;
            @SerializedName("precipitation")
            Precipitation precipitation;
            @SerializedName("temperature")
            Temperature temperature;
            @SerializedName("wind")
            Wind wind;

            public class Sky {
                @SerializedName("name")
                String name;
                @SerializedName("code")
                String code;

                public String getName() {
                    return name;
                }

                public String getCode() {
                    return code;
                }
            }

            public class Precipitation { // 강수 정보
                @SerializedName("sinceOntime")
                String sinceOntime; // 강우
                @SerializedName("type")
                String type; //0 :없음 1:비 2: 비/눈 3: 눈

                public String getSinceOntime() {
                    return sinceOntime;
                }

                public String getType() {
                    return type;
                }
            }

            public class Temperature {
                @SerializedName("tc")
                String tc; // 현재 기온

                public String getTc() {
                    return tc;
                }
            }

            public class Wind { // 바람
                @SerializedName("wdir")
                String wdir;
                @SerializedName("wspd")
                String wspd;

                public String getWdir() {
                    return wdir;
                }

                public String getWspd() {
                    return wspd;
                }
            }

            public Sky getSky() {
                return sky;
            }

            public Precipitation getPrecipitation() {
                return precipitation;
            }

            public Temperature getTemperature() {
                return temperature;
            }

            public Wind getWind() {
                return wind;
            }
        }
    }

    public Result getResult() {
        return result;
    }

    public Weather getWeather() {
        return weather;
    }


    public static Flowable<Weather.Hourly.Sky> getSky() {
        return NetworkManager.getInstance().getWeatherApi()
                .getWeather(1, "37.5714000000", "126.9658000000")
                .map(res -> res.getWeather().getHourly().get(0).getSky());
    }
}