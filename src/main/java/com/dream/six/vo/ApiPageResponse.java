package com.dream.six.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ApiPageResponse<T> {

    private T totalContent;

    private long totalCount;

    private  String message;

}