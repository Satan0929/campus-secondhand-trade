package com.campus.secondhand.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long userId;

    private String username;

    private String passwordHash;

    private String phone;

    private String avatar;

    private Integer role;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
