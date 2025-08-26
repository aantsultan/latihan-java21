package id.latihan.java21.restfulapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebResponse<T> {

    private T data;
    private String errors;
    private PagingDto paging;

}
