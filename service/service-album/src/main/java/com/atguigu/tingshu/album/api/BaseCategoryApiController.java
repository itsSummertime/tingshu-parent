package com.atguigu.tingshu.album.api;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.tingshu.album.service.BaseCategoryService;
import com.atguigu.tingshu.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Tag(name = "分类管理")
@RestController
@RequestMapping(value="/api/album/category")
@SuppressWarnings({"unchecked", "rawtypes"})
public class BaseCategoryApiController {
	
	@Autowired
	private BaseCategoryService baseCategoryService;

	/**
	 * 获取专辑分类数据
	 * @return
	 */

	@Operation(summary = "获取专辑分类数据")
	@GetMapping("getBaseCategoryList")
	public Result getBaseCategoryList(){
		//调用服务层方法
		//	class{ categoryName categoryId } setCategoryName("图书") getCategoryName()  与 map put("categoryName","图书") get("categoryName")可以互换！
		//	JSONObject extends JSON implements Map<String, Object>
		List<JSONObject> list = baseCategoryService.getCategoryList();
		//返回数据
		return Result.ok(list);
	}

}

