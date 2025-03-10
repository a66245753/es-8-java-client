package com.david.mapper;

import com.david.domain.SysDistrict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.PostMapping;

/**
* @author 58333
* @description 针对表【sys_district(（公共）区域数据)】的数据库操作Mapper
* @createDate 2025-03-08 15:55:13
* @Entity com.david.domain.SysDistrict
*/
@Mapper
public interface SysDistrictMapper extends BaseMapper<SysDistrict> {

}




