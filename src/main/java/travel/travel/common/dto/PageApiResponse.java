package travel.travel.common.dto;

import lombok.*;

import java.util.List;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageApiResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
    private boolean first;
    private boolean last;
}

