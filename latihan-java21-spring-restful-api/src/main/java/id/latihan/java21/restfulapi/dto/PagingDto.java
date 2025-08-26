package id.latihan.java21.restfulapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagingDto {

    private int currentPage;
    private int totalPage;
    private int size;

}
