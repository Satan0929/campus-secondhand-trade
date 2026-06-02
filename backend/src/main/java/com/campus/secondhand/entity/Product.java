package com.campus.secondhand.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long productId;

    private Long sellerId;

    private String title;

    private String description;

    private BigDecimal price;

    private String category;

    private Integer status;

    private Integer stock;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
