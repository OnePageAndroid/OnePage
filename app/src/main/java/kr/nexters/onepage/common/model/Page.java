package kr.nexters.onepage.common.model;

import com.google.common.collect.Lists;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Page {
    private Long pageId;
    private String locationName;
    private String locationEngName;
    private String locationAddress;
    private String email;
    private String content;
    private List<PageImage> images = Lists.newArrayList();
    private int pageNum;
    private int pageIndex;
    private PageDate createdAt;

    public static Page of(int resId, String content) {
        return Page.builder()
                .pageId(1L)
                .content(content)
                .images(Lists.newArrayList(PageImage.of(resId))).build();
    }

    public String getFirstImageUrl() {
        if (images.size() > 0 && images.get(0) != null) {
            return images.get(0).getUrl();
        }
        return "";
    }
}
