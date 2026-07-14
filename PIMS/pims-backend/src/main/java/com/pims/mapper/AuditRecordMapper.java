package com.pims.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pims.entity.AuditRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 入住审核Mapper
 */
@Mapper
public interface AuditRecordMapper extends BaseMapper<AuditRecord> {
}
