package com.atguigu.tingshu.album.mapper;

import com.atguigu.tingshu.model.album.AlbumInfo;
import com.atguigu.tingshu.query.album.AlbumInfoQuery;
import com.atguigu.tingshu.vo.album.AlbumListVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AlbumInfoMapper extends BaseMapper<AlbumInfo> {

    /**
     * 查看专辑列表
     * @param albumListVoPage 分页数据 page,limit 不需要添加注解, 执行sql 语句的时候，会自动将分页数据添加在sql语句后面！因为有分页插件！
     * @param userId          用户Id
     * @param albumInfoQuery  查询条件
     * @return
     */
    IPage<AlbumListVo> selectUserAlbumPage(Page<AlbumListVo> albumListVoPage, @Param("userId") Long userId, @Param("vo") AlbumInfoQuery albumInfoQuery);
    /**
     * 查看专辑列表
     * @param albumListVoPage 分页数据 page,limit 不需要添加注解, 执行sql 语句的时候，会自动将分页数据添加在sql语句后面！因为有分页插件！
     * @param albumInfoQuery  查询条件
     * @return
     */
    IPage<AlbumListVo> selectUserAlbumPage(Page<AlbumListVo> albumListVoPage, @Param("vo") AlbumInfoQuery albumInfoQuery);
}


