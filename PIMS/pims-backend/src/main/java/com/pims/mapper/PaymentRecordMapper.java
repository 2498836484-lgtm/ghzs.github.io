package com.pims.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pims.entity.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {
    
}
