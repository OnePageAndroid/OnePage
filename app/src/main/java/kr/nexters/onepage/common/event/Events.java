package kr.nexters.onepage.common.event;

/**
 * Created by OhJaeHwan on 2017-01-29.
 */

public class Events {
    private Events() {}

    public static class ToolbarPageNumEvent {
        private int totalPageNum;

        public ToolbarPageNumEvent(int totalPageNum) {
            this.totalPageNum = totalPageNum;
        }

        public int getTotalPageNum() {
            return totalPageNum;
        }
    }
}
