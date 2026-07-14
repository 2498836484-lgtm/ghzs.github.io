package com.pims.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pims.entity.Resident;
import org.apache.ibatis.annotations.Mapper;

/**
 * 住户信息Mapper
 */
@Mapper
public interface ResidentMapper extends BaseMapper<Resident> {
}
