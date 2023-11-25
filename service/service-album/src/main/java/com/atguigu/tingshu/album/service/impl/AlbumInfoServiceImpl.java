package com.atguigu.tingshu.album.service.impl;

import com.atguigu.tingshu.album.mapper.AlbumAttributeValueMapper;
import com.atguigu.tingshu.album.mapper.AlbumInfoMapper;
import com.atguigu.tingshu.album.mapper.AlbumStatMapper;
import com.atguigu.tingshu.album.service.AlbumAttributeValueService;
import com.atguigu.tingshu.album.service.AlbumInfoService;
import com.atguigu.tingshu.common.constant.SystemConstant;
import com.atguigu.tingshu.model.album.AlbumAttributeValue;
import com.atguigu.tingshu.model.album.AlbumInfo;
import com.atguigu.tingshu.model.album.AlbumStat;
import com.atguigu.tingshu.query.album.AlbumInfoQuery;
import com.atguigu.tingshu.vo.album.AlbumAttributeValueVo;
import com.atguigu.tingshu.vo.album.AlbumInfoVo;
import com.atguigu.tingshu.vo.album.AlbumListVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class AlbumInfoServiceImpl extends ServiceImpl<AlbumInfoMapper, AlbumInfo> implements AlbumInfoService {

	@Autowired
	private AlbumInfoMapper albumInfoMapper;

	@Autowired
	private AlbumStatMapper albumStatMapper;

	@Autowired
	private AlbumAttributeValueService albumAttributeValueService;

	@Autowired
	private AlbumAttributeValueMapper albumAttributeValueMapper;




	@Override
	public IPage<AlbumListVo> findUserAlbumPage(Page<AlbumListVo> albumListVoPage, AlbumInfoQuery albumInfoQuery) {
		//	返回数据
		return albumInfoMapper.selectUserAlbumPage(albumListVoPage,albumInfoQuery);
	}

	@Override
	public void removeAlbumInfoById(Long albumId) {
		//	album_info 表； album_stat 统计信息 album_attribute_value:
		albumInfoMapper.deleteById(albumId);
		//	this=AlbumInfoService albumInfoService = new AlbumInfoServiceImpl();
		//	this.removeById(albumId);
		//	删除对应的专辑统计信息，不能使用ById!
		//	Wrapper ： 就是构建sql 语句中的条件!
		//	delete from album_stat where album_id = ? 物理删除： 逻辑删除本质是update 语句
		//	QueryWrapper:
		//  QueryWrapper<AlbumStat> wrapper = new QueryWrapper<>();
		//  wrapper.eq("album_id",albumId);
		LambdaQueryWrapper<AlbumStat> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(AlbumStat::getAlbumId,albumId);
		albumStatMapper.delete(wrapper);

		//	删除数据：album_attribute_value
		LambdaQueryWrapper<AlbumAttributeValue> wrapper1 = new LambdaQueryWrapper<>();
		wrapper1.eq(AlbumAttributeValue::getAlbumId,albumId);
		albumAttributeValueMapper.delete(wrapper1);
	}

	@Override
	public IPage<AlbumListVo> findUserAlbumPage(Page<AlbumListVo> albumListVoPage, Long userId, AlbumInfoQuery albumInfoQuery) {
		//	返回数据
		return albumInfoMapper.selectUserAlbumPage(albumListVoPage,userId,albumInfoQuery);
	}





	@Override
	@Transactional(rollbackFor = Exception.class) // 本地事务-声明式事务 底层使用的思想-aop 反射
	public void saveAlbumInfo(AlbumInfoVo albumInfoVo, Long userId) {
		//	album_info
		//	声明对象
		AlbumInfo albumInfo = new AlbumInfo();
		//	赋值：
		//	albumInfo.setUserId(userId);
		//	给个默认值1
		albumInfo.setUserId(1l);
		//   属性拷贝
		BeanUtils.copyProperties(albumInfoVo,albumInfo);
		//	表示默认审核通过
		albumInfo.setStatus(SystemConstant.ALBUM_STATUS_PASS);
		// 判断当前专辑是否付费
		 if (!albumInfo.getPayType().equals(SystemConstant.ALBUM_PAY_TYPE_FREE)){
			 //	设置前5集免费
			 albumInfo.setTracksForFree(5);
		 }
		//	保存专辑
		albumInfoMapper.insert(albumInfo);
		//	打印数据
		System.out.println("list:"+albumInfo.getAlbumAttributeValueVoList());
		//	获取到属性集合 album_attribute_value
		List<AlbumAttributeValueVo> albumAttributeValueVoList = albumInfoVo.getAlbumAttributeValueVoList();
		if (!CollectionUtils.isEmpty(albumAttributeValueVoList)){
			//	循环遍历
			//			albumAttributeValueVoList.forEach(albumAttributeValueVo -> {
			//				//	创建属性标签对象
			//				AlbumAttributeValue albumAttributeValue = new AlbumAttributeValue();
			//				//	赋值：album_id 前端没有传递
			//				//	因为在这段代码之前已经执行了insert 语句。  @TableId(type = IdType.AUTO)
			//				//	因为这个IdType.AUTO 就能够获取到当前的主键Id 了
			//				albumAttributeValue.setAlbumId(albumInfo.getId());
			//				BeanUtils.copyProperties(albumAttributeValueVo,albumAttributeValue);
			//				//	保存数据 执行5条 insert 语句
			//				albumAttributeValueMapper.insert(albumAttributeValue);
			//			});

			List<AlbumAttributeValue> albumAttributeValueList = albumAttributeValueVoList.stream().map(albumAttributeValueVo -> {
				//	创建属性标签对象
				AlbumAttributeValue albumAttributeValue = new AlbumAttributeValue();
				//	赋值：album_id 前端没有传递
				//	因为在这段代码之前已经执行了insert 语句。  @TableId(type = IdType.AUTO)
				//	因为这个IdType.AUTO 就能够获取到当前的主键Id 了
				albumAttributeValue.setAlbumId(albumInfo.getId());
				BeanUtils.copyProperties(albumAttributeValueVo, albumAttributeValue);
				//	返回
				return albumAttributeValue;
			}).collect(Collectors.toList());

			//	批量插入数据：insert into album_attribute_value values(?,?,?)
			albumAttributeValueService.saveBatch(albumAttributeValueList);
		}
		//	album_stat 统计信息初始化
		this.saveAlbumStat(albumInfo.getId(),SystemConstant.ALBUM_STAT_PLAY);
		this.saveAlbumStat(albumInfo.getId(),SystemConstant.ALBUM_STAT_SUBSCRIBE);
		this.saveAlbumStat(albumInfo.getId(),SystemConstant.ALBUM_STAT_BROWSE);
		this.saveAlbumStat(albumInfo.getId(),SystemConstant.ALBUM_STAT_COMMENT);


	}

	/**
	 * 保存专辑统计信息
	 * @param albumId
	 * @param statType
	 */
	private void saveAlbumStat(Long albumId, String statType) {
		//	创建实体类
		AlbumStat albumStat = new AlbumStat();
		//	赋值
		albumStat.setAlbumId(albumId);
		albumStat.setStatType(statType);
		albumStat.setStatNum(0);
		//	保存信息
		albumStatMapper.insert(albumStat);
	}

}
