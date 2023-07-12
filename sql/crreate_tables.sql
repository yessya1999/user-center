create table user
(
    id           bigint auto_increment
        primary key,
    username     varchar(256)                       null comment '昵称',
    userAccount  varchar(256)                       null comment '登录账号',
    avatarUrl    varchar(512)                       null comment '头像',
    gender       tinyint                            null comment '性别',
    userPassword varchar(256)                       not null comment '密码',
    phone        varchar(256)                       null comment '电话',
    userStatus   int      default 0                 null comment '用户状态 0-正常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '默认创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除 0-未删除',
    userRole     int      default 0                 not null comment '用户角色
0-普通用户
1-管理员',
    planetCode   varchar(512)                       not null comment '星球编号'
);
