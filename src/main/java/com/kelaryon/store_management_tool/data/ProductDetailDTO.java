package com.kelaryon.store_management_tool.data;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ProductDetailDTO {

    private Long id;
    private String detailName;
    private String detail;
}
