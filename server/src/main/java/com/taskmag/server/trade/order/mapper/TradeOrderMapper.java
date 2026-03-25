package com.taskmag.server.trade.order.mapper;

import com.taskmag.server.trade.order.vo.OrderDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface TradeOrderMapper {
    List<OrderDetailVO> selectPage(@Param("offset") long offset, @Param("pageSize") int pageSize, @Param("keyword") String keyword);

    Long count(@Param("keyword") String keyword);

    OrderDetailVO selectById(@Param("id") String id);

    int updateRemark(@Param("id") String id, @Param("remark") String remark);

    int updatePrice(@Param("id") String id, @Param("price") BigDecimal price);

    int updateAddress(@Param("id") String id, @Param("address") String address);
}
