package com.atguigu.tingshu.album.api;

import com.atguigu.tingshu.album.service.AlbumInfoService;
import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.common.util.AuthContextHolder;
import com.atguigu.tingshu.query.album.AlbumInfoQuery;
import com.atguigu.tingshu.vo.album.AlbumInfoVo;
import com.atguigu.tingshu.vo.album.AlbumListVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@Tag(name = "专辑管理")
@RestController
@RequestMapping("api/album/albumInfo")
@SuppressWarnings({"unchecked", "rawtypes"})
public class AlbumInfoApiController {

	@Autowired
	private AlbumInfoService albumInfoService;


	@Operation(summary = "保存专辑")
	@PostMapping("/saveAlbumInfo")
	public Result saveAlbumInfo (@RequestBody @Validated AlbumInfoVo albumInfoVo){
		// 如果登录成功后,会将用户id存储到一个方法中,使用getUserdId()获取用户id
		Long userId = AuthContextHolder.getUserId();
		//调用服务层方法
		albumInfoService.saveAlbumInfo(albumInfoVo,userId);
		//返回数据
		return Result.ok();
	}

	@Operation(summary = "查看专辑列表")
	@PostMapping("/findUserAlbumPage/{page}/{limit}")

	public Result findUserAlbumPage(@PathVariable Long page,
									@PathVariable Long limit,
									@RequestBody AlbumInfoQuery albumInfoQuery){
		//	获取用户Id
		Long userId = AuthContextHolder.getUserId()==null?1l:AuthContextHolder.getUserId();
		//	将用户Id 赋值给 albumInfoQuery 对象
		albumInfoQuery.setUserId(userId);
		//	创建分页对象
		Page<AlbumListVo> albumListVoPage = new Page<>(page,limit);
		//	调用服务层方法.
		//	IPage<AlbumListVo> iPage = this.albumInfoService.findUserAlbumPage(albumListVoPage,userId,albumInfoQuery);
		IPage<AlbumListVo> iPage = albumInfoService.findUserAlbumPage(albumListVoPage,albumInfoQuery);
		//	返回数据
		return Result.ok(iPage);
	}

	/**
	 * 删除专辑
	 * @param albumId
	 * @return
	 */
	//	http://127.0.0.1/api/album/albumInfo/removeAlbumInfo/1604
	@Operation(summary = "删除专辑")
	@DeleteMapping("/removeAlbumInfo/{albumId}")
	public Result removeAlbumInfoById(@PathVariable Long albumId) {
		//	调用服务层方法。
		this.albumInfoService.removeAlbumInfoById(albumId);
		//	返回数据
		return Result.ok();

	}
}

