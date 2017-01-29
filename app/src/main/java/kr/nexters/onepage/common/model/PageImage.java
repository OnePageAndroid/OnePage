package kr.nexters.onepage.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageImage {
    private Long pageId;
    private Long locationId;
    private String url;
    private String name;
    private int resourceId;

    public static PageImage of(int resId) {
        return PageImage.builder()
                .resourceId(resId).build();
    }
}
